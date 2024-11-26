import {Component, OnDestroy, OnInit} from '@angular/core';
import {
  FileBackupMessageTopics,
  FileBackupProgressUpdatedEvent,
  FileBackupStartedEvent,
  FileBackupStatus,
  FileBackupStatusChangedEvent,
  GameFile, GameFileProcessingStatus,
  GameFilesClient,
  PageGameFile
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

  enqueuedDownloads?: PageGameFile;
  processedFiles?: PageGameFile;
  currentDownload?: FileBackupStartedEvent;
  downloadProgress?: FileBackupProgressUpdatedEvent;
  filesAreLoading: boolean = false;
  FileBackupStatus = FileBackupStatus;

  private readonly pageSize = 20;
  private readonly stompSubscriptions: StompSubscription[] = [];

  constructor(private readonly gameFilesClient: GameFilesClient,
              private readonly messageService: MessagesService) {
  }

  asGameFile = (gameFile: GameFile) => gameFile;
  asBackupStartedEvent = (event: FileBackupStartedEvent) => event;

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
    const event: FileBackupStatusChangedEvent = JSON.parse(payload.body);
    if (event.gameFileId != this.currentDownload?.gameFileId) {
      return;
    }
    if (event.newStatus == FileBackupStatus.Success || event.newStatus == FileBackupStatus.Failed)
    {
      this.currentDownload = undefined;
    }
  }

  refresh() {
    this.filesAreLoading = true;
    const page = 0;
    const size = this.pageSize;

    this.gameFilesClient.getGameFiles(GameFileProcessingStatus.Enqueued, {
      page: page,
      size: size
    })
      .subscribe((d: PageGameFile) => {
        this.enqueuedDownloads = d;

        this.gameFilesClient.getGameFiles(GameFileProcessingStatus.Processed, {
          page: page,
          size: size
        })
          .subscribe((f: PageGameFile) => {
            this.processedFiles = f;
            this.filesAreLoading = false;
          })
      });

    this.gameFilesClient.getCurrentlyDownloading()
      .subscribe((gameFile: GameFile) => {
        if (!gameFile) {
          this.currentDownload = undefined;
          return;
        }
        this.currentDownload = {
          gameFileId: gameFile.id,
          originalGameTitle: gameFile.gameProviderFile.originalGameTitle,
          fileTitle: gameFile.gameProviderFile.fileTitle,
          version: gameFile.gameProviderFile.version,
          originalFileName: gameFile.gameProviderFile.originalFileName,
          size: gameFile.gameProviderFile.size,
          filePath: gameFile.fileBackup.filePath
        };
      });
  }

  removeFromQueue(fileId?: string) {
    console.error("Removing from queue not yet implemented");
  }

  ngOnDestroy(): void {
    this.stompSubscriptions.forEach(s => s.unsubscribe());
  }
}
