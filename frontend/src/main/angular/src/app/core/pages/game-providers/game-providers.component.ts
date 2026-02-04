import {Component, computed, signal} from '@angular/core';
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
import {MessageService} from "@app/shared/backend/services/message.service";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {firstValueFrom} from "rxjs";
import {ButtonComponent} from "@app/shared/components/button/button.component";
import {LoadedContentComponent} from "@app/shared/components/loaded-content/loaded-content.component";
import {AutoLayoutComponent} from "@app/shared/components/auto-layout/auto-layout.component";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";

@Component({
  selector: 'app-game-providers',
  templateUrl: './game-providers.component.html',
  styleUrls: ['./game-providers.component.scss'],
  imports: [PageHeaderComponent, GogAuthComponent, SectionComponent, ButtonComponent, LoadedContentComponent, AutoLayoutComponent]
})
export class GameProvidersComponent {

  infoIsLoading = signal(false);
  discoveryIsInProgressByGameProviderId = signal<Map<string, boolean>>(new Map());
  discoveryOverviewsByGameProviderId =
    signal<Map<string, GameContentDiscoveryOverview | undefined>>(new Map());
  discoveryStatusUnknownByGameProviderId = signal<Map<string, boolean>>(new Map());

  anyDiscoveryStatusIsUnknown = computed(() =>
    Array.from(this.discoveryStatusUnknownByGameProviderId().values()).some(Boolean)
  );

  discoveryOngoing = computed(() =>
    Array.from(this.discoveryIsInProgressByGameProviderId().values()).some(Boolean)
  );

  constructor(private readonly gameContentDiscoveryClient: GameContentDiscoveryClient,
              private readonly messageService: MessageService,
              private readonly notificationService: NotificationService) {
    this.messageService.watch<GameContentDiscoveryStartedEvent>(
      GameContentDiscoveryWebSocketTopics.TopicGameContentDiscoveryDiscoveryStarted)
      .pipe(takeUntilDestroyed())
      .subscribe(event => this.onDiscoveryStarted(event));
    this.messageService.watch<GameContentDiscoveryStoppedEvent>(
      GameContentDiscoveryWebSocketTopics.TopicGameContentDiscoveryDiscoveryStopped)
      .pipe(takeUntilDestroyed())
      .subscribe(event => this.onDiscoveryStopped(event));
    this.messageService.watch<GameContentDiscoveryProgressChangedEvent>(
      GameContentDiscoveryWebSocketTopics.TopicGameContentDiscoveryProgressUpdate)
      .pipe(takeUntilDestroyed())
      .subscribe(event => this.onDiscoveryProgressChanged(event));

    this.refreshInfo();
  }

  getGogOverview(): GameContentDiscoveryOverview | undefined {
    return this.getOverview('GOG');
  }

  getOverview(gameProviderId: string): GameContentDiscoveryOverview | undefined {
    const overview = this.discoveryOverviewsByGameProviderId().get(gameProviderId);
    if (!this.discoveryIsInProgressByGameProviderId().has(gameProviderId)) {
      return undefined;
    }
    return overview;
  }

  onClickStartGameContentDiscovery(): () => Promise<void> {
    return this.startGameContentDiscoveryClickHandler;
  }

  async startGameContentDiscovery(): Promise<void> {
    this.setAllDiscoveryStatusesUnknown();
    try {
      await firstValueFrom(this.gameContentDiscoveryClient.startGameContentDiscovery());
    } catch (error) {
      this.notificationService.showFailure('Error starting discovery', error);
    }
  }

  onClickStopGameContentDiscovery(): () => Promise<void> {
    return this.stopGameContentDiscoveryClickHandler;
  }

  async stopGameContentDiscovery(): Promise<void> {
    this.setAllDiscoveryStatusesUnknown();
    try {
      await firstValueFrom(this.gameContentDiscoveryClient.stopGameContentDiscovery());
      this.refreshInfo();
    } catch (error) {
      this.notificationService.showFailure('Error stopping discovery', error);
    }
  }

