import {ComponentFixture, TestBed} from '@angular/core/testing';

import {GameProvidersComponent} from './game-providers.component';
import {GogAuthComponentStub} from "@app/gog/pages/auth/gog-auth/gog-auth.component.stub";
import {GogAuthComponent} from "@app/gog/pages/auth/gog-auth/gog-auth.component";
import {
  GameContentDiscoveryClient,
  GameContentDiscoveryOverview,
  GameContentDiscoveryProgressChangedEvent,
  GameContentDiscoveryStartedEvent,
  GameContentDiscoveryStoppedEvent,
  GameContentDiscoveryWebSocketTopics
} from "@backend";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {MessageTesting} from "@app/shared/testing/message-testing";
import {of, throwError} from "rxjs";
import {TestGameContentDiscoveryOverview} from "@app/shared/testing/objects/test-game-content-discovery-overview";
import {HttpResponse} from "@angular/common/http";
import {
  TestGameContentDiscoveryProgressChangedEvent
} from "@app/shared/testing/objects/test-game-content-discovery-progress-changed-event";
import {
  TestGameContentDiscoveryStartedEvent
} from "@app/shared/testing/objects/test-game-content-discovery-started-event";
import {
  TestGameContentDiscoveryStoppedEvent
} from "@app/shared/testing/objects/test-game-content-discovery-stopped-event";
import {AutoLayoutComponent} from "@app/shared/components/auto-layout/auto-layout.component";
import {AutoLayoutStubComponent} from "@app/shared/components/auto-layout/auto-layout.stub.component";
import SpyObj = jasmine.SpyObj;
import createSpyObj = jasmine.createSpyObj;

