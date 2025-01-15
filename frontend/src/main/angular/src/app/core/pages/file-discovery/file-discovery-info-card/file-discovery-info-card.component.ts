import {Component, OnDestroy, OnInit} from '@angular/core';
import {
  FileBackupStatus,
  FileDiscoveredEvent,
  FileDiscoveryClient,
  FileDiscoveryProgressUpdateEvent,
  FileDiscoveryStatus,
  FileDiscoveryStatusChangedEvent,
  FileDiscoveryWebSocketTopics
} from "@backend";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {IMessage} from "@stomp/stompjs";
import {firstValueFrom, Subscription} from "rxjs";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {LoadedContentComponent} from '@app/shared/components/loaded-content/loaded-content.component';
import {DatePipe, NgForOf, NgIf, NgStyle} from '@angular/common';
import {ButtonComponent} from '@app/shared/components/button/button.component';
import {CardComponent} from "@app/shared/components/card/card.component";
import {
  FileDiscoveryStatusBadgeComponent
} from "@app/core/pages/file-discovery/file-discovery-status-badge/file-discovery-status-badge.component";
import {
  NewDiscoveredFilesBadgeComponent
} from "@app/core/pages/file-discovery/new-discovered-files-badge/new-discovered-files-badge.component";

@Component({
  selector: 'app-file-discovery-info-card',
  standalone: true,
  imports: [
    ButtonComponent,
    CardComponent,
    DatePipe,
    FileDiscoveryStatusBadgeComponent,
    LoadedContentComponent,
    NewDiscoveredFilesBadgeComponent,
    NgForOf,
    NgIf,
    NgStyle
  ],
  templateUrl: './file-discovery-info-card.component.html',
  styleUrl: './file-discovery-info-card.component.scss'
})
export class FileDiscoveryInfoCardComponent implements OnInit, OnDestroy {

  FileBackupStatus = FileBackupStatus;

  newestDiscovered?: FileDiscoveredEvent;
  newDiscoveredCount: number = 0;
  infoIsLoading: boolean = false;
  discoveryStatusByGameProviderId: Map<string, boolean> = new Map<string, boolean>();
  discoveryProgressByGameProviderId: Map<string, FileDiscoveryProgressUpdateEvent>
    = new Map<string, FileDiscoveryProgressUpdateEvent>();
  discoveryStateUnknown: boolean = true;

  private readonly subscriptions: Subscription[] = [];

  constructor(private readonly fileDiscoveryClient: FileDiscoveryClient,
              private readonly messageService: MessagesService,
              private readonly notificationService: NotificationService) {
  }

  ngOnInit(): void {
    this.subscriptions.push(
      this.messageService.watch(FileDiscoveryWebSocketTopics.Discovered)
        .subscribe(p => this.onFileDiscovered(p)),
      this.messageService.watch(FileDiscoveryWebSocketTopics.DiscoveryStatusChanged)
        .subscribe(p => this.onDiscoveryStatusChanged(p)),
      this.messageService.watch(FileDiscoveryWebSocketTopics.DiscoveryProgressChanged)
        .subscribe(p => this.onDiscoveryProgressChanged(p))
    )

    this.refreshInfo();
  }

  private onFileDiscovered(payload: IMessage) {
    this.newestDiscovered = JSON.parse(payload.body);
    this.newDiscoveredCount++;
  }

  private onDiscoveryStatusChanged(payload: IMessage) {
    const status: FileDiscoveryStatusChangedEvent = JSON.parse(payload.body);
    this.updateDiscoveryStatus(status);
  }

  private onDiscoveryProgressChanged(payload: IMessage) {
    const progress: FileDiscoveryProgressUpdateEvent = JSON.parse(payload.body);
    this.discoveryProgressByGameProviderId.set(progress.gameProviderId, progress);
  }

  private updateDiscoveryStatus(status: FileDiscoveryStatusChangedEvent) {
    this.discoveryStatusByGameProviderId.set(status.gameProviderId, status.isInProgress as boolean);
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

  onClickStartDiscovery(): () => Promise<void> {
    return async () => this.startDiscovery();
  }

  async startDiscovery(): Promise<void> {
    this.discoveryStateUnknown = true;
    try {
      await firstValueFrom(this.fileDiscoveryClient.startDiscovery());
    } catch (error) {
      this.notificationService.showFailure('Error starting discovery', error);
    }
  }

  onClickStopDiscovery(): () => Promise<void> {
    return async () => this.stopDiscovery();
  }

  async stopDiscovery(): Promise<void> {
    this.discoveryStateUnknown = true;
    try {
      await firstValueFrom(this.fileDiscoveryClient.stopDiscovery());
    } catch (error) {
      this.notificationService.showFailure('Error stopping discovery', error);
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

  onClickDiscoverFilesFor(gameProviderId?: string): () => Promise<void> {
    return async () => this.discoverFilesFor(gameProviderId);
  }

  async discoverFilesFor(gameProviderId?: string): Promise<void> {
    this.notificationService.showFailure('Per-provider file discovery start not yet implemented');
  }

  isInProgress(gameProviderId: string): boolean {
    return !!this.discoveryStatusByGameProviderId.get(gameProviderId);
  }
}
