import {Component, OnDestroy, OnInit} from '@angular/core';
import {PageHeaderComponent} from '@app/shared/components/page-header/page-header.component';
import {GogAuthComponent} from '@app/gog/pages/auth/gog-auth/gog-auth.component';
import {SectionComponent} from "@app/shared/components/section/section.component";
import {
  GameContentDiscoveryClient,
  GameContentDiscoveryProgressUpdateEvent,
  GameContentDiscoveryStatus,
  GameContentDiscoveryStatusChangedEvent,
  GameContentDiscoveryWebSocketTopics,
  GameFileDiscoveredEvent
} from "@backend";
import {IMessage} from "@stomp/stompjs";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {firstValueFrom, Subscription} from "rxjs";
import {ButtonComponent} from "@app/shared/components/button/button.component";
import {LoadedContentComponent} from "@app/shared/components/loaded-content/loaded-content.component";
import {
  NewDiscoveredFilesBadgeComponent
} from "@app/core/pages/file-discovery/new-discovered-files-badge/new-discovered-files-badge.component";

@Component({
  selector: 'app-game-providers',
  templateUrl: './game-providers.component.html',
  styleUrls: ['./game-providers.component.scss'],
  standalone: true,
  imports: [PageHeaderComponent, GogAuthComponent, SectionComponent, ButtonComponent, LoadedContentComponent,
    NewDiscoveredFilesBadgeComponent]
})
export class GameProvidersComponent implements OnInit, OnDestroy {

  newestGameFileDiscoveredEvent?: GameFileDiscoveredEvent;
  newDiscoveredGameFilesCount: number = 0;
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
    this.newestGameFileDiscoveredEvent = JSON.parse(payload.body);
    this.newDiscoveredGameFilesCount++;
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
    this.discoveryStatusByGameProviderId.set(status.gameProviderId, status.isInProgress);
    this.discoveryStateUnknown = false;
  }

  private refreshInfo() {
    this.infoIsLoading = true;
    this.gameContentDiscoveryClient.getStatuses()
      .subscribe(gameContentDiscoveryStatuses => {
        gameContentDiscoveryStatuses
          .forEach((status) => this.updateDiscoveryStatus(status))
        this.infoIsLoading = false;
      });
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

  getGogProgress(): GameContentDiscoveryProgressUpdateEvent | undefined {
    return this.getProgress('GOG');
  }

  getProgress(gameProviderId: string): GameContentDiscoveryProgressUpdateEvent | undefined {
    const progress = this.discoveryProgressByGameProviderId.get(gameProviderId);
    if (progress?.percentage == 100) {
      return undefined;
    }
    if (!this.discoveryStatusByGameProviderId.has(gameProviderId)) {
      return undefined;
    }
    return progress;
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
      this.refreshInfo();
    } catch (error) {
      this.notificationService.showFailure('Error stopping discovery', error);
    }
  }

  discoveryOngoing(): boolean {
    return Array.from(this.discoveryStatusByGameProviderId)
      .some(([gameProviderId, inProgress]) => inProgress);
  }
}
