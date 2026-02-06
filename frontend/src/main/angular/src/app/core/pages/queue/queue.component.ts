import {Component, OnInit, signal} from '@angular/core';
import {
  FileBackupMessageTopics,
  FileCopiesClient,
  FileCopyReplicationProgressUpdatedEvent,
  FileCopyStatus,
  FileCopyStatusChangedEvent,
  FileCopyWithContext,
  StorageSolutionsClient,
  StorageSolutionStatus,
  StorageSolutionStatusesResponse
} from "@backend";
import {firstValueFrom} from "rxjs";
import {MessageService} from "@app/shared/backend/services/message.service";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {PageHeaderComponent} from "@app/shared/components/page-header/page-header.component";
import {SectionComponent} from "@app/shared/components/section/section.component";
import {LoadedContentComponent} from "@app/shared/components/loaded-content/loaded-content.component";
import {ButtonComponent} from "@app/shared/components/button/button.component";
import {PaginationComponent} from "@app/shared/components/pagination/pagination.component";
import {DatePipe} from "@angular/common";
import {IconItemComponent} from "@app/shared/components/icon-item/icon-item.component";
import {NamedValueComponent} from "@app/shared/components/named-value/named-value.component";
import {
  StorageSolutionStatusBadgeComponent
} from "@app/core/components/storage-solution-status-badge/storage-solution-status-badge.component";
import {
  FileCopyStatusBadgeComponent
} from "@app/core/components/file-copy-status-badge/file-copy-status-badge.component";
import {
  GameFileVersionBadgeComponent
} from "@app/core/components/game-file-version-badge/game-file-version-badge.component";
import {ProgressBarComponent} from "@app/shared/components/progress-bar/progress-bar.component";
import {AutoLayoutComponent} from "@app/shared/components/auto-layout/auto-layout.component";
import {
  NamedValueContainerComponent
} from "@app/shared/components/named-value-container/named-value-container.component";
import {Page} from "@app/shared/components/table/page";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";

@Component({
  selector: 'app-queue',
  imports: [
    PageHeaderComponent,
    SectionComponent,
    LoadedContentComponent,
    ButtonComponent,
    PaginationComponent,
    IconItemComponent,
    NamedValueComponent,
    StorageSolutionStatusBadgeComponent,
    FileCopyStatusBadgeComponent,
    GameFileVersionBadgeComponent,
    ProgressBarComponent,
    DatePipe,
    AutoLayoutComponent,
    NamedValueContainerComponent
  ],
  templateUrl: './queue.component.html',
  styleUrl: './queue.component.scss'
})
export class QueueComponent implements OnInit {

  fileCopiesAreLoading = signal(false);
  fileCopyWithContextPage =
    signal<Page<FileCopyWithContext> | undefined>(undefined);
  pageNumber = signal(1);
  pageSize = signal(3);
  storageSolutionStatusesById =
    signal<Map<string, StorageSolutionStatus>>(new Map());

  constructor(private readonly fileCopiesClient: FileCopiesClient,
              private readonly storageSolutionsClient: StorageSolutionsClient,
              private readonly messageService: MessageService,
              private readonly notificationService: NotificationService) {
    this.messageService.watch<FileCopyStatusChangedEvent>(FileBackupMessageTopics.TopicBackupsStatusChanged)
      .pipe(takeUntilDestroyed())
      .subscribe(event => this.onStatusChanged(event));
    this.messageService.watch<FileCopyReplicationProgressUpdatedEvent>(FileBackupMessageTopics.TopicBackupsProgressUpdate)
      .pipe(takeUntilDestroyed())
      .subscribe(event => this.onReplicationProgressChanged(event));
  }

  ngOnInit(): void {
    void this.refresh();
  }

  readonly refreshAction: () => Promise<void> = () => this.refresh();

  async refresh(): Promise<void> {
    if (this.fileCopiesAreLoading()) {
      return;
    }
    this.fileCopiesAreLoading.set(true);
    try {
      const [fileCopyWithContextPage, storageSolutionStatusesResponse]:
        [Page<FileCopyWithContext>, StorageSolutionStatusesResponse] = await Promise.all([
        firstValueFrom(this.fileCopiesClient.getFileCopyQueue({
          page: this.pageNumber() - 1,
          size: this.pageSize()
        })),
        firstValueFrom(this.storageSolutionsClient.getStorageSolutionStatuses())
      ]);
      this.fileCopyWithContextPage.set(fileCopyWithContextPage);
      this.storageSolutionStatusesById.set(new Map<string, StorageSolutionStatus>(
        Object.entries(storageSolutionStatusesResponse.statuses)));
    } catch (error) {
      this.notificationService.showFailure('Error fetching enqueued files', error);
    } finally {
      this.fileCopiesAreLoading.set(false);
    }
  }

