import {Component, OnDestroy, OnInit} from '@angular/core';
import {
  FileBackupMessageTopics,
  FileBackupProgressUpdateMessage,
  FileBackupStartedMessage,
  FileBackupStatus,
  FileBackupStatusChangedMessage,
  GameFileDetails,
  GameFileDetailsClient,
  PageHttpDtoGameFileDetails
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

  enqueuedDownloads?: PageHttpDtoGameFileDetails;
  processedFiles?: PageHttpDtoGameFileDetails;
  currentDownload?: FileBackupStartedMessage;
  downloadProgress?: FileBackupProgressUpdateMessage;
  filesAreLoading: boolean = false;
  FileBackupStatus = FileBackupStatus;

  private pageSize = 20;
  private readonly stompSubscriptions: StompSubscription[] = [];

  constructor(private readonly gameFileDetailsClient: GameFileDetailsClient,
              private readonly messageService: MessagesService) {
  }

  asFile = (file: GameFileDetails) => file;
  asBackupStartedMessage = (message: FileBackupStartedMessage) => message;

  ngOnInit(): void {
    this.messageService.onConnect(client => this.stompSubscriptions.push(
      client.subscribe(FileBackupMessageTopics.Started, p => this.onBackupStartedReceived(p)),
      client.subscribe(FileBackupMessageTopics.ProgressUpdate, p => this.onProgressUpdate(p)),
      client.subscribe(FileBackupMessageTopics.StatusChanged, p => this.onStatusChanged(p))
    ))

    this.refresh();
  }

  private onBackupStartedReceived(payload: IMessage) {
    this.currentDownload = JSON.parse(payload.body);
  }

  private onProgressUpdate(payload: IMessage) {
    this.downloadProgress = JSON.parse(payload.body);
  }

  private onStatusChanged(payload: IMessage) {
    const message: FileBackupStatusChangedMessage = JSON.parse(payload.body);
    if (message.gameFileDetailsId == this.currentDownload?.gameFileDetailsId &&
      (message.newStatus == FileBackupStatus.Success || message.newStatus == FileBackupStatus.Failed)) {
      this.currentDownload = undefined;
    }
  }

  refresh() {
    this.filesAreLoading = true;
    const page = 0;
    const size = this.pageSize;

    this.gameFileDetailsClient.getQueueItems({
      page: page,
      size: size
    })
      .subscribe(d => {
        this.enqueuedDownloads = d;

        this.gameFileDetailsClient.getProcessedFiles({
          page: 0,
          size: 20
        })
          .subscribe(f => {
            this.processedFiles = f;
            this.filesAreLoading = false;
          })
      });

    this.gameFileDetailsClient.getCurrentlyProcessing()
      .subscribe(d => {
        this.currentDownload = {
          gameFileDetailsId: d.id,
          originalGameTitle: d.sourceFileDetails?.originalGameTitle,
          fileTitle: d.sourceFileDetails?.fileTitle,
          version: d.sourceFileDetails?.version,
          originalFileName: d.sourceFileDetails?.originalFileName,
          size: d.sourceFileDetails?.size
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
