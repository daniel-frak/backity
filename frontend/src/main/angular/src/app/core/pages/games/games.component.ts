import {Component, OnDestroy, OnInit} from '@angular/core';
import {
  FileBackupMessageTopics,
  FileBackupsClient,
  FileBackupStatus, FileBackupStatusChangedEvent,
  GameFile,
  GameFilesClient,
  GamesClient,
  PageGameWithFiles
} from "@backend";
import {catchError} from "rxjs/operators";
import {firstValueFrom, Subscription} from "rxjs";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {ModalService} from "@app/shared/services/modal-service/modal.service";
import {PageHeaderComponent} from '@app/shared/components/page-header/page-header.component';
import {ButtonComponent} from '@app/shared/components/button/button.component';
import {LoadedContentComponent} from '@app/shared/components/loaded-content/loaded-content.component';
import {NgFor, NgSwitch, NgSwitchCase} from '@angular/common';
import {TableComponent} from '@app/shared/components/table/table.component';
import {TableColumnDirective} from '@app/shared/components/table/column-directive/table-column.directive';
import {FileStatusBadgeComponent} from './file-status-badge/file-status-badge.component';
import {CardComponent} from "@app/shared/components/card/card.component";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {Message} from "@stomp/stompjs";
import {PaginationComponent} from "@app/shared/components/pagination/pagination.component";

@Component({
  selector: 'app-games',
  templateUrl: './games.component.html',
  styleUrls: ['./games.component.scss'],
  standalone: true,
    imports: [PageHeaderComponent, ButtonComponent, LoadedContentComponent, NgFor, TableComponent, TableColumnDirective, FileStatusBadgeComponent, NgSwitch, NgSwitchCase, CardComponent, PaginationComponent]
})
export class GamesComponent implements OnInit, OnDestroy {

  gamesAreLoading: boolean = true;
  gameWithFilesPage?: PageGameWithFiles;

  gamePageNumber: number = 1;
  gamePageSize: number = 3;

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

    if(fileInTable) {
      fileInTable.fileBackup.status = event.newStatus as FileBackupStatus;
    }
  }

  private findFileInTable(event: FileBackupStatusChangedEvent) {
    return this.gameWithFilesPage?.content
      ?.flatMap(game => game.files)
      ?.find(file => file?.id == event.gameFileId);
  }

  refresh = async () => {
    try {
      this.gamesAreLoading = true;
      this.gameWithFilesPage = await firstValueFrom(this.gamesClient.getGames({
        page: this.gamePageNumber - 1,
        size: this.gamePageSize
      }));
    } catch (error) {
      this.notificationService.showFailure('Error fetching games', error);
    } finally {
      this.gamesAreLoading = false;
    }
  }

  enqueueFileBackup(gameFile: GameFile): () => Promise<void> {
    return async () => {
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
    };
  }

  cancelBackup(gameFileId: string): () => Promise<void> {
    return async () => {
      this.notificationService.showFailure('Removing from queue not yet implemented');
    }
  }

  deleteBackup(gameFileId: string): () => Promise<void> {
    return async () => {
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
    };
  }

  viewFilePath(gameFileId: string): () => Promise<void> {
    return async () => {
      this.notificationService.showFailure('Viewing file paths not yet implemented');
    };
  }

  download(gameFileId: string): () => Promise<void> {
    return async () => {
      this.notificationService.showFailure('Downloading files not yet implemented');
    }
  }

  viewError(gameFileId: string): () => Promise<void> {
    return async () => {
      this.notificationService.showFailure('Viewing errors not yet implemented');
    }
  }

  asGameFile = (gameFile: GameFile) => gameFile;

  public readonly FileBackupStatus = FileBackupStatus;

  ngOnDestroy(): void {
    this.subscriptions.forEach(s => s.unsubscribe());
  }
}
