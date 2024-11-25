import {Component, OnInit} from '@angular/core';
import {FileBackupsClient, FileBackupStatus, GameFile, GameFilesClient, GamesClient, PageGameWithFiles} from "@backend";
import {catchError} from "rxjs/operators";
import {throwError} from "rxjs";

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
              private readonly fileBackupsClient: FileBackupsClient) {
  }

  ngOnInit(): void {
    this.refresh();
  }

  refresh() {
    this.gamesAreLoading = true;
    this.gamesClient.getGames({
      page: 0,
      size: 20
    })
      .subscribe(games => {
        this.gameWithFilesPage = games;
        this.gamesAreLoading = false;
      });
  }

  backUp(file: GameFile) {
    file.fileBackup.status = FileBackupStatus.Enqueued;
    console.info("Enqueuing backup: " + file.id);
    this.gameFilesClient.download(file.id)
      .pipe(catchError(e => {
        file.fileBackup.status = FileBackupStatus.Discovered;
        return throwError(() => e);
      }))
      .subscribe({
        error: (err) => console.error(`An error occurred while trying to enqueue a file (id=${file.id})`,
          file, err)
      });
  }

  cancelBackup(gameFileId: string) {
    console.error("Removing from queue not yet implemented");
  }

  deleteBackup(gameFileId: string) {
    this.fileBackupsClient.deleteFileBackup(gameFileId)
      .subscribe({
        complete: () => {
          this.refresh();
        },
        error: (err) =>
          console.error(`An error occurred while trying to delete a file backup (id=${gameFileId})`,
          gameFileId, err)
      });
  }


  viewFilePath(gameFileId: string) {
    console.error("Viewing file paths not yet implemented");
  }

  download(gameFileId: string) {
    console.error("Downloading files not yet implemented");
  }

  viewError(gameFileId: string) {
    console.error("Viewing errors not yet implemented");
  }

  asGameFile = (gameFile: GameFile) => gameFile;

  public readonly FileBackupStatus = FileBackupStatus;
}
