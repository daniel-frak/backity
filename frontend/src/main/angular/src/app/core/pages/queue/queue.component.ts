import {Component, OnDestroy, OnInit} from '@angular/core';
import {
  FileBackupMessageTopics,
  FileCopiesClient,
  FileCopyStatus,
  FileCopyStatusChangedEvent,
  FileCopyWithContext,
  FileDownloadProgressUpdatedEvent,
  PageFileCopyWithContext,
  StorageSolutionsClient,
  StorageSolutionStatus,
  StorageSolutionStatusesResponse
} from "@backend";
import {firstValueFrom, Subscription} from "rxjs";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {Message} from "@stomp/stompjs";
import {PageHeaderComponent} from "@app/shared/components/page-header/page-header.component";
import {SectionComponent} from "@app/shared/components/section/section.component";
import {LoadedContentComponent} from "@app/shared/components/loaded-content/loaded-content.component";
import {ButtonComponent} from "@app/shared/components/button/button.component";
import {PaginationComponent} from "@app/shared/components/pagination/pagination.component";
import {DatePipe, NgForOf, NgIf} from "@angular/common";
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

@Component({
  selector: 'app-queue',
  standalone: true,
  imports: [
    PageHeaderComponent,
    SectionComponent,
    LoadedContentComponent,
    ButtonComponent,
    PaginationComponent,
    NgForOf,
    NgIf,
    IconItemComponent,
    NamedValueComponent,
    StorageSolutionStatusBadgeComponent,
    FileCopyStatusBadgeComponent,
    GameFileVersionBadgeComponent,
    ProgressBarComponent,
    DatePipe
  ],
  templateUrl: './queue.component.html',
  styleUrl: './queue.component.scss'
})
export class QueueComponent implements OnInit, OnDestroy {

  fileCopiesAreLoading: boolean = false;

  fileCopyWithContextPage?: PageFileCopyWithContext;
  pageNumber: number = 1;
  pageSize: number = 3;
  storageSolutionStatusesById: Map<string, StorageSolutionStatus> = new Map();

  protected readonly FileCopyStatus = FileCopyStatus;

  private readonly subscriptions: Subscription[] = [];

  constructor(private readonly fileCopiesClient: FileCopiesClient,
              private readonly storageSolutionsClient: StorageSolutionsClient,
              private readonly messageService: MessagesService,
              private readonly notificationService: NotificationService) {
  }

  ngOnInit(): void {
    this.subscriptions.push(
      this.messageService.watch(FileBackupMessageTopics.StatusChanged)
        .subscribe(p => this.onStatusChanged(p)),
      this.messageService.watch(FileBackupMessageTopics.ProgressUpdate)
        .subscribe(p => this.onReplicationProgressChanged(p))
    );
  }

  private onStatusChanged(payload: Message) {
    const event: FileCopyStatusChangedEvent = JSON.parse(payload.body);
    const foundFileCopyWithContext: FileCopyWithContext | undefined =
      this.findFileCopyWithContextInQueue(event.fileCopyId);
    if (!foundFileCopyWithContext) {
      return;
    }
    if (event.newStatus != FileCopyStatus.Enqueued && event.newStatus != FileCopyStatus.InProgress) {
      this.removeFileCopyFromQueue(foundFileCopyWithContext);
    }
    if (event.newStatus == FileCopyStatus.InProgress) {
      this.updateFileCopyStatusInQueue(foundFileCopyWithContext);
    }
  }

  private findFileCopyWithContextInQueue(fileCopyId: string): FileCopyWithContext | undefined {
    return this.fileCopyWithContextPage?.content
      ?.find(fileCopyWithContext => fileCopyWithContext?.fileCopy.id == fileCopyId);
  }

  private removeFileCopyFromQueue(foundFileCopyWithContext: FileCopyWithContext) {
    const index: number | undefined = this.fileCopyWithContextPage?.content?.indexOf(foundFileCopyWithContext);
    if (index !== -1 && index !== undefined && index !== null) {
      this.fileCopyWithContextPage?.content?.splice(index, 1);
    }
  }

  private updateFileCopyStatusInQueue(foundFileCopyWithContext: FileCopyWithContext) {
    foundFileCopyWithContext.fileCopy.status = FileCopyStatus.InProgress;

    // We might have gotten the status change and progress update events out of order, in which case we don't want
    // to reset the progress on status change:
    foundFileCopyWithContext.progress ??= {
      percentage: 0,
      timeLeftSeconds: 0
    };
  }

  private onReplicationProgressChanged(payload: Message) {
    const event: FileDownloadProgressUpdatedEvent = JSON.parse(payload.body);
    const foundFileCopyWithContext: FileCopyWithContext | undefined =
      this.findFileCopyWithContextInQueue(event.fileCopyId);
    if (!foundFileCopyWithContext) {
      return;
    }

    foundFileCopyWithContext.progress = {
      percentage: event.percentage,
      timeLeftSeconds: event.timeLeftSeconds
    };
  }

  onClickRefreshEnqueuedFileCopies(): () => Promise<void> {
    return async () => this.refreshEnqueuedFileCopies();
  }

  async refreshEnqueuedFileCopies(): Promise<void> {
    this.fileCopiesAreLoading = true;

    try {
      this.fileCopyWithContextPage = await firstValueFrom(
        this.fileCopiesClient.getFileCopyQueue({
          page: this.pageNumber - 1,
          size: this.pageSize
        }));

      const storageSolutionStatusesResponse: StorageSolutionStatusesResponse = await firstValueFrom(
        this.storageSolutionsClient.getStorageSolutionStatuses());
      this.storageSolutionStatusesById = new Map<string, StorageSolutionStatus>(
        Object.entries(storageSolutionStatusesResponse.statuses));
    } catch (error) {
      this.notificationService.showFailure('Error fetching enqueued files', error);
    } finally {
      this.fileCopiesAreLoading = false;
    }
  }

  onClickRemoveFromQueue(fileId?: string): () => Promise<void> {
    return async () => this.removeFromQueue(fileId);
  }

  async removeFromQueue(fileId?: string): Promise<void> {
    this.notificationService.showFailure('Removing from queue not yet implemented');
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(s => s.unsubscribe());
  }

  getStorageSolutionStatus(storageSolutionId: string) {
    return this.storageSolutionStatusesById.get(storageSolutionId);
  }
}
