import {Component, OnInit} from '@angular/core';
import {BackupsClient, FileBackupStatus, GameFileDetails, GamesClient, PageGameWithFiles} from "@backend";
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
              private readonly backupsClient: BackupsClient) {
  }

  ngOnInit(): void {
    this.refresh();
  }

  refresh() {
    this.gamesAreLoading = true;
    this.gamesClient.getGames(0, 20)
      .subscribe(games => {
        this.gameWithFilesPage = games;
        this.gamesAreLoading = false;
      });
  }

  backUp(file: GameFileDetails) {
    file.backupDetails!.status = FileBackupStatus.Enqueued;
    console.info("Enqueuing backup: " + file.id);
    this.backupsClient.download(file.id!)
      .pipe(catchError(e => {
        file.backupDetails!.status = FileBackupStatus.Discovered;
        return throwError(e);
      }))
      .subscribe(() => {
      }, err => console.error(`An error occurred while trying to enqueue a file (${file})`,
        file, err));
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

  asGameFile = (game: GameFileDetails) => game;
  public readonly FileBackupStatus = FileBackupStatus;
}
