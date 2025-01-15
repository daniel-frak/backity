import {ComponentFixture, TestBed} from '@angular/core/testing';

import {FileDiscoveryInfoCardComponent} from './file-discovery-info-card.component';
import {
  FileDiscoveredEvent,
  FileDiscoveryClient,
  FileDiscoveryProgressUpdateEvent,
  FileDiscoveryStatus,
  FileDiscoveryWebSocketTopics
} from "@backend";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {provideRouter} from "@angular/router";
import {HttpResponse} from "@angular/common/http";
import {MessageTesting} from "@app/shared/testing/message-testing";
import {of, throwError} from "rxjs";
import {TestFileDiscoveryStatus} from "@app/shared/testing/objects/test-file-discovery-status";
import {
  TestFileDiscoveryProgressUpdateEvent
} from "@app/shared/testing/objects/test-file-discovery-progress-update-event";
import SpyObj = jasmine.SpyObj;
import createSpyObj = jasmine.createSpyObj;

describe('FileDiscoveryInfoCardComponent', () => {
  let component: FileDiscoveryInfoCardComponent;
  let fixture: ComponentFixture<FileDiscoveryInfoCardComponent>;
  let discoveredSubscriptions: Function[];
  let discoveryChangedSubscriptions: Function[];
  let discoveryProgressSubscriptions: Function[];
  let fileDiscoveryClient: SpyObj<FileDiscoveryClient>;
  let notificationService: SpyObj<NotificationService>;
  let messagesService: SpyObj<MessagesService>;

  beforeEach(async () => {
    discoveredSubscriptions = [];
    discoveryChangedSubscriptions = [];
    discoveryProgressSubscriptions = [];

    await TestBed.configureTestingModule({
      imports: [FileDiscoveryInfoCardComponent],
      providers: [
        {
          provide: MessagesService,
          useValue: createSpyObj(MessagesService, ['watch'])
        },
        {
          provide: FileDiscoveryClient,
          useValue: createSpyObj('FileDiscoveryClient', ['getStatuses', 'startDiscovery', 'stopDiscovery'])
        },
        {
          provide: NotificationService, useValue: createSpyObj('NotificationService', ['showSuccess', 'showFailure'])
        },
        provideRouter([])
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(FileDiscoveryInfoCardComponent);
    component = fixture.componentInstance;
    component.discoveryStatusByGameProviderId.clear();
    component.discoveryProgressByGameProviderId.clear();

    messagesService = TestBed.inject(MessagesService) as SpyObj<MessagesService>;
    fileDiscoveryClient = TestBed.inject(FileDiscoveryClient) as SpyObj<FileDiscoveryClient>;
    notificationService = TestBed.inject(NotificationService) as SpyObj<NotificationService>;

    MessageTesting.mockWatch(messagesService, (destination, callback) => {
      if (destination === FileDiscoveryWebSocketTopics.Discovered) {
        discoveredSubscriptions.push(callback);
      }
      if (destination === FileDiscoveryWebSocketTopics.DiscoveryStatusChanged) {
        discoveryChangedSubscriptions.push(callback);
      }
      if (destination === FileDiscoveryWebSocketTopics.DiscoveryProgressChanged) {
        discoveryProgressSubscriptions.push(callback);
      }
    });
    fileDiscoveryClient.getStatuses.and.returnValue(of([]) as any);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should refresh info on init', async () => {
    const newStatus: FileDiscoveryStatus = {gameProviderId: 'someGameProviderId', isInProgress: true};
    fileDiscoveryClient.getStatuses.and.returnValue(of([newStatus]) as any);

    component.ngOnInit();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(component.discoveryStatusByGameProviderId.get('someGameProviderId')).toBeTrue();
    expect(component.newDiscoveredCount).toBe(0);
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
    const expectedFileDiscoveredEvent: FileDiscoveredEvent = {
      fileTitle: 'currentGame.exe'
    };
    discoveredSubscriptions[0]({body: JSON.stringify(expectedFileDiscoveredEvent)})
    expect(component.newestDiscovered).toEqual(expectedFileDiscoveredEvent);
    expect(component.newDiscoveredCount).toEqual(1);
  });

  it('should unset current download on finish', () => {
    const newStatus1: FileDiscoveryStatus = TestFileDiscoveryStatus.inProgress();
    const newStatus2: FileDiscoveryStatus = TestFileDiscoveryStatus.notInProgress();
    newStatus2.gameProviderId = newStatus1.gameProviderId;

    discoveryChangedSubscriptions[0]({body: JSON.stringify(newStatus1)});
    expect(component.discoveryStatusByGameProviderId.get(newStatus1.gameProviderId!)).toBeTrue();
    discoveryChangedSubscriptions[0]({body: JSON.stringify(newStatus2)});
    expect(component.discoveryStatusByGameProviderId.get(newStatus1.gameProviderId!)).toBeFalse();
  });

  it('should start file discovery', async () => {
    const fakeObservable = of(new HttpResponse());
    fileDiscoveryClient.startDiscovery.and.returnValue(fakeObservable);

    await component.startDiscovery();

    expect(fileDiscoveryClient.startDiscovery).toHaveBeenCalled();
  });

  it('should log an error when file discovery cannot be started', async () => {
    const mockError = new Error('Discovery failed');

    fileDiscoveryClient.startDiscovery.and.returnValue(throwError(() => mockError));

    await component.startDiscovery();

    expect(notificationService.showFailure).toHaveBeenCalledWith(
      'Error starting discovery', mockError);
  });

  it('should stop file discovery', async () => {
    const fakeObservable = of(new HttpResponse());
    fileDiscoveryClient.stopDiscovery.and.returnValue(fakeObservable);

    await component.stopDiscovery();

    expect(fileDiscoveryClient.stopDiscovery).toHaveBeenCalled();
  });

  it('should log an error when file discovery cannot be stopped', async () => {
    const mockError = new Error('Discovery failed');

    fileDiscoveryClient.stopDiscovery.and.returnValue(throwError(() => mockError));

    await component.stopDiscovery();

    expect(notificationService.showFailure).toHaveBeenCalledWith(
      'Error stopping discovery', mockError);
  });

  it('should update discovery progress', () => {
    const progressUpdate: FileDiscoveryProgressUpdateEvent = TestFileDiscoveryProgressUpdateEvent.twentyFivePercent();
    discoveryProgressSubscriptions[0]({body: JSON.stringify(progressUpdate)});

    expect(component.discoveryProgressByGameProviderId.get(progressUpdate.gameProviderId!)).toEqual(progressUpdate);
  });

  it('should get progress list', () => {
    expect(component.getProgressList()).toEqual([]);
    const expectedProgress: FileDiscoveryProgressUpdateEvent = TestFileDiscoveryProgressUpdateEvent.twentyFivePercent();

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
    const expectedStatus: FileDiscoveryStatus = TestFileDiscoveryStatus.inProgress();

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
      `Per-provider file discovery start not yet implemented`);
  });
});
