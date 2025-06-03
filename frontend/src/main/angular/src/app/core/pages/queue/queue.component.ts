import {Component, OnDestroy, OnInit} from '@angular/core';
import {
  FileBackupMessageTopics,
  FileCopiesClient,
  FileCopy,
  FileCopyProcessingStatus,
  FileCopyStatus,
  FileCopyStatusChangedEvent,
  PageFileCopy
} from "@backend";
import {firstValueFrom, Subscription} from "rxjs";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {Message} from "@stomp/stompjs";
import {PageHeaderComponent} from "@app/shared/components/page-header/page-header.component";
import {SectionComponent} from "@app/shared/components/section/section.component";
import {LoadedContentComponent} from "@app/shared/components/loaded-content/loaded-content.component";
import {ButtonComponent} from "@app/shared/components/button/button.component";
import {TableComponent} from "@app/shared/components/table/table.component";
import {PaginationComponent} from "@app/shared/components/pagination/pagination.component";
import {TableColumnDirective} from "@app/shared/components/table/column-directive/table-column.directive";
import {NgForOf, NgIf} from "@angular/common";

@Component({
  selector: 'app-queue',
  standalone: true,
  imports: [
    PageHeaderComponent,
    SectionComponent,
    LoadedContentComponent,
    ButtonComponent,
    TableComponent,
    PaginationComponent,
    TableColumnDirective,
    NgForOf,
    NgIf
  ],
  templateUrl: './queue.component.html',
  styleUrl: './queue.component.scss'
})
export class QueueComponent implements OnInit, OnDestroy {

  asFileCopy = (fileCopy: FileCopy) => fileCopy;

  fileCopiesAreLoading: boolean = false;
  fileCopyPage?: PageFileCopy;
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
    if (event.newStatus !== FileCopyStatus.Enqueued) {
      this.tryToRemoveFileCopyFromQueue(event);
    }
  }

  private tryToRemoveFileCopyFromQueue(event: FileCopyStatusChangedEvent) {
    const foundFileCopy: FileCopy | undefined = this.findFileCopyInEnqueuedDownloads(event);
    if (foundFileCopy) {
      const index: number | undefined = this.fileCopyPage?.content?.indexOf(foundFileCopy);
      if (index !== -1 && index !== undefined && index !== null) {
        this.fileCopyPage?.content?.splice(index, 1);
      }
    }
  }

  private findFileCopyInEnqueuedDownloads(event: FileCopyStatusChangedEvent) {
    return this.fileCopyPage?.content
      ?.find(fileCopy => fileCopy?.id == event.fileCopyId);
  }

  onClickRefreshEnqueuedFileCopies(): () => Promise<void> {
    return async () => this.refreshEnqueuedFileCopies();
  }

  async refreshEnqueuedFileCopies(): Promise<void> {
    this.fileCopiesAreLoading = true;

    try {
      this.fileCopyPage = await firstValueFrom(
        this.fileCopiesClient.getFileCopiesWithStatus(FileCopyProcessingStatus.Enqueued, {
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
