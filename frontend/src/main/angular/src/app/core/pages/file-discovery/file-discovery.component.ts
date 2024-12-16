import {Component, OnDestroy, OnInit} from '@angular/core';
import {
  FileBackupStatus,
  FileDiscoveredEvent,
  FileDiscoveryClient,
  FileDiscoveryProgressUpdateEvent,
  FileDiscoveryStatus,
  FileDiscoveryStatusChangedEvent,
  FileDiscoveryWebSocketTopics,
  GameFile,
  GameFileProcessingStatus,
  GameFilesClient,
  PageGameFile
} from "@backend";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {IMessage} from "@stomp/stompjs";
import {catchError} from "rxjs/operators";
import {firstValueFrom, Subscription} from "rxjs";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {PageHeaderComponent} from '@app/shared/components/page-header/page-header.component';
import {LoadedContentComponent} from '@app/shared/components/loaded-content/loaded-content.component';
import {DatePipe, NgFor, NgIf, NgStyle} from '@angular/common';
import {FileDiscoveryStatusBadgeComponent} from './file-discovery-status-badge/file-discovery-status-badge.component';
import {NewDiscoveredFilesBadgeComponent} from './new-discovered-files-badge/new-discovered-files-badge.component';
import {ButtonComponent} from '@app/shared/components/button/button.component';
import {TableComponent} from '@app/shared/components/table/table.component';
import {TableColumnDirective} from '@app/shared/components/table/column-directive/table-column.directive';
import {CardComponent} from "@app/shared/components/card/card.component";
import {PaginationComponent} from "@app/shared/components/pagination/pagination.component";

@Component({
  selector: 'app-file-discovery',
  templateUrl: './file-discovery.component.html',
  styleUrls: ['./file-discovery.component.scss'],
  standalone: true,
  imports: [PageHeaderComponent, LoadedContentComponent, NgFor, FileDiscoveryStatusBadgeComponent, NewDiscoveredFilesBadgeComponent, ButtonComponent, NgIf, NgStyle, TableComponent, TableColumnDirective, DatePipe, CardComponent, PaginationComponent]
})
export class FileDiscoveryComponent implements OnInit, OnDestroy {

  discoveredFiles?: PageGameFile;
  newestDiscovered?: FileDiscoveredEvent;
  newDiscoveredCount: number = 0;
  infoIsLoading: boolean = false;
  filesAreLoading: boolean = false;
  discoveryStatusByGameProviderId: Map<string, boolean> = new Map<string, boolean>();
  discoveryProgressByGameProviderId: Map<string, FileDiscoveryProgressUpdateEvent>
    = new Map<string, FileDiscoveryProgressUpdateEvent>();
  discoveryStateUnknown: boolean = true;

  discoveredFilesPageNumber: number = 1;
  discoveredFilesPageSize: number = 3;

  private readonly subscriptions: Subscription[] = [];

  constructor(private readonly fileDiscoveryClient: FileDiscoveryClient,
              private readonly gameFilesClient: GameFilesClient,
              private readonly messageService: MessagesService,
              private readonly notificationService: NotificationService) {
  }

  asGameFile = (gameFile: GameFile) => gameFile;

  ngOnInit(): void {
    this.subscriptions.push(
      this.messageService.watch(FileDiscoveryWebSocketTopics.FileDiscovered)
        .subscribe(p => this.onFileDiscovered(p)),
      this.messageService.watch(FileDiscoveryWebSocketTopics.FileStatusChanged)
        .subscribe(p => this.onDiscoveryStatusChanged(p)),
      this.messageService.watch(FileDiscoveryWebSocketTopics.ProgressUpdate)
        .subscribe(p => this.onProgressUpdated(p))
    )

    this.refreshInfo();

    this.refreshDiscoveredFiles()();
  }

  private onFileDiscovered(payload: IMessage) {
    this.newestDiscovered = JSON.parse(payload.body);
    this.newDiscoveredCount++;
  }

  private onDiscoveryStatusChanged(payload: IMessage) {
    const status: FileDiscoveryStatusChangedEvent = JSON.parse(payload.body);
    this.updateDiscoveryStatus(status);
  }

