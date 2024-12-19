import {Component, OnDestroy, OnInit} from '@angular/core';
import {
  FileBackupMessageTopics,
  FileBackupsClient,
  FileBackupStatus,
  FileBackupStatusChangedEvent,
  GameFile,
  GameFilesClient,
  GamesClient,
  PageGameWithFiles
} from "@backend";
import {catchError} from "rxjs/operators";
import {firstValueFrom, Subscription} from "rxjs";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {ModalService} from "@app/shared/services/modal-service/modal.service";
import {ButtonComponent} from '@app/shared/components/button/button.component';
import {LoadedContentComponent} from '@app/shared/components/loaded-content/loaded-content.component';
import {NgForOf, NgSwitch, NgSwitchCase} from '@angular/common';
import {TableComponent} from '@app/shared/components/table/table.component';
import {TableColumnDirective} from '@app/shared/components/table/column-directive/table-column.directive';
import {CardComponent} from "@app/shared/components/card/card.component";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {Message} from "@stomp/stompjs";
import {PaginationComponent} from "@app/shared/components/pagination/pagination.component";
import {FileStatusBadgeComponent} from "@app/core/pages/games/file-status-badge/file-status-badge.component";

@Component({
  selector: 'app-games-with-files-card',
  standalone: true,
  imports: [
    ButtonComponent,
    CardComponent,
    FileStatusBadgeComponent,
    LoadedContentComponent,
    NgForOf,
    NgSwitchCase,
    PaginationComponent,
    TableColumnDirective,
    TableComponent,
    NgSwitch
  ],
  templateUrl: './games-with-files-card.component.html',
  styleUrl: './games-with-files-card.component.scss'
})
export class GamesWithFilesCardComponent implements OnInit, OnDestroy {

  asGameFile = (gameFile: GameFile) => gameFile;
  FileBackupStatus = FileBackupStatus;

  gamesAreLoading: boolean = true;
  gameWithFilesPage?: PageGameWithFiles;
  pageNumber: number = 1;
  pageSize: number = 3;

  private readonly subscriptions: Subscription[] = [];

  constructor(private readonly gamesClient: GamesClient,
              private readonly gameFilesClient: GameFilesClient,
              private readonly fileBackupsClient: FileBackupsClient,
              private readonly messageService: MessagesService,
              private readonly notificationService: NotificationService,
              private readonly modalService: ModalService) {
  }

  ngOnInit(): void {
    this.subscriptions.push(
      this.messageService.watch(FileBackupMessageTopics.StatusChanged)
        .subscribe(p => this.onStatusChanged(p))
    );
  }

  private onStatusChanged(payload: Message) {
    const event: FileBackupStatusChangedEvent = JSON.parse(payload.body);

    const fileInTable: GameFile | undefined = this.findFileInTable(event);

    if (fileInTable) {
      fileInTable.fileBackup.status = event.newStatus as FileBackupStatus;
    }
  }

  private findFileInTable(event: FileBackupStatusChangedEvent) {
    return this.gameWithFilesPage?.content
      ?.flatMap(game => game.files)
      ?.find(file => file?.id == event.gameFileId);
  }

  onClickRefresh(): () => Promise<void> {
    return async () => this.refresh();
  }

  async refresh(): Promise<void> {
    try {
      this.gamesAreLoading = true;
      this.gameWithFilesPage = await firstValueFrom(this.gamesClient.getGames({
        page: this.pageNumber - 1,
        size: this.pageSize
      }));
    } catch (error) {
      this.notificationService.showFailure('Error fetching games', error);
    } finally {
      this.gamesAreLoading = false;
    }
  }

  onClickEnqueueFileBackup(gameFile: GameFile): () => Promise<void> {
    return async () => this.enqueueFileBackup(gameFile);
  }

  async enqueueFileBackup(gameFile: GameFile): Promise<void> {
    try {
      await firstValueFrom(this.gameFilesClient.enqueueFileBackup(gameFile.id).pipe(catchError(e => {
        throw e;
      })));
      gameFile.fileBackup.status = FileBackupStatus.Enqueued;
      this.notificationService.showSuccess("File backup enqueued");
    } catch (error) {
      this.notificationService.showFailure('An error occurred while trying to enqueue a file', gameFile, error);
      gameFile.fileBackup.status = FileBackupStatus.Discovered;
    }
  }

  onClickCancelBackup(gameFileId: string): () => Promise<void> {
    return async () => this.cancelBackup(gameFileId);
  }

  async cancelBackup(gameFileId: string): Promise<void> {
    this.notificationService.showFailure('Removing from queue not yet implemented');
  }

  onClickDeleteBackup(gameFileId: string): () => Promise<void> {
    return async () => this.deleteBackup(gameFileId);
  }

  async deleteBackup(gameFileId: string): Promise<void> {
    try {
      await this.modalService.withConfirmationModal("Are you sure you want to delete the file backup?",
        async () => {
          await firstValueFrom(this.fileBackupsClient.deleteFileBackup(gameFileId));
          this.notificationService.showSuccess('Deleted file backup');
          return this.refresh();
        });
    } catch (error) {
      this.notificationService.showFailure('An error occurred while trying to delete a file backup', gameFileId, error);
    }
  }

  onClickViewFilePath(gameFileId: string): () => Promise<void> {
    return async () => this.viewFilePath(gameFileId);
  }

  async viewFilePath(gameFileId: string): Promise<void> {
    this.notificationService.showFailure('Viewing file paths not yet implemented');
  }

  onClickDownload(gameFileId: string): () => Promise<void> {
    return async () => this.download(gameFileId);
  }

  async download(gameFileId: string): Promise<void> {
    this.notificationService.showFailure('Downloading files not yet implemented');
  }

  onClickViewError(gameFileId: string): () => Promise<void> {
    return async () => this.viewError(gameFileId);
  }

  async viewError(gameFileId: string): Promise<void> {
    this.notificationService.showFailure('Viewing errors not yet implemented');
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(s => s.unsubscribe());
  }
}
