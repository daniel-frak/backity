import {Component} from '@angular/core';
import {ButtonComponent} from "@app/shared/components/button/button.component";
import {CardComponent} from "@app/shared/components/card/card.component";
import {NgForOf} from "@angular/common";
import {firstValueFrom} from "rxjs";
import {
  EnqueueFileCopyRequest,
  FileCopiesClient,
  FileCopy,
  FileCopyProcessingStatus,
  FileCopyStatus,
  PageFileCopy
} from "@backend";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {catchError} from "rxjs/operators";
import {PaginationComponent} from "@app/shared/components/pagination/pagination.component";
import {TableColumnDirective} from "@app/shared/components/table/column-directive/table-column.directive";
import {TableComponent} from "@app/shared/components/table/table.component";
import {LoadedContentComponent} from "@app/shared/components/loaded-content/loaded-content.component";

@Component({
  selector: 'app-discovered-file-copies-card',
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
  templateUrl: './discovered-file-copies-card.component.html',
  styleUrl: './discovered-file-copies-card.component.scss'
})

// @TODO Remove this
export class DiscoveredFileCopiesCardComponent {

  asFileCopy = (fileCopy: FileCopy) => fileCopy;
  FileCopyStatus = FileCopyStatus;

  filesAreLoading: boolean = false;
  filePage?: PageFileCopy;
  pageNumber: number = 1;
  pageSize: number = 3;

  constructor(private readonly fileCopiesClient: FileCopiesClient,
              private readonly notificationService: NotificationService) {
  }

  onClickRefreshDiscoveredFiles(): () => Promise<void> {
    return async () => this.refreshDiscoveredFiles();
  }

  async refreshDiscoveredFiles(): Promise<void> {
    this.filesAreLoading = true;
    try {
      const fileCopyPage = await firstValueFrom(
        this.fileCopiesClient.getFileCopiesWithStatus(FileCopyProcessingStatus.Discovered, {
          page: this.pageNumber - 1,
          size: this.pageSize
        }));
      this.updateDiscoveredFiles(fileCopyPage);
    } catch (error) {
      this.notificationService.showFailure('Error fetching discovered files', error);
    } finally {
      this.filesAreLoading = false;
    }
  }

  private updateDiscoveredFiles(fileCopyPage: PageFileCopy) {
    this.filePage = fileCopyPage;
    this.filesAreLoading = false;
  }

  onClickEnqueueFile(fileCopy: FileCopy): () => Promise<void> {
    return async () => this.enqueueFile(fileCopy);
  }

  async enqueueFile(fileCopy: FileCopy): Promise<void> {
    fileCopy.status = FileCopyStatus.Enqueued;
    try {
      const request: EnqueueFileCopyRequest = {
        fileCopyNaturalId: fileCopy.naturalId
      };
      await firstValueFrom(this.fileCopiesClient.enqueueFileCopy(request).pipe(catchError(e => {
        fileCopy.status = FileCopyStatus.Discovered;
        throw e;
      })));
      this.notificationService.showSuccess("File copy enqueued");
    } catch (error) {
      this.notificationService.showFailure('An error occurred while trying to enqueue a file', fileCopy, error);
    }
  }
}
