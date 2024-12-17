import {ComponentFixture, TestBed} from '@angular/core/testing';

import {FileDiscoveryComponent} from './file-discovery.component';
import {provideHttpClientTesting} from "@angular/common/http/testing";
import {LoadedContentStubComponent} from "@app/shared/components/loaded-content/loaded-content.component.stub";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {PageHeaderStubComponent} from "@app/shared/components/page-header/page-header.component.stub";
import {
  NewDiscoveredFilesBadgeComponent
} from "@app/core/pages/file-discovery/new-discovered-files-badge/new-discovered-files-badge.component";
import {TableComponent} from "@app/shared/components/table/table.component";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {
  FileBackupStatus,
  FileDiscoveredEvent,
  FileDiscoveryClient,
  FileDiscoveryProgressUpdateEvent,
  FileDiscoveryStatus,
  FileDiscoveryWebSocketTopics,
  GameFile,
  GameFilesClient,
  PageGameFile
} from "@backend";
import {Observable, of, throwError} from "rxjs";
import {MessageTesting} from "@app/shared/testing/message-testing";
import {HttpResponse, provideHttpClient, withInterceptorsFromDi} from '@angular/common/http';
import {catchError} from "rxjs/operators";
import {ButtonComponent} from "@app/shared/components/button/button.component";
import {FileStatusBadgeComponent} from "@app/core/pages/games/file-status-badge/file-status-badge.component";
import {
  FileDiscoveryStatusBadgeComponent
} from "@app/core/pages/file-discovery/file-discovery-status-badge/file-discovery-status-badge.component";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {provideRouter} from '@angular/router';
import createSpyObj = jasmine.createSpyObj;
import Spy = jasmine.Spy;
import SpyObj = jasmine.SpyObj;

