import {Component, OnDestroy, OnInit} from '@angular/core';
import {
  FileBackupMessageTopics,
  FileBackupProgressUpdatedEvent,
  FileBackupStartedEvent,
  FileBackupStatus,
  FileBackupStatusChangedEvent,
  GameFile,
  GameFileProcessingStatus,
  GameFilesClient,
  PageGameFile
} from "@backend";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {StompSubscription} from "@stomp/stompjs/esm6/stomp-subscription";
import {IMessage} from "@stomp/stompjs";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {firstValueFrom} from "rxjs";

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
              private readonly messageService: MessagesService,
              private readonly notificationService: NotificationService) {
  }

  asGameFile = (gameFile: GameFile) => gameFile;
  asBackupStartedEvent = (event: FileBackupStartedEvent) => event;

  ngOnInit(): void {
    this.messageService.onConnect(client => this.stompSubscriptions.push(
      client.subscribe(FileBackupMessageTopics.Started, p => this.onBackupStarted(p)),
      client.subscribe(FileBackupMessageTopics.ProgressUpdate, p => this.onProgressUpdate(p)),
      client.subscribe(FileBackupMessageTopics.StatusChanged, p => this.onStatusChanged(p))
    ))

    this.refresh()();
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
    if (event.newStatus == FileBackupStatus.Success || event.newStatus == FileBackupStatus.Failed) {
      this.currentDownload = undefined;
    }
  }

  refresh(): () => Promise<void> {
    return async () => {
      this.filesAreLoading = true;
      const page = 0;
      const size = this.pageSize;

      try {
        this.enqueuedDownloads = await firstValueFrom(
          this.gameFilesClient.getGameFiles(GameFileProcessingStatus.Enqueued, {page, size}));
        this.processedFiles = await firstValueFrom(
          this.gameFilesClient.getGameFiles(GameFileProcessingStatus.Processed, {page, size}));
        const gameFile = await firstValueFrom(this.gameFilesClient.getCurrentlyDownloading());
        if (!gameFile) {
          this.currentDownload = undefined;
        } else {
          this.currentDownload = {
            gameFileId: gameFile.id,
            originalGameTitle: gameFile.gameProviderFile.originalGameTitle,
            fileTitle: gameFile.gameProviderFile.fileTitle,
            version: gameFile.gameProviderFile.version,
            originalFileName: gameFile.gameProviderFile.originalFileName,
            size: gameFile.gameProviderFile.size,
            filePath: gameFile.fileBackup.filePath
          };
        }
      } catch (error) {
        this.notificationService.showFailure('Error during refresh', undefined, error);
      } finally {
        this.filesAreLoading = false;
      }
    }
  }

  removeFromQueue(fileId?: string): () => Promise<void> {
    return async () => {
      this.notificationService.showFailure('Removing from queue not yet implemented');
    }
  }

  ngOnDestroy(): void {
    this.stompSubscriptions.forEach(s => s.unsubscribe());
  }
}
