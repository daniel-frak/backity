import {Component, OnDestroy, OnInit} from '@angular/core';
import {PageHeaderComponent} from '@app/shared/components/page-header/page-header.component';
import {GogAuthComponent} from '@app/gog/pages/auth/gog-auth/gog-auth.component';
import {SectionComponent} from "@app/shared/components/section/section.component";
import {
  GameContentDiscoveryClient,
  GameContentDiscoveryOverview,
  GameContentDiscoveryProgressChangedEvent,
  GameContentDiscoveryStartedEvent,
  GameContentDiscoveryStoppedEvent,
  GameContentDiscoveryWebSocketTopics
} from "@backend";
import {IMessage} from "@stomp/stompjs";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {firstValueFrom, Subscription} from "rxjs";
import {ButtonComponent} from "@app/shared/components/button/button.component";
import {LoadedContentComponent} from "@app/shared/components/loaded-content/loaded-content.component";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-game-providers',
  templateUrl: './game-providers.component.html',
  styleUrls: ['./game-providers.component.scss'],
  standalone: true,
  imports: [PageHeaderComponent, GogAuthComponent, SectionComponent, ButtonComponent, LoadedContentComponent, NgIf]
})
export class GameProvidersComponent implements OnInit, OnDestroy {

  infoIsLoading: boolean = false;
  discoveryIsInProgressByGameProviderId: Map<string, boolean> = new Map();
  discoveryOverviewsByGameProviderId: Map<string, GameContentDiscoveryOverview | undefined> = new Map();
  discoveryStatusUnknownByGameProviderId: Map<string, boolean> = new Map();

  private readonly subscriptions: Subscription[] = [];

  constructor(private readonly gameContentDiscoveryClient: GameContentDiscoveryClient,
              private readonly messageService: MessagesService,
              private readonly notificationService: NotificationService) {
  }

  ngOnInit(): void {
    this.subscriptions.push(
      this.messageService.watch(GameContentDiscoveryWebSocketTopics.DiscoveryStarted)
        .subscribe(p => this.onDiscoveryStarted(p)),
      this.messageService.watch(GameContentDiscoveryWebSocketTopics.DiscoveryStopped)
        .subscribe(p => this.onDiscoveryStopped(p)),
      this.messageService.watch(GameContentDiscoveryWebSocketTopics.ProgressUpdate)
        .subscribe(p => this.onDiscoveryProgressChanged(p))
    )

    this.refreshInfo();
  }

  private onDiscoveryStarted(payload: IMessage) {
    const event: GameContentDiscoveryStartedEvent = JSON.parse(payload.body);

    this.discoveryIsInProgressByGameProviderId.set(event.gameProviderId, true);
    this.discoveryStatusUnknownByGameProviderId.set(event.gameProviderId, false);

    const overview: GameContentDiscoveryOverview | undefined =
      this.discoveryOverviewsByGameProviderId.get(event.gameProviderId);
    if (overview) {
      overview.isInProgress = true;
    }
  }

  private onDiscoveryStopped(payload: IMessage) {
    const event: GameContentDiscoveryStoppedEvent = JSON.parse(payload.body);

    this.discoveryIsInProgressByGameProviderId.set(event.gameProviderId, false);
    let overview: GameContentDiscoveryOverview | undefined =
      this.discoveryOverviewsByGameProviderId.get(event.gameProviderId);
    if (!overview) {
      overview = {
        gameProviderId: event.gameProviderId,
        isInProgress: false
      };
      this.discoveryOverviewsByGameProviderId.set(event.gameProviderId, overview);
    }
    overview.isInProgress = false;
    overview.progress = undefined;
    overview.lastDiscoveryResult = event.discoveryResult;
    this.discoveryStatusUnknownByGameProviderId.set(event.gameProviderId, false);
  }

  private onDiscoveryProgressChanged(payload: IMessage) {
    const event: GameContentDiscoveryProgressChangedEvent = JSON.parse(payload.body);
    const overview: GameContentDiscoveryOverview | undefined =
      this.discoveryOverviewsByGameProviderId.get(event.gameProviderId);
    this.discoveryStatusUnknownByGameProviderId.set(event.gameProviderId, false);
    if (overview == undefined) {
      return;
    }
    overview.progress = {
      percentage: event.percentage,
      timeLeftSeconds: event.timeLeftSeconds,
      gamesDiscovered: event.gamesDiscovered,
      gameFilesDiscovered: event.gameFilesDiscovered
    }
  }

  private refreshInfo() {
    this.infoIsLoading = true;
    this.gameContentDiscoveryClient.getDiscoveryOverviews()
      .subscribe(discoveryOverviews => {
        for (const discoveryOverview of discoveryOverviews) {
          this.discoveryIsInProgressByGameProviderId.set(discoveryOverview.gameProviderId, discoveryOverview.isInProgress);
          this.discoveryOverviewsByGameProviderId.set(discoveryOverview.gameProviderId, discoveryOverview);
          this.discoveryStatusUnknownByGameProviderId.set(discoveryOverview.gameProviderId, false);
        }
        this.infoIsLoading = false;
      });
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(s => s.unsubscribe());
  }

  getGogOverview(): GameContentDiscoveryOverview | undefined {
    return this.getOverview('GOG');
  }

  getOverview(gameProviderId: string): GameContentDiscoveryOverview | undefined {
    const overview = this.discoveryOverviewsByGameProviderId.get(gameProviderId);
    if (!this.discoveryIsInProgressByGameProviderId.has(gameProviderId)) {
      return undefined;
    }
    return overview;
  }

  onClickStartDiscovery(): () => Promise<void> {
    return async () => this.startDiscovery();
  }

  async startDiscovery(): Promise<void> {
    this.setAllDiscoveryStatusesUnknown();
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
    this.setAllDiscoveryStatusesUnknown();
    try {
      await firstValueFrom(this.gameContentDiscoveryClient.stopDiscovery());
      this.refreshInfo();
    } catch (error) {
      this.notificationService.showFailure('Error stopping discovery', error);
    }
  }

  private setAllDiscoveryStatusesUnknown() {
    for (const gameProviderId of this.discoveryStatusUnknownByGameProviderId.keys()) {
      this.discoveryStatusUnknownByGameProviderId.set(gameProviderId, true);
    }
  }

  anyDiscoveryStatusIsUnknown(): boolean {
    return Array.from(this.discoveryStatusUnknownByGameProviderId)
      .some(([_, isUnknown]) => isUnknown);
  }

  discoveryOngoing(): boolean {
    return Array.from(this.discoveryIsInProgressByGameProviderId)
      .some(([_, inProgress]) => inProgress);
  }
}
