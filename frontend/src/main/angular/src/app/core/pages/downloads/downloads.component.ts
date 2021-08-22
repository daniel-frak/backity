import {Component, OnDestroy, OnInit} from '@angular/core';
import {
  DownloadsClient,
  DownloadStatus,
  EnqueuedFileDownload,
  FileDownloadMessageTopics,
  PageEnqueuedFileDownload
} from "@backend";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {StompSubscription} from "@stomp/stompjs/esm6/stomp-subscription";
import {IMessage} from "@stomp/stompjs";

@Component({
  selector: 'app-downloads',
  templateUrl: './downloads.component.html',
  styleUrls: ['./downloads.component.scss']
})
export class DownloadsComponent implements OnInit, OnDestroy {

  enqueuedDownloads?: PageEnqueuedFileDownload;
  processedFiles?: PageEnqueuedFileDownload;
  currentDownload?: EnqueuedFileDownload;
  filesAreLoading: boolean = false;
  DownloadStatus = DownloadStatus;

  private pageSize = 20;
  private stompSubscriptions: StompSubscription[] = [];

  constructor(private readonly downloadsClient: DownloadsClient,
              private readonly messageService: MessagesService) {
  }

  asFile = (file: EnqueuedFileDownload) => file;

  ngOnInit(): void {
    this.messageService.onConnect(client => this.stompSubscriptions.push(
      client.subscribe(FileDownloadMessageTopics.Started, p => this.onDownloadStartedReceived(p)),
      client.subscribe(FileDownloadMessageTopics.Finished, () => this.onDownloadFinishedReceived())
    ))

    this.refresh();
  }

  private onDownloadStartedReceived(payload: IMessage) {
    this.currentDownload = JSON.parse(payload.body);
  }

  private onDownloadFinishedReceived() {
    this.currentDownload = undefined;
  }

  refresh() {
    this.filesAreLoading = true;
    const page = 0;
    const size = this.pageSize;
    const sort = ["dateCreated,desc"];

    this.downloadsClient.getQueueItems(page, size, sort)
      .subscribe(d => {
        this.enqueuedDownloads = d;

        this.downloadsClient.getProcessedFiles()
          .subscribe(f => {
            this.processedFiles = f;
            this.filesAreLoading = false;
          })
      });

    this.downloadsClient.getCurrentlyDownloading()
      .subscribe(d => this.currentDownload = d);
  }

  removeFromQueue(fileId?: number) {
    console.warn("Removing from queue not yet implemented");
  }

  ngOnDestroy(): void {
    this.stompSubscriptions.forEach(s => s.unsubscribe());
  }
}