describe('GameProvidersComponent', () => {
  let component: GameProvidersComponent;
  let fixture: ComponentFixture<GameProvidersComponent>;

  let discoveryStartedSubscriptions: Function[];
  let discoveryStoppedSubscriptions: Function[];
  let discoveryProgressSubscriptions: Function[];
  let gameContentDiscoveryClient: SpyObj<GameContentDiscoveryClient>;
  let notificationService: SpyObj<NotificationService>;
  let messagesService: SpyObj<MessagesService>;

  beforeEach(async () => {
    discoveryStartedSubscriptions = [];
    discoveryStoppedSubscriptions = [];
    discoveryProgressSubscriptions = [];

    await TestBed.configureTestingModule({
      imports: [GameProvidersComponent],
      providers: [
        {
          provide: MessagesService,
          useValue: createSpyObj(MessagesService, ['watch'])
        },
        {
          provide: GameContentDiscoveryClient,
          useValue: createSpyObj('GameContentDiscoveryClient',
            ['getGameContentDiscoveryOverviews', 'startGameContentDiscovery', 'stopGameContentDiscovery'])
        },
        {
          provide: NotificationService,
          useValue: createSpyObj('NotificationService', ['showSuccess', 'showFailure'])
        }
      ]
    })
      .overrideComponent(GameProvidersComponent, {
        remove: {imports: [GogAuthComponent, AutoLayoutComponent]},
        add: {imports: [GogAuthComponentStub, AutoLayoutStubComponent]}
      })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GameProvidersComponent);
    component = fixture.componentInstance;

    component.discoveryIsInProgressByGameProviderId.clear();
    component.discoveryOverviewsByGameProviderId.clear();

    messagesService = TestBed.inject(MessagesService) as SpyObj<MessagesService>;
    gameContentDiscoveryClient = TestBed.inject(GameContentDiscoveryClient) as SpyObj<GameContentDiscoveryClient>;
    notificationService = TestBed.inject(NotificationService) as SpyObj<NotificationService>;

    MessageTesting.mockWatch(messagesService, (destination, callback) => {
      if (destination === GameContentDiscoveryWebSocketTopics.TopicGameContentDiscoveryDiscoveryStarted) {
        discoveryStartedSubscriptions.push(callback);
      }
      if (destination === GameContentDiscoveryWebSocketTopics.TopicGameContentDiscoveryDiscoveryStopped) {
        discoveryStoppedSubscriptions.push(callback);
      }
      if (destination === GameContentDiscoveryWebSocketTopics.TopicGameContentDiscoveryProgressUpdate) {
        discoveryProgressSubscriptions.push(callback);
      }
    });
    gameContentDiscoveryClient.getGameContentDiscoveryOverviews.and.returnValue(of([]) as any);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should refresh info on init', async () => {
    const newOverview: GameContentDiscoveryOverview = TestGameContentDiscoveryOverview.inProgress();
    gameContentDiscoveryClient.getGameContentDiscoveryOverviews.and.returnValue(of([newOverview]) as any);

    component.ngOnInit();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(component.discoveryIsInProgressByGameProviderId.get('someGameProviderId')).toBeTrue();
    expect(component.discoveryOverviewsByGameProviderId.get('someGameProviderId')).toEqual(newOverview);
    expect(component.discoveryStatusUnknownByGameProviderId.get('someGameProviderId')).toBeFalse();
  });

  it('should subscribe to discovery started events', () => {
    expect(discoveryStartedSubscriptions.length).toBe(1);
  });

  it('should subscribe to discovery stopped events', () => {
    expect(discoveryStoppedSubscriptions.length).toBe(1);
  });

  it('should subscribe to discovery progress updates', () => {
    expect(discoveryProgressSubscriptions.length).toBe(1);
  });

  function simulateDiscoveryStartedEventReceived(event: GameContentDiscoveryStartedEvent) {
    discoveryStartedSubscriptions[0]({body: JSON.stringify(event)});
  }

  it('should update discovery status given discovery started event received', () => {
    const event: GameContentDiscoveryStartedEvent = TestGameContentDiscoveryStartedEvent.any();

    simulateDiscoveryStartedEventReceived(event);

    expect(component.discoveryIsInProgressByGameProviderId.get(event.gameProviderId)).toBeTrue();
    expect(component.discoveryStatusUnknownByGameProviderId.get(event.gameProviderId)).toBeFalse();
  });

  it('should not update overview given discovery started event received but overview is undefined',
    () => {
      const event: GameContentDiscoveryStartedEvent = TestGameContentDiscoveryStartedEvent.any();
      component.discoveryOverviewsByGameProviderId.delete(event.gameProviderId);

      simulateDiscoveryStartedEventReceived(event);

      expect(component.discoveryOverviewsByGameProviderId.get(event.gameProviderId)).toBeUndefined();
    });

  it('should update overview given discovery started event received and overview exists',
    () => {
      const event: GameContentDiscoveryStartedEvent = TestGameContentDiscoveryStartedEvent.any();
      component.discoveryOverviewsByGameProviderId.set(event.gameProviderId,
        TestGameContentDiscoveryOverview.notInProgress());

      simulateDiscoveryStartedEventReceived(event);

      expect(component.discoveryOverviewsByGameProviderId.get(event.gameProviderId)?.isInProgress).toBeTrue();
    });

  it('should update discovery status given discovery stopped event received', () => {
    const event: GameContentDiscoveryStoppedEvent = TestGameContentDiscoveryStoppedEvent.successfulSubsequent();
    component.discoveryIsInProgressByGameProviderId.set(event.gameProviderId, true);

    simulateDiscoveryStoppedEventReceived(event);

    expect(component.discoveryIsInProgressByGameProviderId.get(event.gameProviderId)).toBeFalse();
    expect(component.discoveryStatusUnknownByGameProviderId.get(event.gameProviderId)).toBeFalse();
  });

  function simulateDiscoveryStoppedEventReceived(event: GameContentDiscoveryStoppedEvent) {
    discoveryStoppedSubscriptions[0]({body: JSON.stringify(event)});
  }

  it('should update overview given discovery stopped event received and overview exists', () => {
    const event: GameContentDiscoveryStoppedEvent = TestGameContentDiscoveryStoppedEvent.successfulSubsequent();
    component.discoveryOverviewsByGameProviderId.set(event.gameProviderId,
      TestGameContentDiscoveryOverview.inProgress());

    simulateDiscoveryStoppedEventReceived(event);

    const expectedOverview: GameContentDiscoveryOverview =
      TestGameContentDiscoveryOverview.notInProgressAfterSuccessfulSubsequent()
    expect(component.discoveryOverviewsByGameProviderId.get(event.gameProviderId)).toEqual(expectedOverview);
    expect(component.discoveryStatusUnknownByGameProviderId.get(event.gameProviderId)).toBeFalse();
  });

  it('should update overview given discovery stopped event received and overview is undefined', () => {
    const event: GameContentDiscoveryStoppedEvent = TestGameContentDiscoveryStoppedEvent.successfulSubsequent();
    component.discoveryOverviewsByGameProviderId.delete(event.gameProviderId);

    simulateDiscoveryStoppedEventReceived(event);

    const expectedOverview: GameContentDiscoveryOverview =
      TestGameContentDiscoveryOverview.notInProgressAfterSuccessfulSubsequent()
    expect(component.discoveryOverviewsByGameProviderId.get(event.gameProviderId)).toEqual(expectedOverview);
    expect(component.discoveryStatusUnknownByGameProviderId.get(event.gameProviderId)).toBeFalse();
  });

  it('should start game content discovery', async () => {
    const fakeObservable = of(new HttpResponse());
    gameContentDiscoveryClient.startGameContentDiscovery.and.returnValue(fakeObservable);
    component.discoveryStatusUnknownByGameProviderId.set('someGameProviderId', false);

    await component.startGameContentDiscovery();

    expect(gameContentDiscoveryClient.startGameContentDiscovery).toHaveBeenCalled();
    expect(component.discoveryStatusUnknownByGameProviderId.get('someGameProviderId')).toBeTrue();
  });

  it('should log an error when game content discovery cannot be started', async () => {
    const mockError = new Error('Discovery failed');

    gameContentDiscoveryClient.startGameContentDiscovery.and.returnValue(throwError(() => mockError));

    await component.startGameContentDiscovery();

    expect(notificationService.showFailure).toHaveBeenCalledWith(
      'Error starting discovery', mockError);
  });

  it('should stop game content discovery', async () => {
    const fakeObservable = of(new HttpResponse());
    gameContentDiscoveryClient.stopGameContentDiscovery.and.returnValue(fakeObservable);
    component.discoveryStatusUnknownByGameProviderId.set('someGameProviderId', false);

    await component.stopGameContentDiscovery();

    expect(gameContentDiscoveryClient.stopGameContentDiscovery).toHaveBeenCalled();
    expect(component.discoveryStatusUnknownByGameProviderId.get('someGameProviderId')).toBeTrue();
  });

  it('should log an error when game content discovery cannot be stopped', async () => {
    const mockError = new Error('Discovery failed');

    gameContentDiscoveryClient.stopGameContentDiscovery.and.returnValue(throwError(() => mockError));

    await component.stopGameContentDiscovery();

    expect(notificationService.showFailure).toHaveBeenCalledWith(
      'Error stopping discovery', mockError);
  });

  it('should not update discovery progress given event received but overview is undefined', () => {
    const event: GameContentDiscoveryProgressChangedEvent =
      TestGameContentDiscoveryProgressChangedEvent.twentyFivePercent();
    simulateDiscoveryProgressChangedEventReceived(event);

    expect(component.discoveryOverviewsByGameProviderId.get(event.gameProviderId!))
      .toBeUndefined();
    expect(component.discoveryStatusUnknownByGameProviderId.get(event.gameProviderId)).toBeFalse();
  });

  function simulateDiscoveryProgressChangedEventReceived(progressUpdate: GameContentDiscoveryProgressChangedEvent) {
    discoveryProgressSubscriptions[0]({body: JSON.stringify(progressUpdate)});
  }

  it('should update discovery progress given event received and overview is defined', () => {
    const event: GameContentDiscoveryProgressChangedEvent =
      TestGameContentDiscoveryProgressChangedEvent.twentyFivePercent();
    component.discoveryOverviewsByGameProviderId.set(event.gameProviderId, {
      gameProviderId: event.gameProviderId,
      isInProgress: true
    });
    simulateDiscoveryProgressChangedEventReceived(event);

    expect(component.discoveryOverviewsByGameProviderId.get(event.gameProviderId!))
      .toEqual(TestGameContentDiscoveryOverview.inProgressAtTwentyFivePercent());
    expect(component.discoveryStatusUnknownByGameProviderId.get(event.gameProviderId)).toBeFalse();
  });

  it('should return undefined from getOverview when game provider not found', () => {
    const gameProviderId = 'someGameProvider';
    expect(component.getOverview(gameProviderId)).toEqual(undefined);
  });

  it('should get overview for specific game provider', () => {
    const expectedOverview: GameContentDiscoveryOverview =
      TestGameContentDiscoveryOverview.notInProgressAfterSuccessfulSubsequent();
    component.discoveryOverviewsByGameProviderId.set(expectedOverview.gameProviderId, expectedOverview);
    component.discoveryIsInProgressByGameProviderId.set(expectedOverview.gameProviderId, true);
    expect(component.getOverview(expectedOverview.gameProviderId)).toEqual(expectedOverview);
  });

  it('should return if discovery is ongoing', () => {
    expect(component.discoveryOngoing()).toBeFalse();

    component.discoveryIsInProgressByGameProviderId.set('someGameProviderId', false);
    expect(component.discoveryOngoing()).toBeFalse();

    component.discoveryIsInProgressByGameProviderId.set('someGameProviderId', true);
    expect(component.discoveryOngoing()).toBeTrue();
  });
});