  private readonly startGameContentDiscoveryClickHandler: () => Promise<void> =
    async () => this.startGameContentDiscovery();

  private readonly stopGameContentDiscoveryClickHandler: () => Promise<void> =
    async () => this.stopGameContentDiscovery();

  private onDiscoveryStarted(event: GameContentDiscoveryStartedEvent) {
    this.discoveryIsInProgressByGameProviderId.update(map => new Map(map).set(event.gameProviderId, true));
    this.discoveryStatusUnknownByGameProviderId.update(map => new Map(map).set(event.gameProviderId, false));

    this.discoveryOverviewsByGameProviderId.update(map => {
      const newMap = new Map(map);
      const overview = newMap.get(event.gameProviderId);
      if (overview) {
        newMap.set(event.gameProviderId, {...overview, isInProgress: true});
      }
      return newMap;
    });
  }

  private onDiscoveryStopped(event: GameContentDiscoveryStoppedEvent) {
    this.discoveryIsInProgressByGameProviderId.update(map => new Map(map).set(event.gameProviderId, false));
    this.discoveryOverviewsByGameProviderId.update(map => {
      const newMap = new Map(map);
      let overview = newMap.get(event.gameProviderId);
      overview ??= {
        gameProviderId: event.gameProviderId,
        isInProgress: false
      };
      newMap.set(event.gameProviderId, {
        ...overview,
        isInProgress: false,
        progress: undefined,
        lastDiscoveryResult: event.discoveryResult
      });
      return newMap;
    });
    this.discoveryStatusUnknownByGameProviderId.update(map => new Map(map).set(event.gameProviderId, false));
  }

  private onDiscoveryProgressChanged(event: GameContentDiscoveryProgressChangedEvent) {
    this.discoveryStatusUnknownByGameProviderId.update(map => new Map(map).set(event.gameProviderId, false));
    this.discoveryOverviewsByGameProviderId.update(map => {
      const newMap = new Map(map);
      const overview = newMap.get(event.gameProviderId);
      if (overview) {
        newMap.set(event.gameProviderId, {
          ...overview,
          progress: {
            percentage: event.percentage,
            timeLeftSeconds: event.timeLeftSeconds,
            gamesDiscovered: event.gamesDiscovered,
            gameFilesDiscovered: event.gameFilesDiscovered
          }
        });
      }
      return newMap;
    });
  }

  private refreshInfo() {
    this.infoIsLoading.set(true);
    this.gameContentDiscoveryClient.getGameContentDiscoveryOverviews()
      .subscribe({
        next: discoveryOverviews => {
          this.discoveryIsInProgressByGameProviderId.update(map => {
            const newMap = new Map(map);
            discoveryOverviews.forEach(o => {
              newMap.set(o.gameProviderId, o.isInProgress)
            });
            return newMap;
          });
          this.discoveryOverviewsByGameProviderId.update(map => {
            const newMap = new Map(map);
            discoveryOverviews.forEach(o => {
              newMap.set(o.gameProviderId, o);
            });
            return newMap;
          });
          this.discoveryStatusUnknownByGameProviderId.update(map => {
            const newMap = new Map(map);
            discoveryOverviews.forEach(o => {
              newMap.set(o.gameProviderId, false)
            });
            return newMap;
          });
          this.infoIsLoading.set(false);
        },
        error: error => {
          this.notificationService.showFailure('Error fetching discovery overviews', error);
          this.infoIsLoading.set(false);
        }
      });
  }

  private setAllDiscoveryStatusesUnknown() {
    this.discoveryStatusUnknownByGameProviderId.update(map => {
      const newMap = new Map(map);
      for (const gameProviderId of newMap.keys()) {
        newMap.set(gameProviderId, true);
      }
      return newMap;
    });
  }
}
