import {Component, OnInit} from '@angular/core';
import {FileBackupsClient, FileBackupStatus, GameFile, GameFilesClient, GamesClient, PageGameWithFiles} from "@backend";
import {catchError} from "rxjs/operators";
import {firstValueFrom} from "rxjs";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {ModalService} from "@app/shared/services/modal-service/modal.service";

@Component({
  selector: 'app-games',
  templateUrl: './games.component.html',
  styleUrls: ['./games.component.scss']
})
export class GamesComponent implements OnInit {

  gamesAreLoading: boolean = true;
  gameWithFilesPage?: PageGameWithFiles;

  constructor(private readonly gamesClient: GamesClient,
              private readonly gameFilesClient: GameFilesClient,
              private readonly fileBackupsClient: FileBackupsClient,
              private readonly notificationService: NotificationService,
              private readonly modalService: ModalService) {
  }

  ngOnInit(): void {
    this.refresh().then(() => {
      // Do nothing
    });
  }

  refresh = async () => {
    try {
      this.gamesAreLoading = true;
      this.gameWithFilesPage = await firstValueFrom(this.gamesClient.getGames({page: 0, size: 20}));
    } catch (error) {
      this.notificationService.showFailure('Error fetching games', undefined, error);
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
        this.notificationService.showFailure(
          'An error occurred while trying to enqueue a file', undefined, gameFile, error);
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
        this.notificationService.showFailure(
          'An error occurred while trying to delete a file backup', undefined, gameFileId, error);
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
}
