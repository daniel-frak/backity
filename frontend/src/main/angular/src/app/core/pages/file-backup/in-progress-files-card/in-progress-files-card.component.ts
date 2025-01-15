import {Component, OnDestroy, OnInit} from '@angular/core';
import {CardComponent} from "@app/shared/components/card/card.component";
import {DatePipe, NgIf, NgStyle} from "@angular/common";
import {TableColumnDirective} from "@app/shared/components/table/column-directive/table-column.directive";
import {TableComponent} from "@app/shared/components/table/table.component";
import {
  FileBackupMessageTopics,
  FileBackupProgressUpdatedEvent,
  FileBackupStartedEvent,
  FileBackupStatus,
  FileBackupStatusChangedEvent,
  GameFile,
  GameFilesClient
} from "@backend";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {Message} from "@stomp/stompjs";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {firstValueFrom, Subscription} from "rxjs";

@Component({
  selector: 'app-in-progress-files-card',
  standalone: true,
  imports: [
    CardComponent,
    DatePipe,
    NgIf,
    TableColumnDirective,
    TableComponent,
    NgStyle
  ],
  templateUrl: './in-progress-files-card.component.html',
  styleUrl: './in-progress-files-card.component.scss'
})
export class InProgressFilesCardComponent implements OnInit, OnDestroy {

  currentDownloadIsLoading: boolean = false;
  currentDownload?: GameFile;
  downloadProgress?: FileBackupProgressUpdatedEvent;

  private readonly subscriptions: Subscription[] = [];

  constructor(private readonly gameFilesClient: GameFilesClient,
              private readonly messageService: MessagesService,
              private readonly notificationService: NotificationService) {
  }

  asGameFile = (gameFile: GameFile) => gameFile;

  ngOnInit(): void {
    this.subscriptions.push(
      this.messageService.watch(FileBackupMessageTopics.Started)
        .subscribe(p => this.onBackupStarted(p)),
      this.messageService.watch(FileBackupMessageTopics.ProgressChanged)
        .subscribe(p => this.onProgressChanged(p)),
      this.messageService.watch(FileBackupMessageTopics.StatusChanged)
        .subscribe(p => this.onStatusChanged(p))
    )

    this.refreshCurrentlyDownloaded();
  }

  private onBackupStarted(payload: Message) {
    const event: FileBackupStartedEvent = JSON.parse(payload.body);
    this.currentDownload = {
      id: event.gameFileId,
      gameId: 'UNKNOWN',
      gameProviderFile: {
        originalGameTitle: event.originalGameTitle,
        gameProviderId: 'UNKNOWN',
        fileTitle: event.fileTitle,
        version: event.version,
        url: 'UNKNOWN',
        originalFileName: event.originalFileName,
        size: event.size,
      },
      fileBackup: {
        filePath: event.filePath,
        status: FileBackupStatus.InProgress
      }
    }
  }

  private onProgressChanged(payload: Message) {
    this.downloadProgress = JSON.parse(payload.body);
  }

  private onStatusChanged(payload: Message) {
    const event: FileBackupStatusChangedEvent = JSON.parse(payload.body);
    if (event.gameFileId != this.currentDownload?.id) {
      return;
    }
    if (event.newStatus == FileBackupStatus.Success || event.newStatus == FileBackupStatus.Failed) {
      this.currentDownload = undefined;
    }
  }

  async refreshCurrentlyDownloaded(): Promise<void> {
    this.currentDownloadIsLoading = true;

    try {
      const gameFile = await firstValueFrom(this.gameFilesClient.getCurrentlyDownloading());
      if (!gameFile) {
        this.currentDownload = undefined;
      } else {
        this.currentDownload = gameFile;
      }
    } catch (error) {
      this.notificationService.showFailure('Error fetching currently downloaded file', error);
    } finally {
      this.currentDownloadIsLoading = false;
    }
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(s => s.unsubscribe());
  }
}
