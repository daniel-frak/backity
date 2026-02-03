import {ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';

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
import {of, Subject, throwError} from "rxjs";
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
import {By} from "@angular/platform-browser";
import SpyObj = jasmine.SpyObj;
import createSpyObj = jasmine.createSpyObj;

describe('GameProvidersComponent', () => {
  let component: GameProvidersComponent;
  let fixture: ComponentFixture<GameProvidersComponent>;

  let discoveryStartedSubject: Subject<GameContentDiscoveryStartedEvent>;
  let discoveryStoppedSubject: Subject<GameContentDiscoveryStoppedEvent>;
  let discoveryProgressSubject: Subject<GameContentDiscoveryProgressChangedEvent>;
  let gameContentDiscoveryClient: SpyObj<GameContentDiscoveryClient>;
  let notificationService: SpyObj<NotificationService>;
  let messagesService: SpyObj<MessagesService>;

  beforeEach(async () => {
    discoveryStartedSubject = new Subject();
    discoveryStoppedSubject = new Subject();
    discoveryProgressSubject = new Subject();

    await TestBed.configureTestingModule({
      imports: [GameProvidersComponent],
      providers: [
        {
          provide: MessagesService,
          useValue: createSpyObj(MessagesService, ['watchJson'])
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
    messagesService = TestBed.inject(MessagesService) as SpyObj<MessagesService>;
    gameContentDiscoveryClient = TestBed.inject(GameContentDiscoveryClient) as SpyObj<GameContentDiscoveryClient>;
    notificationService = TestBed.inject(NotificationService) as SpyObj<NotificationService>;

    messagesService.watchJson.and.callFake(((destination: string) => {
      if (destination === GameContentDiscoveryWebSocketTopics.TopicGameContentDiscoveryDiscoveryStarted) {
        return discoveryStartedSubject;
      }
      if (destination === GameContentDiscoveryWebSocketTopics.TopicGameContentDiscoveryDiscoveryStopped) {
        return discoveryStoppedSubject;
      }
      if (destination === GameContentDiscoveryWebSocketTopics.TopicGameContentDiscoveryProgressUpdate) {
        return discoveryProgressSubject;
      }
      return of();
    }) as any);
  });

  it('should create', () => {
    gameContentDiscoveryClient.getGameContentDiscoveryOverviews.and.returnValue(of([]) as any);
    fixture = TestBed.createComponent(GameProvidersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should refresh info on init', fakeAsync(() => {
    const newOverview: GameContentDiscoveryOverview = TestGameContentDiscoveryOverview.inProgress();
    gameContentDiscoveryClient.getGameContentDiscoveryOverviews.and.returnValue(of([newOverview]) as any);

    fixture = TestBed.createComponent(GameProvidersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    tick();
    fixture.detectChanges();

    expect(component.discoveryIsInProgressByGameProviderId().get('someGameProviderId')).toBeTrue();
    expect(component.discoveryOverviewsByGameProviderId().get('someGameProviderId')).toEqual(newOverview);
    expect(component.discoveryStatusUnknownByGameProviderId().get('someGameProviderId')).toBeFalse();
  }));

  it('should subscribe to discovery started events', () => {
    gameContentDiscoveryClient.getGameContentDiscoveryOverviews.and.returnValue(of([]) as any);
    fixture = TestBed.createComponent(GameProvidersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    expect(messagesService.watchJson)
      .toHaveBeenCalledWith(GameContentDiscoveryWebSocketTopics.TopicGameContentDiscoveryDiscoveryStarted);
  });

  it('should subscribe to discovery stopped events', () => {
    gameContentDiscoveryClient.getGameContentDiscoveryOverviews.and.returnValue(of([]) as any);
    fixture = TestBed.createComponent(GameProvidersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    expect(messagesService.watchJson)
      .toHaveBeenCalledWith(GameContentDiscoveryWebSocketTopics.TopicGameContentDiscoveryDiscoveryStopped);
  });

  it('should subscribe to discovery progress updates', () => {
    gameContentDiscoveryClient.getGameContentDiscoveryOverviews.and.returnValue(of([]) as any);
    fixture = TestBed.createComponent(GameProvidersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    expect(messagesService.watchJson)
      .toHaveBeenCalledWith(GameContentDiscoveryWebSocketTopics.TopicGameContentDiscoveryProgressUpdate);
  });

  it('should update discovery status given discovery started event received', fakeAsync(() => {
    gameContentDiscoveryClient.getGameContentDiscoveryOverviews.and.returnValue(of([]) as any);
    fixture = TestBed.createComponent(GameProvidersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    const event: GameContentDiscoveryStartedEvent = TestGameContentDiscoveryStartedEvent.any();

    discoveryStartedSubject.next(event);
    tick();

    expect(component.discoveryIsInProgressByGameProviderId().get(event.gameProviderId)).toBeTrue();
    expect(component.discoveryStatusUnknownByGameProviderId().get(event.gameProviderId)).toBeFalse();
  }));

  it('should update overview given discovery started event received and overview exists',
    fakeAsync(() => {
      gameContentDiscoveryClient.getGameContentDiscoveryOverviews.and.returnValue(of([]) as any);
      fixture = TestBed.createComponent(GameProvidersComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
      const event: GameContentDiscoveryStartedEvent = TestGameContentDiscoveryStartedEvent.any();
      component.discoveryOverviewsByGameProviderId.update(map =>
        new Map(map).set(event.gameProviderId, TestGameContentDiscoveryOverview.notInProgress()));

      discoveryStartedSubject.next(event);
      tick();

      expect(component.discoveryOverviewsByGameProviderId().get(event.gameProviderId)?.isInProgress).toBeTrue();
    }));

  it('should update discovery status given discovery stopped event received', fakeAsync(() => {
    gameContentDiscoveryClient.getGameContentDiscoveryOverviews.and.returnValue(of([]) as any);
    fixture = TestBed.createComponent(GameProvidersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    const event: GameContentDiscoveryStoppedEvent = TestGameContentDiscoveryStoppedEvent.successfulSubsequent();
    component.discoveryIsInProgressByGameProviderId.update(map =>
      new Map(map).set(event.gameProviderId, true));

    discoveryStoppedSubject.next(event);
    tick();

    expect(component.discoveryIsInProgressByGameProviderId().get(event.gameProviderId)).toBeFalse();
    expect(component.discoveryStatusUnknownByGameProviderId().get(event.gameProviderId)).toBeFalse();
  }));

  it('should update overview given discovery stopped event received and overview exists', fakeAsync(() => {
    gameContentDiscoveryClient.getGameContentDiscoveryOverviews.and.returnValue(of([]) as any);
    fixture = TestBed.createComponent(GameProvidersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    const event: GameContentDiscoveryStoppedEvent = TestGameContentDiscoveryStoppedEvent.successfulSubsequent();
    component.discoveryOverviewsByGameProviderId.update(map =>
      new Map(map).set(event.gameProviderId, TestGameContentDiscoveryOverview.inProgress()));

    discoveryStoppedSubject.next(event);
    tick();

    const expectedOverview: GameContentDiscoveryOverview =
      TestGameContentDiscoveryOverview.notInProgressAfterSuccessfulSubsequent()
    expect(component.discoveryOverviewsByGameProviderId().get(event.gameProviderId)).toEqual(expectedOverview);
    expect(component.discoveryStatusUnknownByGameProviderId().get(event.gameProviderId)).toBeFalse();
  }));

  it('should update overview given discovery stopped event received and overview is undefined', fakeAsync(() => {
    gameContentDiscoveryClient.getGameContentDiscoveryOverviews.and.returnValue(of([]) as any);
    fixture = TestBed.createComponent(GameProvidersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    const event: GameContentDiscoveryStoppedEvent = TestGameContentDiscoveryStoppedEvent.successfulSubsequent();
    component.discoveryOverviewsByGameProviderId.update(map => {
      const newMap = new Map(map);
      newMap.delete(event.gameProviderId);
      return newMap;
    });

    discoveryStoppedSubject.next(event);
    tick();

    const expectedOverview: GameContentDiscoveryOverview =
      TestGameContentDiscoveryOverview.notInProgressAfterSuccessfulSubsequent()
    expect(component.discoveryOverviewsByGameProviderId().get(event.gameProviderId)).toEqual(expectedOverview);
    expect(component.discoveryStatusUnknownByGameProviderId().get(event.gameProviderId)).toBeFalse();
  }));

  async function clickButtonByTestId(testId: string) {
    const button = fixture.debugElement.query(By.css('[data-testid="' + testId + '"]'));
    button.triggerEventHandler('click');
    await fixture.whenStable();
    fixture.detectChanges();
  }

  it('should start game content discovery', async () => {
    gameContentDiscoveryClient.getGameContentDiscoveryOverviews.and.returnValue(of([]) as any);
    fixture = TestBed.createComponent(GameProvidersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    const fakeObservable = of(new HttpResponse());
    gameContentDiscoveryClient.startGameContentDiscovery.and.returnValue(fakeObservable);
    component.discoveryStatusUnknownByGameProviderId.update(map => new Map(map).set('someGameProviderId', false));

    await clickButtonByTestId('start-game-content-discovery-btn');

    expect(gameContentDiscoveryClient.startGameContentDiscovery).toHaveBeenCalled();
    expect(component.discoveryStatusUnknownByGameProviderId().get('someGameProviderId')).toBeTrue();
  });

  it('should log an error when game content discovery cannot be started', async () => {
    gameContentDiscoveryClient.getGameContentDiscoveryOverviews.and.returnValue(of([]) as any);
    fixture = TestBed.createComponent(GameProvidersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    const mockError = new Error('Discovery failed');

    gameContentDiscoveryClient.startGameContentDiscovery.and.returnValue(throwError(() => mockError));

    await component.startGameContentDiscovery();

    expect(notificationService.showFailure).toHaveBeenCalledWith(
      'Error starting discovery', mockError);
  });

  it('should stop game content discovery', async () => {
    gameContentDiscoveryClient.getGameContentDiscoveryOverviews.and.returnValue(of([]) as any);
    fixture = TestBed.createComponent(GameProvidersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    const fakeObservable = of(new HttpResponse());
    gameContentDiscoveryClient.stopGameContentDiscovery.and.returnValue(fakeObservable);
    component.discoveryStatusUnknownByGameProviderId.update(map => new Map(map).set('someGameProviderId', false));

    await clickButtonByTestId('stop-game-content-discovery-btn');

    expect(gameContentDiscoveryClient.stopGameContentDiscovery).toHaveBeenCalled();
    expect(component.discoveryStatusUnknownByGameProviderId().get('someGameProviderId')).toBeTrue();
  });

  it('should log an error when game content discovery cannot be stopped', async () => {
    gameContentDiscoveryClient.getGameContentDiscoveryOverviews.and.returnValue(of([]) as any);
    fixture = TestBed.createComponent(GameProvidersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    const mockError = new Error('Discovery failed');

    gameContentDiscoveryClient.stopGameContentDiscovery.and.returnValue(throwError(() => mockError));

    await component.stopGameContentDiscovery();

    expect(notificationService.showFailure).toHaveBeenCalledWith(
      'Error stopping discovery', mockError);
  });

  it('should update discovery progress given event received and overview is defined', () => {
    gameContentDiscoveryClient.getGameContentDiscoveryOverviews.and.returnValue(of([]) as any);
    fixture = TestBed.createComponent(GameProvidersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    const event: GameContentDiscoveryProgressChangedEvent =
      TestGameContentDiscoveryProgressChangedEvent.twentyFivePercent();
    component.discoveryOverviewsByGameProviderId.update(map => new Map(map).set(event.gameProviderId, {
      gameProviderId: event.gameProviderId,
      isInProgress: true
    }));

    discoveryProgressSubject.next(event);

    expect(component.discoveryOverviewsByGameProviderId().get(event.gameProviderId!))
      .toEqual(TestGameContentDiscoveryOverview.inProgressAtTwentyFivePercent());
    expect(component.discoveryStatusUnknownByGameProviderId().get(event.gameProviderId)).toBeFalse();
  });

  it('should return undefined from getOverview when game provider not found', () => {
    gameContentDiscoveryClient.getGameContentDiscoveryOverviews.and.returnValue(of([]) as any);
    fixture = TestBed.createComponent(GameProvidersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    const gameProviderId = 'someGameProvider';
    expect(component.getOverview(gameProviderId)).toEqual(undefined);
  });

  it('should get overview for specific game provider', () => {
    gameContentDiscoveryClient.getGameContentDiscoveryOverviews.and.returnValue(of([]) as any);
    fixture = TestBed.createComponent(GameProvidersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    const expectedOverview: GameContentDiscoveryOverview =
      TestGameContentDiscoveryOverview.notInProgressAfterSuccessfulSubsequent();
    component.discoveryOverviewsByGameProviderId.update(map =>
      new Map(map).set(expectedOverview.gameProviderId, expectedOverview));
    component.discoveryIsInProgressByGameProviderId.update(map =>
      new Map(map).set(expectedOverview.gameProviderId, true));
    expect(component.getOverview(expectedOverview.gameProviderId)).toEqual(expectedOverview);
  });

  it('should return if discovery is ongoing', () => {
    gameContentDiscoveryClient.getGameContentDiscoveryOverviews.and.returnValue(of([]) as any);
    fixture = TestBed.createComponent(GameProvidersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    expect(component.discoveryOngoing()).toBeFalse();

    component.discoveryIsInProgressByGameProviderId.update(map => new Map(map).set('someGameProviderId', false));
    expect(component.discoveryOngoing()).toBeFalse();

    component.discoveryIsInProgressByGameProviderId.update(map => new Map(map).set('someGameProviderId', true));
    expect(component.discoveryOngoing()).toBeTrue();
  });
});
