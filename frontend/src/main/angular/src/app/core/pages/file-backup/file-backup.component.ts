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
import {Message} from "@stomp/stompjs";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {firstValueFrom, Subscription} from "rxjs";
import {PageHeaderComponent} from '@app/shared/components/page-header/page-header.component';
import {CommonModule} from '@angular/common';
import {TableComponent} from '@app/shared/components/table/table.component';
import {TableColumnDirective} from '@app/shared/components/table/column-directive/table-column.directive';
import {ButtonComponent} from '@app/shared/components/button/button.component';
import {CardComponent} from "@app/shared/components/card/card.component";

@Component({
  selector: 'app-downloads',
  templateUrl: './file-backup.component.html',
  styleUrls: ['./file-backup.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    PageHeaderComponent,
    TableComponent,
    TableColumnDirective,
    ButtonComponent,
    CardComponent
  ]
})
export class FileBackupComponent implements OnInit, OnDestroy {

  enqueuedDownloads?: PageGameFile;
  processedFiles?: PageGameFile;
  currentDownload?: FileBackupStartedEvent;
  downloadProgress?: FileBackupProgressUpdatedEvent;
  filesAreLoading: boolean = false;
  FileBackupStatus = FileBackupStatus;

  private readonly pageSize = 20;
  private readonly subscriptions: Subscription[] = [];

  constructor(private readonly gameFilesClient: GameFilesClient,
              private readonly messageService: MessagesService,
              private readonly notificationService: NotificationService) {
  }

  asGameFile = (gameFile: GameFile) => gameFile;
  asBackupStartedEvent = (event: FileBackupStartedEvent) => event;

  ngOnInit(): void {
    this.subscriptions.push(
      this.messageService.watch(FileBackupMessageTopics.Started)
        .subscribe(p => this.onBackupStarted(p)),
      this.messageService.watch(FileBackupMessageTopics.ProgressUpdate)
        .subscribe(p => this.onProgressUpdate(p)),
      this.messageService.watch(FileBackupMessageTopics.StatusChanged)
        .subscribe(p => this.onStatusChanged(p))
    )

    this.refresh()();
  }

  private onBackupStarted(payload: Message) {
    this.currentDownload = JSON.parse(payload.body);
  }

  private onProgressUpdate(payload: Message) {
    this.downloadProgress = JSON.parse(payload.body);
  }

  private onStatusChanged(payload: Message) {
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
    this.subscriptions.forEach(s => s.unsubscribe());
  }
}
