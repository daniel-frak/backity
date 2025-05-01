import {Component, OnDestroy, OnInit} from '@angular/core';
import {
  FileBackupMessageTopics,
  FileBackupStartedEvent,
  FileBackupStatus,
  GameFile,
  GameFileProcessingStatus,
  GameFilesClient,
  PageGameFile
} from "@backend";
import {ButtonComponent} from "@app/shared/components/button/button.component";
import {CardComponent} from "@app/shared/components/card/card.component";
import {NgForOf, NgIf} from "@angular/common";
import {PaginationComponent} from "@app/shared/components/pagination/pagination.component";
import {TableColumnDirective} from "@app/shared/components/table/column-directive/table-column.directive";
import {TableComponent} from "@app/shared/components/table/table.component";
import {firstValueFrom, Subscription} from "rxjs";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {Message} from "@stomp/stompjs";
import {LoadedContentComponent} from "@app/shared/components/loaded-content/loaded-content.component";

@Component({
  selector: 'app-enqueued-files-card',
  standalone: true,
  imports: [
    ButtonComponent,
    CardComponent,
    NgForOf,
    NgIf,
    PaginationComponent,
    TableColumnDirective,
    TableComponent,
    LoadedContentComponent
  ],
  templateUrl: './enqueued-files-card.component.html',
  styleUrl: './enqueued-files-card.component.scss'
})
export class EnqueuedFilesCardComponent implements OnInit, OnDestroy {

  asGameFile = (gameFile: GameFile) => gameFile;

  filesAreLoading: boolean = false;
  filePage?: PageGameFile;
  pageNumber: number = 1;
  pageSize: number = 3;

  protected readonly FileBackupStatus = FileBackupStatus;

  private readonly subscriptions: Subscription[] = [];

  constructor(private readonly gameFilesClient: GameFilesClient,
              private readonly messageService: MessagesService,
              private readonly notificationService: NotificationService) {
  }


  ngOnInit(): void {
    this.subscriptions.push(
      this.messageService.watch(FileBackupMessageTopics.Started)
        .subscribe(p => this.onBackupStarted(p))
    )
  }

  private onBackupStarted(payload: Message) {
    const event: FileBackupStartedEvent = JSON.parse(payload.body);
    this.tryToRemoveFileFromEnqueuedDownloads(event);
  }

  private tryToRemoveFileFromEnqueuedDownloads(event: FileBackupStartedEvent) {
    const foundFile: GameFile | undefined = this.findFileInEnqueuedDownloads(event);
    if (foundFile) {
      const index: number | undefined = this.filePage?.content?.indexOf(foundFile);
      if (index !== -1 && index !== undefined && index !== null) {
        this.filePage?.content?.splice(index, 1);
      }
    }
  }

  private findFileInEnqueuedDownloads(event: FileBackupStartedEvent) {
    return this.filePage?.content
      ?.find(file => file?.id == event.gameFileId);
  }

  onClickRefreshEnqueuedFiles(): () => Promise<void> {
    return async () => this.refreshEnqueuedFiles();
  }

  async refreshEnqueuedFiles(): Promise<void> {
    this.filesAreLoading = true;

    try {
      this.filePage = await firstValueFrom(
        this.gameFilesClient.getGameFiles(GameFileProcessingStatus.Enqueued, {
          page: this.pageNumber - 1,
          size: this.pageSize
        }));
    } catch (error) {
      this.notificationService.showFailure('Error fetching enqueued files', error);
    } finally {
      this.filesAreLoading = false;
    }
  }

  onClickRemoveFromQueue(fileId?: string): () => Promise<void> {
    return async () => this.removeFromQueue(fileId);
  }

  async removeFromQueue(fileId?: string): Promise<void> {
    this.notificationService.showFailure('Removing from queue not yet implemented');
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(s => s.unsubscribe());
  }
}
