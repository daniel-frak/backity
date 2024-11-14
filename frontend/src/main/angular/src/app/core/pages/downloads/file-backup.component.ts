import {Component, OnDestroy, OnInit} from '@angular/core';
import {
  FileBackupMessageTopics,
  FileBackupProgressUpdatedEvent,
  FileBackupStartedEvent,
  FileBackupStatus,
  FileBackupStatusChangedEvent,
  FileDetails,
  FileDetailsClient,
  PageFileDetails
} from "@backend";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {StompSubscription} from "@stomp/stompjs/esm6/stomp-subscription";
import {IMessage} from "@stomp/stompjs";

@Component({
  selector: 'app-downloads',
  templateUrl: './file-backup.component.html',
  styleUrls: ['./file-backup.component.scss']
})
export class FileBackupComponent implements OnInit, OnDestroy {

  enqueuedDownloads?: PageFileDetails;
  processedFiles?: PageFileDetails;
  currentDownload?: FileBackupStartedEvent;
  downloadProgress?: FileBackupProgressUpdatedEvent;
  filesAreLoading: boolean = false;
  FileBackupStatus = FileBackupStatus;

  private readonly pageSize = 20;
  private readonly stompSubscriptions: StompSubscription[] = [];

  constructor(private readonly fileDetailsClient: FileDetailsClient,
              private readonly messageService: MessagesService) {
  }

  asFile = (file: FileDetails) => file;
  asBackupStartedMessage = (message: FileBackupStartedEvent) => message;

  ngOnInit(): void {
    this.messageService.onConnect(client => this.stompSubscriptions.push(
      client.subscribe(FileBackupMessageTopics.Started, p => this.onBackupStarted(p)),
      client.subscribe(FileBackupMessageTopics.ProgressUpdate, p => this.onProgressUpdate(p)),
      client.subscribe(FileBackupMessageTopics.StatusChanged, p => this.onStatusChanged(p))
    ))

    this.refresh();
  }

  private onBackupStarted(payload: IMessage) {
    this.currentDownload = JSON.parse(payload.body);
  }

  private onProgressUpdate(payload: IMessage) {
    this.downloadProgress = JSON.parse(payload.body);
  }

  private onStatusChanged(payload: IMessage) {
    const message: FileBackupStatusChangedEvent = JSON.parse(payload.body);
    if (message.fileDetailsId != this.currentDownload?.fileDetailsId) {
      return;
    }
    if (message.newStatus == FileBackupStatus.Success || message.newStatus == FileBackupStatus.Failed)
    {
      this.currentDownload = undefined;
    }
  }

  refresh() {
    this.filesAreLoading = true;
    const page = 0;
    const size = this.pageSize;

    this.fileDetailsClient.getQueueItems({
      page: page,
      size: size
    })
      .subscribe((d: PageFileDetails) => {
        this.enqueuedDownloads = d;

        this.fileDetailsClient.getProcessedFiles({
          page: 0,
          size: 20
        })
          .subscribe((f: PageFileDetails) => {
            this.processedFiles = f;
            this.filesAreLoading = false;
          })
      });

    this.fileDetailsClient.getCurrentlyDownloading()
      .subscribe((d: FileDetails) => {
        this.currentDownload = {
          fileDetailsId: d.id,
          originalGameTitle: d.sourceFileDetails.originalGameTitle,
          fileTitle: d.sourceFileDetails.fileTitle,
          version: d.sourceFileDetails.version,
          originalFileName: d.sourceFileDetails.originalFileName,
          size: d.sourceFileDetails.size,
          filePath: d.backupDetails.filePath
        }
      });
  }

  removeFromQueue(fileId?: string) {
    console.error("Removing from queue not yet implemented");
  }

  ngOnDestroy(): void {
    this.stompSubscriptions.forEach(s => s.unsubscribe());
  }
}