  onClickCancelBackup(fileCopyWithContext: FileCopyWithContext): () => Promise<void> {
    return async () => this.cancelBackup(fileCopyWithContext);
  }

  async cancelBackup(fileCopyWithContext: FileCopyWithContext): Promise<void> {
    try {
      await firstValueFrom(this.fileCopiesClient.cancelFileCopy(fileCopyWithContext.fileCopy.id));
      this.notificationService.showSuccess("Backup canceled");
      fileCopyWithContext.progress = undefined;
    } catch (error) {
      this.notificationService.showFailure(
        'An error occurred while trying to cancel the backup', fileCopyWithContext, error);
    } finally {
      await this.refresh();
    }
  }

  getStorageSolutionStatus(storageSolutionId: string): StorageSolutionStatus | undefined {
    return this.storageSolutionStatusesById().get(storageSolutionId);
  }

  onReplicationProgressChanged(event: FileCopyReplicationProgressUpdatedEvent) {
    this.fileCopyWithContextPage.update(page => {
      if (!page) {
        return page;
      }
      const foundFileCopyWithContext: FileCopyWithContext | undefined =
        this.findFileCopyWithContext(page, event.fileCopyId);
      if (!foundFileCopyWithContext) {
        return page;
      }
      const indexInPage: number = this.getIndexInPage(page, foundFileCopyWithContext);
      const newContent: FileCopyWithContext[] = [...page.content];
      newContent[indexInPage] = this.getWithUpdatedProgress(foundFileCopyWithContext, event);
      return {...page, content: newContent};
    });
  }

  private getWithUpdatedProgress(foundFileCopyWithContext: FileCopyWithContext,
                                 event: FileCopyReplicationProgressUpdatedEvent): FileCopyWithContext {
    return {
      ...foundFileCopyWithContext,
      progress: {
        percentage: event.percentage,
        timeLeftSeconds: event.timeLeftSeconds
      }
    };
  }

  private onStatusChanged(event: FileCopyStatusChangedEvent) {
    this.fileCopyWithContextPage.update(page => {
      if (!page) {
        return page;
      }
      const foundFileCopyWithContext: FileCopyWithContext | undefined =
        this.findFileCopyWithContext(page, event.fileCopyId);
      if (!foundFileCopyWithContext) {
        return page;
      }
      const indexInPage: number = this.getIndexInPage(page, foundFileCopyWithContext);
      const newContent: FileCopyWithContext[] = [...page.content];
      if (event.newStatus == FileCopyStatus.InProgress) {
        newContent[indexInPage] = this.getWithUpdatedStatusAndProgress(foundFileCopyWithContext);
      } else if (event.newStatus != FileCopyStatus.Enqueued && event.newStatus != FileCopyStatus.InProgress) {
        // Remove the FileCopy from the queue:
        newContent.splice(indexInPage, 1);
      }

      return {...page, content: newContent};
    });
  }

  private getWithUpdatedStatusAndProgress(foundFileCopyWithContext: FileCopyWithContext): FileCopyWithContext {
    return {
      ...foundFileCopyWithContext,
      fileCopy: {
        ...foundFileCopyWithContext.fileCopy,
        status: FileCopyStatus.InProgress
      },
      // We might have gotten the status change and progress update events out of order, in which case we don't want
      // to reset the progress on status change:
      progress: foundFileCopyWithContext.progress ?? {
        percentage: 0,
        timeLeftSeconds: 0
      }
    };
  }

  private findFileCopyWithContext(page: Page<FileCopyWithContext>, fileCopyId: string)
    : FileCopyWithContext {
    return <FileCopyWithContext>page.content
      .find(fileCopyWithContext => fileCopyWithContext?.fileCopy.id == fileCopyId);
  }

  private getIndexInPage(page: Page<FileCopyWithContext>, foundFileCopyWithContext: FileCopyWithContext) {
    return page.content.findIndex(item => item.fileCopy.id == foundFileCopyWithContext.fileCopy.id);
  }
}
