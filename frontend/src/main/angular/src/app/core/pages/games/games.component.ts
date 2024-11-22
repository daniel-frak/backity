import {Component, OnInit} from '@angular/core';
import {FileBackupStatus, GameFile, GameFilesClient, GamesClient, PageGameWithFiles} from "@backend";
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
              private readonly gameFilesClient: GameFilesClient) {
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

  cancelBackup(fileId: string) {
    console.error("Removing from queue not yet implemented");
  }

  deleteBackup(fileId: string) {
    console.error("Deleting backups not yet implemented");
  }


  viewFilePath(fileId: string) {
    console.error("Viewing file paths not yet implemented");
  }

  download(fileId: string) {
    console.error("Downloading files not yet implemented");
  }

  viewError(fileId: string) {
    console.error("Viewing errors not yet implemented");
  }

  asGameFile = (gameFile: GameFile) => gameFile;

  public readonly FileBackupStatus = FileBackupStatus;
}