describe('FileDiscoveryComponent', () => {
  let component: FileDiscoveryComponent;
  let fixture: ComponentFixture<FileDiscoveryComponent>;
  let discoveredSubscriptions: Function[];
  let discoveryChangedSubscriptions: Function[];
  let discoveryProgressSubscriptions: Function[];
  let fileDiscoveryClient: SpyObj<FileDiscoveryClient>;
  let gameFilesClient: SpyObj<GameFilesClient>;
  let notificationService: NotificationService;
  let messagesService: SpyObj<MessagesService>;

  beforeEach(async () => {
    discoveredSubscriptions = [];
    discoveryChangedSubscriptions = [];
    discoveryProgressSubscriptions = [];

    await TestBed.configureTestingModule({
      imports: [
        FileDiscoveryComponent,
        NgbModule,
        ButtonComponent,
        NewDiscoveredFilesBadgeComponent,
        TableComponent,
        FileStatusBadgeComponent,
        FileDiscoveryStatusBadgeComponent,
        LoadedContentStubComponent,
        PageHeaderStubComponent
      ],
      providers: [
        provideRouter([]),
        {
          provide: MessagesService,
          useValue: createSpyObj(MessagesService, ['watch'])
        },
        {
          provide: FileDiscoveryClient,
          useValue: jasmine.createSpyObj('FileDiscoveryClient', ['getStatuses', 'startDiscovery', 'stopDiscovery'])
        },
        {
          provide: GameFilesClient,
          useValue: jasmine.createSpyObj('GameFilesClient', ['enqueueFileBackup', 'getGameFiles'])
        },
        {
          provide: NotificationService, useValue: createSpyObj('NotificationService', ['showSuccess', 'showFailure'])
        },
        provideHttpClient(withInterceptorsFromDi()),
        provideHttpClientTesting()
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(FileDiscoveryComponent);
    component = fixture.componentInstance;
    component.discoveryStatusByGameProviderId.clear();
    component.discoveryProgressByGameProviderId.clear();

    messagesService = TestBed.inject(MessagesService) as SpyObj<MessagesService>;
    fileDiscoveryClient = TestBed.inject(FileDiscoveryClient) as SpyObj<FileDiscoveryClient>;
    gameFilesClient = TestBed.inject(GameFilesClient) as SpyObj<GameFilesClient>;
    notificationService = TestBed.inject(NotificationService);

    MessageTesting.mockWatch(messagesService, (destination, callback) => {
      if (destination === FileDiscoveryWebSocketTopics.FileDiscovered) {
        discoveredSubscriptions.push(callback);
      }
      if (destination === FileDiscoveryWebSocketTopics.FileStatusChanged) {
        discoveryChangedSubscriptions.push(callback);
      }
      if (destination === FileDiscoveryWebSocketTopics.ProgressUpdate) {
        discoveryProgressSubscriptions.push(callback);
      }
    });
    fileDiscoveryClient.getStatuses.and.returnValue(of([]) as any);
    const emptyGameFilePage: PageGameFile = {content: []};
    gameFilesClient.getGameFiles.and.returnValue(of(emptyGameFilePage) as any);

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

  it('should refresh game files', async () => {
    const expectedGameFilePage: PageGameFile = {
      content: [{
        id: "someFileId",
        gameId: "someGameId",
        gameProviderFile: {
          gameProviderId: "someGameProviderId",
          originalGameTitle: "Some current game",
          originalFileName: "Some original file name",
          version: "Some version",
          url: "some.url",
          size: "3 GB",
          fileTitle: "currentGame.exe"
        },
        fileBackup: {status: FileBackupStatus.InProgress}
      }]
    };
    gameFilesClient.getGameFiles.and.returnValue(of(expectedGameFilePage) as any);

    await component.refreshDiscoveredFiles()();

    expect(component.discoveredFiles).toEqual(expectedGameFilePage);
    expect(component.filesAreLoading).toBeFalse();
  });

  it('should log an error when discovered files cannot be refreshed', async () => {
    const mockError = new Error('Discovery failed');
    (gameFilesClient.getGameFiles as Spy).and.returnValue(throwError(() => mockError));

    await component.refreshDiscoveredFiles()();

    expect(notificationService.showFailure).toHaveBeenCalledWith(
      'Error fetching discovered files', mockError);
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
    const newStatus1: FileDiscoveryStatus = {
      gameProviderId: 'someGameProviderId',
      isInProgress: true
    };
    const newStatus2: FileDiscoveryStatus = {
      gameProviderId: 'someGameProviderId',
      isInProgress: false
    };

    discoveryChangedSubscriptions[0]({body: JSON.stringify(newStatus1)});
    expect(component.discoveryStatusByGameProviderId.get('someGameProviderId')).toBeTrue();
    discoveryChangedSubscriptions[0]({body: JSON.stringify(newStatus2)});
    expect(component.discoveryStatusByGameProviderId.get('someGameProviderId')).toBeFalse();
  });

  it('should start file discovery', async () => {
    const fakeObservable = of(new HttpResponse());
    fileDiscoveryClient.startDiscovery.and.returnValue(fakeObservable);

    await component.startDiscovery()();

    expect(fileDiscoveryClient.startDiscovery).toHaveBeenCalled();
  });

  it('should log an error when file discovery cannot be started', async () => {
    const mockError = new Error('Discovery failed');

    (fileDiscoveryClient.startDiscovery as Spy).and.returnValue(throwError(() => mockError));

    await component.startDiscovery()();

    expect(notificationService.showFailure).toHaveBeenCalledWith(
      'Error starting discovery', mockError);
  });

  it('should stop file discovery', async () => {
    const fakeObservable = of(new HttpResponse());
    fileDiscoveryClient.stopDiscovery.and.returnValue(fakeObservable);

    await component.stopDiscovery()();

    expect(fileDiscoveryClient.stopDiscovery).toHaveBeenCalled();
  });

  it('should log an error when file discovery cannot be stopped', async () => {
    const mockError = new Error('Discovery failed');

    (fileDiscoveryClient.stopDiscovery as Spy).and.returnValue(throwError(() => mockError));

    await component.stopDiscovery()();

    expect(notificationService.showFailure).toHaveBeenCalledWith(
      'Error stopping discovery', mockError);
  });

  it('should enqueue file', async () => {
    const file: GameFile = {
      id: "someFileId",
      gameId: "someGameId",
      gameProviderFile: {
        gameProviderId: "someGameProviderId",
        originalGameTitle: "Some current game",
        originalFileName: "Some original file name",
        version: "Some version",
        url: "some.url",
        size: "3 GB",
        fileTitle: "currentGame.exe"
      },
      fileBackup: {
        status: FileBackupStatus.Discovered
      }
    };
    const fakeObservable: Observable<HttpResponse<any>> = of(new HttpResponse()).pipe(catchError(e => {
      file.fileBackup.status = FileBackupStatus.Discovered;
      throw e;
    }));
    gameFilesClient.enqueueFileBackup.and.returnValue(fakeObservable);

    await component.enqueueFile(file)();

    expect(file.fileBackup?.status).toEqual(FileBackupStatus.Enqueued);
    expect(gameFilesClient.enqueueFileBackup).toHaveBeenCalledWith(file.id);
    expect(notificationService.showSuccess).toHaveBeenCalledWith(`File backup enqueued`);
  });

  it('should update discovery progress', () => {
    const progressUpdate: FileDiscoveryProgressUpdateEvent = {
      gameProviderId: 'someGameProviderId',
      percentage: 25,
      timeLeftSeconds: 1234
    };
    discoveryProgressSubscriptions[0]({body: JSON.stringify(progressUpdate)});

    expect(component.discoveryProgressByGameProviderId.get('someGameProviderId')).toEqual(progressUpdate);
  });

  it('should get progress list', () => {
    expect(component.getProgressList()).toEqual([]);
    const expectedProgress: FileDiscoveryProgressUpdateEvent = {
      gameProviderId: 'someGameProviderId',
      percentage: 25,
      timeLeftSeconds: 1234
    };

    component.discoveryProgressByGameProviderId.set('someGameProviderId', expectedProgress);
    expect(component.getProgressList()).toEqual([expectedProgress]);
  });

  it('should correctly check if is in progress', () => {
    expect(component.isInProgress('someGameProviderId')).toBeFalse();

    component.discoveryStatusByGameProviderId.set('someGameProviderId', true);
    expect(component.isInProgress('someGameProviderId')).toBeTrue();
  });

  it('should dequeue file when enqueueFile throws', async () => {
    const gameFile: GameFile = {
      id: "someFileId",
      gameId: "someGameId",
      gameProviderFile: {
        gameProviderId: "someGameProviderId",
        originalGameTitle: "Some current game",
        originalFileName: "Some original file name",
        version: "Some version",
        url: "some.url",
        size: "3 GB",
        fileTitle: "currentGame.exe"
      },
      fileBackup: {
        status: FileBackupStatus.Enqueued
      }
    };
    const mockError = new Error("error1");
    const observableMock: any = createSpyObj('Observable', ['subscribe', 'pipe']);
    observableMock.pipe.and.returnValue(observableMock);

    gameFilesClient.enqueueFileBackup.and.returnValue(new Observable(subscriber => {
      expect(gameFile.fileBackup?.status).toEqual(FileBackupStatus.Enqueued);
      subscriber.error(mockError);
    }));

    await component.enqueueFile(gameFile)();

    expect(gameFile.fileBackup?.status).toEqual(FileBackupStatus.Discovered);
    expect(notificationService.showFailure).toHaveBeenCalledWith(
      'An error occurred while trying to enqueue a file', gameFile, mockError);
  });

  it('should return all statuses', () => {
    expect(component.getStatuses()).toEqual([]);
    const expectedStatus: FileDiscoveryStatus = {
      gameProviderId: 'someGameProviderId',
      isInProgress: true
    }

    component.discoveryStatusByGameProviderId.set('someGameProviderId', true);
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
    await component.discoverFilesFor('someGameProviderId')();

    expect(notificationService.showFailure).toHaveBeenCalledWith(
      `Per-provider file discovery start not yet implemented`);
  });
});
