import {Component, OnDestroy, OnInit} from '@angular/core';
import {CardComponent} from "@app/shared/components/card/card.component";
import {DatePipe, NgIf, NgStyle} from "@angular/common";
import {TableColumnDirective} from "@app/shared/components/table/column-directive/table-column.directive";
import {TableComponent} from "@app/shared/components/table/table.component";
import {
  FileBackupMessageTopics,
  FileBackupStartedEvent,
  FileCopiesClient, FileCopy,
  FileCopyStatus,
  FileCopyStatusChangedEvent,
  FileCopyWithContext,
  FileDownloadProgressUpdatedEvent,
  GameFile
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
  currentDownload?: FileCopyWithContext;
  downloadProgress?: FileDownloadProgressUpdatedEvent;

  private readonly subscriptions: Subscription[] = [];

  constructor(private readonly fileCopiesClient: FileCopiesClient,
              private readonly messageService: MessagesService,
              private readonly notificationService: NotificationService) {
  }

  asFileCopyWithContext =
    (fileCopyWithContext: FileCopyWithContext) => fileCopyWithContext;

  ngOnInit(): void {
    this.subscriptions.push(
      this.messageService.watch(FileBackupMessageTopics.Started)
        .subscribe(p => this.onBackupStarted(p)),
      this.messageService.watch(FileBackupMessageTopics.ProgressUpdate)
        .subscribe(p => this.onProgressChanged(p)),
      this.messageService.watch(FileBackupMessageTopics.StatusChanged)
        .subscribe(p => this.onStatusChanged(p))
    )

    this.refreshCurrentlyDownloaded();
  }

  private onBackupStarted(payload: Message) {
    const event: FileBackupStartedEvent = JSON.parse(payload.body);
    this.currentDownload = {
      fileCopy: {
        id: event.fileCopyId,
        naturalId: event.fileCopyNaturalId,
        status: FileCopyStatus.InProgress,
        filePath: event.filePath
      },
      gameFile: {
        fileSource: {
          originalGameTitle: event.originalGameTitle,
          gameProviderId: 'UNKNOWN',
          fileTitle: event.fileTitle,
          version: event.version,
          url: 'UNKNOWN',
          originalFileName: event.originalFileName,
          size: event.size,
        }
      },
      game: {
        title: "Game title not passed yet" // @TODO Pass Game title in event
      }
    }
  }

  private onProgressChanged(payload: Message) {
    this.downloadProgress = JSON.parse(payload.body);
  }

  private onStatusChanged(payload: Message) {
    const event: FileCopyStatusChangedEvent = JSON.parse(payload.body);
    console.warn(event);
    console.warn(this.currentDownload);
    if (event.fileCopyNaturalId.gameFileId != this.currentDownload?.fileCopy.naturalId.gameFileId) {
      return;
    }
    if (event.newStatus == FileCopyStatus.Success || event.newStatus == FileCopyStatus.Failed) {
      this.currentDownload = undefined;
    }
  }

  async refreshCurrentlyDownloaded(): Promise<void> {
    this.currentDownloadIsLoading = true;

    try {
      const fileCopyWithContext = await firstValueFrom(this.fileCopiesClient.getCurrentlyDownloading());
      if (!fileCopyWithContext) {
        this.currentDownload = undefined;
      } else {
        this.currentDownload = fileCopyWithContext;
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
