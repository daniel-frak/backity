import {ComponentFixture, TestBed} from '@angular/core/testing';

import {GameContentDiscoveryInfoCardComponent} from './game-content-discovery-info-card.component';
import {
  GameFileDiscoveredEvent,
  GameContentDiscoveryClient,
  GameContentDiscoveryProgressUpdateEvent,
  GameContentDiscoveryStatus,
  GameContentDiscoveryWebSocketTopics
} from "@backend";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {provideRouter} from "@angular/router";
import {HttpResponse} from "@angular/common/http";
import {MessageTesting} from "@app/shared/testing/message-testing";
import {of, throwError} from "rxjs";
import {TestGameContentDiscoveryStatus} from "@app/shared/testing/objects/test-game-content-discovery-status";
import {
  TestGameContentDiscoveryProgressUpdateEvent
} from "@app/shared/testing/objects/test-game-content-discovery-progress-update-event";
import SpyObj = jasmine.SpyObj;
import createSpyObj = jasmine.createSpyObj;

describe('GameContentDiscoveryInfoCardComponent', () => {
  let component: GameContentDiscoveryInfoCardComponent;
  let fixture: ComponentFixture<GameContentDiscoveryInfoCardComponent>;
  let discoveredSubscriptions: Function[];
  let discoveryChangedSubscriptions: Function[];
  let discoveryProgressSubscriptions: Function[];
  let gameContentDiscoveryClient: SpyObj<GameContentDiscoveryClient>;
  let notificationService: SpyObj<NotificationService>;
  let messagesService: SpyObj<MessagesService>;

  beforeEach(async () => {
    discoveredSubscriptions = [];
    discoveryChangedSubscriptions = [];
    discoveryProgressSubscriptions = [];

    await TestBed.configureTestingModule({
      imports: [GameContentDiscoveryInfoCardComponent],
      providers: [
        {
          provide: MessagesService,
          useValue: createSpyObj(MessagesService, ['watch'])
        },
        {
          provide: GameContentDiscoveryClient,
          useValue: createSpyObj('GameContentDiscoveryClient',
            ['getStatuses', 'startDiscovery', 'stopDiscovery'])
        },
        {
          provide: NotificationService,
          useValue: createSpyObj('NotificationService', ['showSuccess', 'showFailure'])
        },
        provideRouter([])
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(GameContentDiscoveryInfoCardComponent);
    component = fixture.componentInstance;
    component.discoveryStatusByGameProviderId.clear();
    component.discoveryProgressByGameProviderId.clear();

    messagesService = TestBed.inject(MessagesService) as SpyObj<MessagesService>;
    gameContentDiscoveryClient = TestBed.inject(GameContentDiscoveryClient) as SpyObj<GameContentDiscoveryClient>;
    notificationService = TestBed.inject(NotificationService) as SpyObj<NotificationService>;

    MessageTesting.mockWatch(messagesService, (destination, callback) => {
      if (destination === GameContentDiscoveryWebSocketTopics.FileDiscovered) {
        discoveredSubscriptions.push(callback);
      }
      if (destination === GameContentDiscoveryWebSocketTopics.StatusChanged) {
        discoveryChangedSubscriptions.push(callback);
      }
      if (destination === GameContentDiscoveryWebSocketTopics.ProgressUpdate) {
        discoveryProgressSubscriptions.push(callback);
      }
    });
    gameContentDiscoveryClient.getStatuses.and.returnValue(of([]) as any);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should refresh info on init', async () => {
    const newStatus: GameContentDiscoveryStatus = {gameProviderId: 'someGameProviderId', isInProgress: true};
    gameContentDiscoveryClient.getStatuses.and.returnValue(of([newStatus]) as any);

    component.ngOnInit();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(component.discoveryStatusByGameProviderId.get('someGameProviderId')).toBeTrue();
    expect(component.newDiscoveredGameFilesCount).toBe(0);
  });

  it('should subscribe to new discoveries', () => {
    expect(discoveredSubscriptions.length).toBe(1);
  });

  it('should subscribe to discovery status updates', () => {
    expect(discoveryChangedSubscriptions.length).toBe(1);
  });

  it('should subscribe to discovery progress updates', () => {
    expect(discoveryProgressSubscriptions.length).toBe(1);
  });

  it('should set newest discovered and increment discovered count on new discovery', () => {
    const expectedGameFileDiscoveredEvent: GameFileDiscoveredEvent = {
      fileTitle: 'currentGame.exe'
    };
    discoveredSubscriptions[0]({body: JSON.stringify(expectedGameFileDiscoveredEvent)})
    expect(component.newestGameFileDiscoveredEvent).toEqual(expectedGameFileDiscoveredEvent);
    expect(component.newDiscoveredGameFilesCount).toEqual(1);
  });

  it('should unset current download on finish', () => {
    const newStatus1: GameContentDiscoveryStatus = TestGameContentDiscoveryStatus.inProgress();
    const newStatus2: GameContentDiscoveryStatus = TestGameContentDiscoveryStatus.notInProgress();
    newStatus2.gameProviderId = newStatus1.gameProviderId;

    discoveryChangedSubscriptions[0]({body: JSON.stringify(newStatus1)});
    expect(component.discoveryStatusByGameProviderId.get(newStatus1.gameProviderId!)).toBeTrue();
    discoveryChangedSubscriptions[0]({body: JSON.stringify(newStatus2)});
    expect(component.discoveryStatusByGameProviderId.get(newStatus1.gameProviderId!)).toBeFalse();
  });

  it('should start game content discovery', async () => {
    const fakeObservable = of(new HttpResponse());
    gameContentDiscoveryClient.startDiscovery.and.returnValue(fakeObservable);

    await component.startDiscovery();

    expect(gameContentDiscoveryClient.startDiscovery).toHaveBeenCalled();
  });

  it('should log an error when game content discovery cannot be started', async () => {
    const mockError = new Error('Discovery failed');

    gameContentDiscoveryClient.startDiscovery.and.returnValue(throwError(() => mockError));

    await component.startDiscovery();

    expect(notificationService.showFailure).toHaveBeenCalledWith(
      'Error starting discovery', mockError);
  });

  it('should stop game content discovery', async () => {
    const fakeObservable = of(new HttpResponse());
    gameContentDiscoveryClient.stopDiscovery.and.returnValue(fakeObservable);

    await component.stopDiscovery();

    expect(gameContentDiscoveryClient.stopDiscovery).toHaveBeenCalled();
  });

  it('should log an error when game content discovery cannot be stopped', async () => {
    const mockError = new Error('Discovery failed');

    gameContentDiscoveryClient.stopDiscovery.and.returnValue(throwError(() => mockError));

    await component.stopDiscovery();

    expect(notificationService.showFailure).toHaveBeenCalledWith(
      'Error stopping discovery', mockError);
  });

  it('should update discovery progress', () => {
    const progressUpdate: GameContentDiscoveryProgressUpdateEvent = TestGameContentDiscoveryProgressUpdateEvent.twentyFivePercent();
    discoveryProgressSubscriptions[0]({body: JSON.stringify(progressUpdate)});

    expect(component.discoveryProgressByGameProviderId.get(progressUpdate.gameProviderId!)).toEqual(progressUpdate);
  });

  it('should get progress list', () => {
    expect(component.getProgressList()).toEqual([]);
    const expectedProgress: GameContentDiscoveryProgressUpdateEvent = TestGameContentDiscoveryProgressUpdateEvent.twentyFivePercent();

    component.discoveryProgressByGameProviderId.set(expectedProgress.gameProviderId!, expectedProgress);
    expect(component.getProgressList()).toEqual([expectedProgress]);
  });

  it('should correctly check if is in progress', () => {
    expect(component.isInProgress('someGameProviderId')).toBeFalse();

    component.discoveryStatusByGameProviderId.set('someGameProviderId', true);
    expect(component.isInProgress('someGameProviderId')).toBeTrue();
  });

  it('should return all statuses', () => {
    expect(component.getStatuses()).toEqual([]);
    const expectedStatus: GameContentDiscoveryStatus = TestGameContentDiscoveryStatus.inProgress();

    component.discoveryStatusByGameProviderId.set(expectedStatus.gameProviderId!, true);
    expect(component.getStatuses()).toEqual([expectedStatus]);
  });

  it('should return if discovery is ongoing', () => {
    expect(component.discoveryOngoing()).toBeFalse();

    component.discoveryStatusByGameProviderId.set('someGameProviderId', false);
    expect(component.discoveryOngoing()).toBeFalse();

    component.discoveryStatusByGameProviderId.set('someGameProviderId', true);
    expect(component.discoveryOngoing()).toBeTrue();
  })

  it('should log an error when discoverFilesFor is called', async () => {
    await component.discoverFilesFor('someGameProviderId');

    expect(notificationService.showFailure).toHaveBeenCalledWith(
      `Per-provider game content discovery start not yet implemented`);
  });
});
