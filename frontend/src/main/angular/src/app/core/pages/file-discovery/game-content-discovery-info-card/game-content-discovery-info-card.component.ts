import {Component, OnDestroy, OnInit} from '@angular/core';
import {
  FileBackupStatus,
  FileDiscoveredEvent,
  GameContentDiscoveryClient,
  GameContentDiscoveryProgressUpdateEvent,
  GameContentDiscoveryStatus,
  GameContentDiscoveryStatusChangedEvent,
  GameContentDiscoveryWebSocketTopics
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
  GameContentDiscoveryStatusBadgeComponent
} from "@app/core/pages/file-discovery/game-content-discovery-status-badge/game-content-discovery-status-badge.component";
import {
  NewDiscoveredFilesBadgeComponent
} from "@app/core/pages/file-discovery/new-discovered-files-badge/new-discovered-files-badge.component";

@Component({
  selector: 'app-game-content-discovery-info-card',
  standalone: true,
  imports: [
    ButtonComponent,
    CardComponent,
    DatePipe,
    GameContentDiscoveryStatusBadgeComponent,
    LoadedContentComponent,
    NewDiscoveredFilesBadgeComponent,
    NgForOf,
    NgIf,
    NgStyle
  ],
  templateUrl: './game-content-discovery-info-card.component.html',
  styleUrl: './game-content-discovery-info-card.component.scss'
})
export class GameContentDiscoveryInfoCardComponent implements OnInit, OnDestroy {

  FileBackupStatus = FileBackupStatus;

  newestDiscoveredFile?: FileDiscoveredEvent;
  newDiscoveredFilesCount: number = 0;
  infoIsLoading: boolean = false;
  discoveryStatusByGameProviderId: Map<string, boolean> = new Map<string, boolean>();
  discoveryProgressByGameProviderId: Map<string, GameContentDiscoveryProgressUpdateEvent>
    = new Map<string, GameContentDiscoveryProgressUpdateEvent>();
  discoveryStateUnknown: boolean = true;

  private readonly subscriptions: Subscription[] = [];

  constructor(private readonly gameContentDiscoveryClient: GameContentDiscoveryClient,
              private readonly messageService: MessagesService,
              private readonly notificationService: NotificationService) {
  }

  ngOnInit(): void {
    this.subscriptions.push(
      this.messageService.watch(GameContentDiscoveryWebSocketTopics.FileDiscovered)
        .subscribe(p => this.onFileDiscovered(p)),
      this.messageService.watch(GameContentDiscoveryWebSocketTopics.StatusChanged)
        .subscribe(p => this.onDiscoveryStatusChanged(p)),
      this.messageService.watch(GameContentDiscoveryWebSocketTopics.ProgressUpdate)
        .subscribe(p => this.onDiscoveryProgressChanged(p))
    )

    this.refreshInfo();
  }

  private onFileDiscovered(payload: IMessage) {
    this.newestDiscoveredFile = JSON.parse(payload.body);
    this.newDiscoveredFilesCount++;
  }

  private onDiscoveryStatusChanged(payload: IMessage) {
    const status: GameContentDiscoveryStatusChangedEvent = JSON.parse(payload.body);
    this.updateDiscoveryStatus(status);
  }

  private onDiscoveryProgressChanged(payload: IMessage) {
    const progress: GameContentDiscoveryProgressUpdateEvent = JSON.parse(payload.body);
    this.discoveryProgressByGameProviderId.set(progress.gameProviderId, progress);
  }

  private updateDiscoveryStatus(status: GameContentDiscoveryStatusChangedEvent) {
    this.discoveryStatusByGameProviderId.set(status.gameProviderId, status.isInProgress as boolean);
    this.discoveryStateUnknown = false;
  }

  private refreshInfo() {
    this.infoIsLoading = true;
    this.gameContentDiscoveryClient.getStatuses()
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
      await firstValueFrom(this.gameContentDiscoveryClient.startDiscovery());
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
      await firstValueFrom(this.gameContentDiscoveryClient.stopDiscovery());
    } catch (error) {
      this.notificationService.showFailure('Error stopping discovery', error);
    }
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(s => s.unsubscribe());
  }

  getStatuses(): GameContentDiscoveryStatus[] {
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

  getProgressList(): GameContentDiscoveryProgressUpdateEvent[] {
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
    this.notificationService.showFailure('Per-provider game content discovery start not yet implemented');
  }

  isInProgress(gameProviderId: string): boolean {
    return !!this.discoveryStatusByGameProviderId.get(gameProviderId);
  }
}
