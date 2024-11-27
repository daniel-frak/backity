import {Component, OnInit} from '@angular/core';
import {FileBackupsClient, FileBackupStatus, GameFile, GameFilesClient, GamesClient, PageGameWithFiles} from "@backend";
import {catchError} from "rxjs/operators";
import {firstValueFrom} from "rxjs";
import {NotificationService} from "@app/shared/services/notification/notification.service";

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
              private readonly notificationService: NotificationService) {
  }

  ngOnInit(): void {
    this.notificationService.showFailure("Test content", "Test");
    this.notificationService.showSuccess("Test content");
    this.notificationService.show("A very very very very very very very very long toast");
    this.refresh().then(r => {
      // Do nothing
    });
  }

  refresh = async () => {
    try {
      this.gamesAreLoading = true;
      this.gameWithFilesPage = await firstValueFrom(this.gamesClient.getGames({page: 0, size: 20}));
    } catch (error) {
      console.error('Error fetching games:', error);
    } finally {
      this.gamesAreLoading = false;
    }
  }

  enqueueFileBackup(gameFile: GameFile): () => Promise<void> {
    return async () => {
      console.info("Enqueuing backup: " + gameFile.id);
      try {
        await firstValueFrom(this.gameFilesClient.enqueueFileBackup(gameFile.id).pipe(catchError(e => {
          throw e;
        })));
        gameFile.fileBackup.status = FileBackupStatus.Enqueued;
      } catch (err) {
        console.error(`An error occurred while trying to enqueue a file (id=${gameFile.id})`, gameFile, err);
        gameFile.fileBackup.status = FileBackupStatus.Discovered;
      }
    };
  }

  cancelBackup(gameFileId: string): () => Promise<void> {
    return async () => {
      console.error("Removing from queue not yet implemented");
    }
  }

  deleteBackup(gameFileId: string): () => Promise<void> {
    return async () => {
      try {
        await firstValueFrom(this.fileBackupsClient.deleteFileBackup(gameFileId));
        return this.refresh();
      } catch (err) {
        console.error(`An error occurred while trying to delete a file backup (id=${gameFileId})`, gameFileId, err);
        throw err;
      }
    };
  }

  viewFilePath(gameFileId: string): () => Promise<void> {
    return async () => {
      console.error("Viewing file paths not yet implemented");
    };
  }

  download(gameFileId: string): () => Promise<void> {
    return async () => {
      console.error("Downloading files not yet implemented");
    }
  }

  viewError(gameFileId: string): () => Promise<void> {
    return async () => {
      console.error("Viewing errors not yet implemented");
    }
  }

  asGameFile = (gameFile: GameFile) => gameFile;

  public readonly FileBackupStatus = FileBackupStatus;
}
