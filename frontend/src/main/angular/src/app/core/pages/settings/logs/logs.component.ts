import {Component, OnInit, signal} from '@angular/core';
import {LogCreatedEvent, LogsClient, LogsMessageTopics} from "@backend";
import {MessageService} from "@app/shared/backend/services/message.service";
import {PageHeaderComponent} from '@app/shared/components/page-header/page-header.component';

import {LoadedContentComponent} from '@app/shared/components/loaded-content/loaded-content.component';
import {SectionComponent} from "@app/shared/components/section/section.component";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";

@Component({
    selector: 'app-logs',
    templateUrl: './logs.component.html',
    styleUrl: './logs.component.scss',
    imports: [PageHeaderComponent, LoadedContentComponent, SectionComponent]
})
export class LogsComponent implements OnInit {

  logs = signal<string[]>([]);
  public logsAreLoading = signal(false);

  constructor(private readonly logsClient: LogsClient, private readonly messageService: MessageService) {
    this.messageService.watch<LogCreatedEvent>(LogsMessageTopics.TopicLogs)
      .pipe(takeUntilDestroyed())
      .subscribe(event => this.onLogReceived(event));
  }

  ngOnInit(): void {
    this.refresh();
  }

  private onLogReceived(event: LogCreatedEvent) {
    this.logs.update(logs => {
      const newLogs = [event.message, ...logs];
      if (newLogs.length > (event.maxLogs)) {
        newLogs.pop();
      }
      return newLogs;
    });
  }

  refresh() {
    if (this.logsAreLoading()) {
      return;
    }
    this.logsAreLoading.set(true);
    this.logsClient.getLogs()
      .subscribe(l => this.updateLogs(l));
  }

  private updateLogs(logs: Array<string>) {
    this.logs.set([...logs].reverse());
    this.logsAreLoading.set(false);
  }
}
