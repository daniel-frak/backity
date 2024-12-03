import {Component, OnDestroy, OnInit} from '@angular/core';
import {LogCreatedEvent, LogsClient, LogsMessageTopics} from "@backend";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {Message} from "@stomp/stompjs";
import {PageHeaderComponent} from '@app/shared/components/page-header/page-header.component';
import {NgFor, NgIf} from '@angular/common';
import {LoadedContentComponent} from '@app/shared/components/loaded-content/loaded-content.component';
import {Subscription} from "rxjs";

@Component({
  selector: 'app-logs',
  templateUrl: './logs.component.html',
  styleUrls: ['./logs.component.scss'],
  standalone: true,
  imports: [PageHeaderComponent, NgIf, LoadedContentComponent, NgFor]
})
export class LogsComponent implements OnInit, OnDestroy {

  logs: string[] = [];
  public logsAreLoading: boolean = false;
  private readonly subscriptions: Subscription[] = [];

  constructor(private readonly logsClient: LogsClient, private readonly messageService: MessagesService) {
  }

  ngOnInit(): void {
    this.subscriptions.push(
      this.messageService.watch(LogsMessageTopics.TopicLogs).subscribe(p => this.onLogReceived(p))
    )

    this.refresh();
  }

  private onLogReceived(payload: Message) {
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
    this.subscriptions.forEach(s => s.unsubscribe());
  }
}
