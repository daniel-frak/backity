import {Component} from '@angular/core';
import {FileCopyStatus, FileCopy, FileCopyProcessingStatus, FileCopiesClient, PageFileCopy} from "@backend";
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
  selector: 'app-processed-file-copies-card',
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
  templateUrl: './processed-file-copies-card.component.html',
  styleUrl: './processed-file-copies-card.component.scss'
})
export class ProcessedFileCopiesCardComponent {

  asFileCopy = (fileCopy: FileCopy) => fileCopy;
  FileCopyStatus = FileCopyStatus;

  fileCopiesAreLoading: boolean = false;
  fileCopyPage?: PageFileCopy;
  pageNumber: number = 1;
  pageSize: number = 3;

  constructor(private readonly fileCopiesClient: FileCopiesClient,
              private readonly notificationService: NotificationService) {
  }

  onClickRefreshProcessedFiles(): () => Promise<void> {
    return async () => this.refreshProcessedFileCopies();
  }

  async refreshProcessedFileCopies(): Promise<void> {
    this.fileCopiesAreLoading = true;

    try {
      this.fileCopyPage = await firstValueFrom(
        this.fileCopiesClient.getFileCopiesWithStatus(FileCopyProcessingStatus.Processed, {
          page: this.pageNumber - 1,
          size: this.pageSize
        }));
    } catch (error) {
      this.notificationService.showFailure('Error fetching processed files', error);
    } finally {
      this.fileCopiesAreLoading = false;
    }
  }
}
