import {Component, OnDestroy, OnInit} from '@angular/core';
import {
  FileBackupMessageTopics,
  FileCopiesClient,
  FileCopyStatus,
  FileCopyStatusChangedEvent,
  FileCopyWithContext,
  PageFileCopyWithContext
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
import {NgForOf, NgIf} from "@angular/common";
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
    GameFileVersionBadgeComponent
  ],
  templateUrl: './queue.component.html',
  styleUrl: './queue.component.scss'
})
export class QueueComponent implements OnInit, OnDestroy {

  fileCopiesAreLoading: boolean = false;

  fileCopyWithContextPage?: PageFileCopyWithContext;
  pageNumber: number = 1;
  pageSize: number = 3;

  protected readonly FileCopyStatus = FileCopyStatus;

  private readonly subscriptions: Subscription[] = [];

  constructor(private readonly fileCopiesClient: FileCopiesClient,
              private readonly messageService: MessagesService,
              private readonly notificationService: NotificationService) {
  }

  ngOnInit(): void {
    this.subscriptions.push(
      this.messageService.watch(FileBackupMessageTopics.StatusChanged)
        .subscribe(p => this.onStatusChanged(p))
    );
  }

  private onStatusChanged(payload: Message) {
    const event: FileCopyStatusChangedEvent = JSON.parse(payload.body);
    if (event.newStatus != FileCopyStatus.Enqueued && event.newStatus != FileCopyStatus.InProgress) {
      this.tryToRemoveFileCopyFromQueue(event);
    }
    if (event.newStatus == FileCopyStatus.InProgress) {
      this.tryToUpdateFileCopyStatusInQueue(event);
    }
  }

  private tryToRemoveFileCopyFromQueue(event: FileCopyStatusChangedEvent) {
    const foundFileCopyWithContext: FileCopyWithContext | undefined =
      this.findFileCopyWithContextInQueue(event.fileCopyId);
    if (foundFileCopyWithContext) {
      const index: number | undefined = this.fileCopyWithContextPage?.content?.indexOf(foundFileCopyWithContext);
      if (index !== -1 && index !== undefined && index !== null) {
        this.fileCopyWithContextPage?.content?.splice(index, 1);
      }
    }
  }

  private findFileCopyWithContextInQueue(fileCopyId: string): FileCopyWithContext | undefined {
    return this.fileCopyWithContextPage?.content
      ?.find(fileCopyWithContext => fileCopyWithContext?.fileCopy.id == fileCopyId);
  }

  private tryToUpdateFileCopyStatusInQueue(event: FileCopyStatusChangedEvent) {
    const foundFileCopyWithContext: FileCopyWithContext | undefined =
      this.findFileCopyWithContextInQueue(event.fileCopyId);
    if (foundFileCopyWithContext) {
      foundFileCopyWithContext.fileCopy.status = FileCopyStatus.InProgress;
    }
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
}