  private onProgressUpdated(payload: IMessage) {
    const progress: FileDiscoveryProgressUpdateEvent = JSON.parse(payload.body);
    this.discoveryProgressByGameProviderId.set(progress.gameProviderId as string, progress);
  }

  private updateDiscoveryStatus(status: FileDiscoveryStatusChangedEvent) {
    this.discoveryStatusByGameProviderId.set(status.gameProviderId as string, status.isInProgress as boolean);
    this.discoveryStateUnknown = false;
  }

  private refreshInfo() {
    this.infoIsLoading = true;
    this.fileDiscoveryClient.getStatuses()
      .subscribe(ss => {
        ss.forEach((s) => this.updateDiscoveryStatus(s))
        this.infoIsLoading = false;
      });
  }

  refreshDiscoveredFiles(): () => Promise<void> {
    return async () => {
      this.filesAreLoading = true;
      const page = this.discoveredFilesPageNumber - 1;
      const size = this.discoveredFilesPageSize;
      try {
        const gameFilePage = await firstValueFrom(
          this.gameFilesClient.getGameFiles(GameFileProcessingStatus.Discovered, {
            page: page,
            size: size
          }));
        this.updateDiscoveredFiles(gameFilePage);
      } catch (error) {
        this.notificationService.showFailure('Error fetching discovered files', undefined, error);
      } finally {
        this.filesAreLoading = false;
      }
    }
  }

  private updateDiscoveredFiles(gameFilePage: PageGameFile) {
    this.discoveredFiles = gameFilePage;
    this.newDiscoveredCount = 0;
    this.filesAreLoading = false;
  }

  startDiscovery(): () => Promise<void> {
    return async () => {
      this.discoveryStateUnknown = true;
      try {
        await firstValueFrom(this.fileDiscoveryClient.startDiscovery());
      } catch (error) {
        this.notificationService.showFailure('Error starting discovery', undefined, error);
      }
    };
  }

  stopDiscovery(): () => Promise<void> {
    return async () => {
      this.discoveryStateUnknown = true;
      try {
        await firstValueFrom(this.fileDiscoveryClient.stopDiscovery());
      } catch (error) {
        this.notificationService.showFailure('Error stopping discovery', undefined, error);
      }
    };
  }

  enqueueFile(gameFile: GameFile): () => Promise<void> {
    return async () => {
      gameFile.fileBackup.status = FileBackupStatus.Enqueued;
      try {
        await firstValueFrom(this.gameFilesClient.enqueueFileBackup(gameFile.id).pipe(catchError(e => {
          gameFile.fileBackup.status = FileBackupStatus.Discovered;
          throw e;
        })));
        this.notificationService.showSuccess("File backup enqueued");
      } catch (error) {
        this.notificationService.showFailure(
          'An error occurred while trying to enqueue a file', undefined, gameFile, error);
      }
    }
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(s => s.unsubscribe());
  }

  getStatuses(): FileDiscoveryStatus[] {
    if (this.discoveryStatusByGameProviderId.size === 0) {
      return [];
    }

    return Array.from(this.discoveryStatusByGameProviderId)
      .map(([gameProviderId, isInProgress]) => {
        return {
          gameProviderId: gameProviderId,
          isInProgress: isInProgress
        };
      });
  }

  getProgressList(): FileDiscoveryProgressUpdateEvent[] {
    if (this.discoveryProgressByGameProviderId.size === 0) {
      return [];
    }
    return Array.from(this.discoveryProgressByGameProviderId)
      .map(([gameProviderId, progress]) => {
        return progress;
      });
  }

  discoveryOngoing(): boolean {
    if (this.discoveryStatusByGameProviderId.size === 0) {
      return false;
    }

    return Array.from(this.discoveryStatusByGameProviderId)
      .some(([gameProviderId, inProgress]) => inProgress);
  }

  discoverFilesFor(gameProviderId?: string): () => Promise<void> {
    return async () => {
      this.notificationService.showFailure('Per-provider file discovery start not yet implemented');
    };
  }

  isInProgress(gameProviderId: string): boolean {
    return !!this.discoveryStatusByGameProviderId.get(gameProviderId);
  }

  protected readonly FileBackupStatus = FileBackupStatus;
}
