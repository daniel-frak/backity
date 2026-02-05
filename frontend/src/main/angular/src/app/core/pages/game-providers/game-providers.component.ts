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
  styleUrl: './game-providers.component.scss',
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
    return this.discoveryOverviewsByGameProviderId().get(gameProviderId);
  }

  onClickStartGameContentDiscovery(): () => Promise<void> {
    return async () => this.startGameContentDiscovery();
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
    return async () => this.stopGameContentDiscovery();
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

  private onDiscoveryStarted(event: GameContentDiscoveryStartedEvent) {
    this.updateDiscoveryState(event.gameProviderId, true, {
      isInProgress: true
    });
  }

  private onDiscoveryStopped(event: GameContentDiscoveryStoppedEvent) {
    this.updateDiscoveryState(event.gameProviderId, false, {
      isInProgress: false,
      progress: undefined,
      lastDiscoveryResult: event.discoveryResult
    });
  }

  private onDiscoveryProgressChanged(event: GameContentDiscoveryProgressChangedEvent) {
    this.updateDiscoveryState(event.gameProviderId, true, {
      isInProgress: true,
      progress: {
        percentage: event.percentage,
        timeLeftSeconds: event.timeLeftSeconds,
        gamesDiscovered: event.gamesDiscovered,
        gameFilesDiscovered: event.gameFilesDiscovered
      }
    });
  }

  private updateDiscoveryState(gameProviderId: string, isInProgress: boolean,
                               overviewUpdate: Partial<GameContentDiscoveryOverview>) {
    this.discoveryIsInProgressByGameProviderId.update(map => new Map(map).set(gameProviderId, isInProgress));
    this.discoveryStatusUnknownByGameProviderId.update(map => new Map(map).set(gameProviderId, false));
    this.discoveryOverviewsByGameProviderId.update(map => {
      const newMap = new Map(map);
      const overview = newMap.get(gameProviderId) ?? {
        gameProviderId: gameProviderId,
        isInProgress: isInProgress
      };
      newMap.set(gameProviderId, {...overview, ...overviewUpdate});
      return newMap;
    });
  }

  private refreshInfo() {
    if (this.infoIsLoading()) {
      return;
    }
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
