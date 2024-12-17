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
import {PaginationComponent} from "@app/shared/components/pagination/pagination.component";

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
    CardComponent,
    PaginationComponent
  ]
})
export class FileBackupComponent implements OnInit, OnDestroy {

  currentDownloadIsLoading: boolean = false;
  currentDownload?: FileBackupStartedEvent;
  downloadProgress?: FileBackupProgressUpdatedEvent;

  FileBackupStatus = FileBackupStatus;
  processedFilesAreLoading: boolean = false;
  processedFilePage?: PageGameFile;
  processedFilesPageNumber: number = 1;
  processedFilesPageSize: number = 3;

  enqueuedFilesAreLoading: boolean = false;
  enqueuedFilePage?: PageGameFile;
  enqueuedFilesPageNumber: number = 1;
  enqueuedFilesPageSize: number = 3;

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

    this.refreshCurrentlyDownloaded()();
  }

  private onBackupStarted(payload: Message) {
    const event: FileBackupStartedEvent = JSON.parse(payload.body);
    this.currentDownload = event;
    this.tryToRemoveFileFromEnqueuedDownloads(event);
  }

  private tryToRemoveFileFromEnqueuedDownloads(event: FileBackupStartedEvent) {
    const foundFile: GameFile | undefined = this.findFileInEnqueuedDownloads(event);
    if (foundFile) {
      const index: number | undefined = this.enqueuedFilePage?.content?.indexOf(foundFile);
      if (index !== -1) {
        this.enqueuedFilePage?.content?.splice(index!, 1);
      }
    }
  }

  private findFileInEnqueuedDownloads(event: FileBackupStartedEvent) {
    return this.enqueuedFilePage?.content
      ?.find(file => file?.id == event.gameFileId);
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

  refreshCurrentlyDownloaded(): () => Promise<void> {
    return async () => {
      this.currentDownloadIsLoading = true;

      try {
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
        this.notificationService.showFailure('Error fetching currently downloaded file', error);
      } finally {
        this.processedFilesAreLoading = false;
      }
    }
  }

  refreshEnqueuedFiles(): () => Promise<void> {
    return async () => {
      this.enqueuedFilesAreLoading = true;

      try {
        this.enqueuedFilePage = await firstValueFrom(
          this.gameFilesClient.getGameFiles(GameFileProcessingStatus.Enqueued, {
            page: this.enqueuedFilesPageNumber - 1,
            size: this.enqueuedFilesPageSize
          }));
      } catch (error) {
        this.notificationService.showFailure('Error fetching enqueued files', error);
      } finally {
        this.enqueuedFilesAreLoading = false;
      }
    }
  }

  refreshProcessedFiles(): () => Promise<void> {
    return async () => {
      this.processedFilesAreLoading = true;

      try {
        this.processedFilePage = await firstValueFrom(
          this.gameFilesClient.getGameFiles(GameFileProcessingStatus.Processed, {
            page: this.processedFilesPageNumber - 1,
            size: this.processedFilesPageSize
          }));
      } catch (error) {
        this.notificationService.showFailure('Error fetching processed files', error);
      } finally {
        this.processedFilesAreLoading = false;
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
