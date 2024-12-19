import {Component} from '@angular/core';
import {ButtonComponent} from "@app/shared/components/button/button.component";
import {CardComponent} from "@app/shared/components/card/card.component";
import {NgForOf} from "@angular/common";
import {firstValueFrom} from "rxjs";
import {FileBackupStatus, GameFile, GameFileProcessingStatus, GameFilesClient, PageGameFile} from "@backend";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {catchError} from "rxjs/operators";
import {PaginationComponent} from "@app/shared/components/pagination/pagination.component";
import {TableColumnDirective} from "@app/shared/components/table/column-directive/table-column.directive";
import {TableComponent} from "@app/shared/components/table/table.component";
import {LoadedContentComponent} from "@app/shared/components/loaded-content/loaded-content.component";

@Component({
  selector: 'app-discovered-files-card',
  standalone: true,
  imports: [
    ButtonComponent,
    CardComponent,
    NgForOf,
    PaginationComponent,
    TableColumnDirective,
    TableComponent,
    LoadedContentComponent
  ],
  templateUrl: './discovered-files-card.component.html',
  styleUrl: './discovered-files-card.component.scss'
})
export class DiscoveredFilesCardComponent {

  asGameFile = (gameFile: GameFile) => gameFile;
  FileBackupStatus = FileBackupStatus;

  filesAreLoading: boolean = false;
  filePage?: PageGameFile;
  pageNumber: number = 1;
  pageSize: number = 3;

  constructor(private readonly gameFilesClient: GameFilesClient,
              private readonly notificationService: NotificationService) {
  }

  onClickRefreshDiscoveredFiles(): () => Promise<void> {
    return async () => this.refreshDiscoveredFiles();
  }

  async refreshDiscoveredFiles(): Promise<void> {
    this.filesAreLoading = true;
    try {
      const gameFilePage = await firstValueFrom(
        this.gameFilesClient.getGameFiles(GameFileProcessingStatus.Discovered, {
          page: this.pageNumber - 1,
          size: this.pageSize
        }));
      this.updateDiscoveredFiles(gameFilePage);
    } catch (error) {
      this.notificationService.showFailure('Error fetching discovered files', error);
    } finally {
      this.filesAreLoading = false;
    }
  }

  private updateDiscoveredFiles(gameFilePage: PageGameFile) {
    this.filePage = gameFilePage;
    this.filesAreLoading = false;
  }

  onClickEnqueueFile(gameFile: GameFile): () => Promise<void> {
    return async () => this.enqueueFile(gameFile);
  }

  async enqueueFile(gameFile: GameFile): Promise<void> {
    gameFile.fileBackup.status = FileBackupStatus.Enqueued;
    try {
      await firstValueFrom(this.gameFilesClient.enqueueFileBackup(gameFile.id).pipe(catchError(e => {
        gameFile.fileBackup.status = FileBackupStatus.Discovered;
        throw e;
      })));
      this.notificationService.showSuccess("File backup enqueued");
    } catch (error) {
      this.notificationService.showFailure('An error occurred while trying to enqueue a file', gameFile, error);
    }
  }
}
