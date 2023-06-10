import {Component, OnDestroy, OnInit} from '@angular/core';
import {
  BackupsClient,
  FileBackupMessageTopics,
  FileBackupProgress,
  FileBackupStatus,
  GameFileVersion,
  PageGameFileVersion
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

  enqueuedDownloads?: PageGameFileVersion;
  processedFiles?: PageGameFileVersion;
  currentDownload?: GameFileVersion;
  downloadProgress?: FileBackupProgress;
  filesAreLoading: boolean = false;
  FileBackupStatus = FileBackupStatus;

  private pageSize = 20;
  private readonly stompSubscriptions: StompSubscription[] = [];

  constructor(private readonly backupsClient: BackupsClient,
              private readonly messageService: MessagesService) {
  }

  asFile = (file: GameFileVersion) => file;

  ngOnInit(): void {
    this.messageService.onConnect(client => this.stompSubscriptions.push(
      client.subscribe(FileBackupMessageTopics.Started, p => this.onDownloadStartedReceived(p)),
      client.subscribe(FileBackupMessageTopics.Progress, p => this.onDownloadProgressReceived(p)),
      client.subscribe(FileBackupMessageTopics.Finished, () => this.onDownloadFinishedReceived())
    ))

    this.refresh();
  }

  private onDownloadStartedReceived(payload: IMessage) {
    this.currentDownload = JSON.parse(payload.body);
  }

  private onDownloadProgressReceived(payload: IMessage) {
    this.downloadProgress = JSON.parse(payload.body);
  }

  private onDownloadFinishedReceived() {
    this.currentDownload = undefined;
  }

  refresh() {
    this.filesAreLoading = true;
    const page = 0;
    const size = this.pageSize;
    const sort = ["dateCreated,desc"];

    this.backupsClient.getQueueItems(page, size, sort)
      .subscribe(d => {
        this.enqueuedDownloads = d;

        this.backupsClient.getProcessedFiles()
          .subscribe(f => {
            this.processedFiles = f;
            this.filesAreLoading = false;
          })
      });

    this.backupsClient.getCurrentlyProcessing()
      .subscribe(d => this.currentDownload = d);
  }

  removeFromQueue(fileId?: number) {
    console.error("Removing from queue not yet implemented");
  }

  ngOnDestroy(): void {
    this.stompSubscriptions.forEach(s => s.unsubscribe());
  }
}
