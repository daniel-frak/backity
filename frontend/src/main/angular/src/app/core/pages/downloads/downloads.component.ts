import {Component, OnDestroy, OnInit} from '@angular/core';
import {DownloadsClient, EnqueuedFileDownload, PageEnqueuedFileDownload} from "@backend";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {StompSubscription} from "@stomp/stompjs/esm6/stomp-subscription";

@Component({
  selector: 'app-downloads',
  templateUrl: './downloads.component.html',
  styleUrls: ['./downloads.component.scss']
})
export class DownloadsComponent implements OnInit, OnDestroy {

  enqueuedDownloads?: PageEnqueuedFileDownload;
  currentDownload?: EnqueuedFileDownload;
  filesAreLoading: boolean = false;

  private pageSize = 20;
  private stompSubscriptions: StompSubscription[] = [];

  constructor(private readonly downloadsClient: DownloadsClient,
              private readonly messageService: MessagesService) {
  }

  asFile = (file: EnqueuedFileDownload) => file;

  ngOnInit(): void {
    this.refresh();
  }

  refresh() {
    this.filesAreLoading = true;
    const page = 0;
    const size = this.pageSize;
    const sort = ["dateCreated,desc"];

    this.downloadsClient.getQueueItems(page, size, sort)
      .subscribe(d => this.updateEnqueuedDownloads(d));
  }

  private updateEnqueuedDownloads(downloads: PageEnqueuedFileDownload) {
    this.enqueuedDownloads = downloads;
    this.filesAreLoading = false;
  }

  removeFromQueue(fileId?: number) {
    console.warn("Removing from queue not yet implemented");
  }

  ngOnDestroy(): void {
    this.stompSubscriptions.forEach(s => s.unsubscribe());
  }
}
