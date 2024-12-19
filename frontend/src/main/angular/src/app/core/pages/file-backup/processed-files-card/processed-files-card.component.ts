import {Component} from '@angular/core';
import {FileBackupStatus, GameFile, GameFileProcessingStatus, GameFilesClient, PageGameFile} from "@backend";
import {ButtonComponent} from "@app/shared/components/button/button.component";
import {CardComponent} from "@app/shared/components/card/card.component";
import {NgClass, NgForOf} from "@angular/common";
import {PaginationComponent} from "@app/shared/components/pagination/pagination.component";
import {TableColumnDirective} from "@app/shared/components/table/column-directive/table-column.directive";
import {TableComponent} from "@app/shared/components/table/table.component";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {firstValueFrom} from "rxjs";
import {LoadedContentComponent} from "@app/shared/components/loaded-content/loaded-content.component";

@Component({
  selector: 'app-processed-files-card',
  standalone: true,
  imports: [
    ButtonComponent,
    CardComponent,
    NgForOf,
    PaginationComponent,
    TableColumnDirective,
    TableComponent,
    NgClass,
    LoadedContentComponent
  ],
  templateUrl: './processed-files-card.component.html',
  styleUrl: './processed-files-card.component.scss'
})
export class ProcessedFilesCardComponent {

  asGameFile = (gameFile: GameFile) => gameFile;
  FileBackupStatus = FileBackupStatus;

  filesAreLoading: boolean = false;
  filePage?: PageGameFile;
  pageNumber: number = 1;
  pageSize: number = 3;

  constructor(private readonly gameFilesClient: GameFilesClient,
              private readonly notificationService: NotificationService) {
  }

  onClickRefreshProcessedFiles(): () => Promise<void> {
    return async () => this.refreshProcessedFiles();
  }

  async refreshProcessedFiles(): Promise<void> {
    this.filesAreLoading = true;

    try {
      this.filePage = await firstValueFrom(
        this.gameFilesClient.getGameFiles(GameFileProcessingStatus.Processed, {
          page: this.pageNumber - 1,
          size: this.pageSize
        }));
    } catch (error) {
      this.notificationService.showFailure('Error fetching processed files', error);
    } finally {
      this.filesAreLoading = false;
    }
  }
}
