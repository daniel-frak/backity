import {Component, OnDestroy, OnInit} from '@angular/core';
import {LogCreatedEvent, LogsClient, LogsMessageTopics} from "@backend";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {StompSubscription} from "@stomp/stompjs/esm6/stomp-subscription";
import {IMessage} from "@stomp/stompjs";

@Component({
  selector: 'app-logs',
  templateUrl: './logs.component.html',
  styleUrls: ['./logs.component.scss']
})
export class LogsComponent implements OnInit, OnDestroy {

  logs: string[] = [];
  public logsAreLoading: boolean = false;
  private readonly stompSubscriptions: StompSubscription[] = [];

  constructor(private readonly logsClient: LogsClient, private readonly messageService: MessagesService) {
  }

  ngOnInit(): void {
    this.messageService.onConnect(client => this.stompSubscriptions.push(
      client.subscribe(LogsMessageTopics.TopicLogs, p => this.onLogReceived(p))
    ));

    this.refresh();
  }

  private onLogReceived(payload: IMessage) {
    const event: LogCreatedEvent = JSON.parse(payload.body);
    this.logs.unshift(event.message as string);
    if (this.logs.length > (event.maxLogs as number)) {
      this.logs.pop();
    }
  }

  refresh() {
    this.logsAreLoading = true;
    this.logsClient.getLogs()
      .subscribe(l => this.updateLogs(l));
  }

  private updateLogs(logs: Array<string>) {
    this.logs = [...logs].reverse();
    this.logsAreLoading = false;
  }

  ngOnDestroy(): void {
    this.stompSubscriptions.forEach(s => s.unsubscribe());
  }
}
