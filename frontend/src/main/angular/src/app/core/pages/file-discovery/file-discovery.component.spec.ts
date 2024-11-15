import {ComponentFixture, TestBed} from '@angular/core/testing';

import {FileDiscoveryComponent} from './file-discovery.component';
import { provideHttpClientTesting } from "@angular/common/http/testing";
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
  FileDetails,
  FileDetailsClient,
  FileDiscoveredEvent,
  FileDiscoveryClient,
  FileDiscoveryWebSocketTopics,
  FileDiscoveryProgressUpdateEvent,
  FileDiscoveryStatus,
  PageFileDetails
} from "@backend";
import {Observable} from "rxjs";
import {MessageTesting} from "@app/shared/testing/message-testing";
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import createSpyObj = jasmine.createSpyObj;

describe('FileDiscoveryComponent', () => {
  let component: FileDiscoveryComponent;
  let fixture: ComponentFixture<FileDiscoveryComponent>;
  let discoveredSubscriptions: Function[];
  let discoveryChangedSubscriptions: Function[];
  let discoveryProgressSubscriptions: Function[];
  let fileDiscoveryClient: jasmine.SpyObj<FileDiscoveryClient>;
  let fileDetailsClient: jasmine.SpyObj<FileDetailsClient>;

  beforeEach(async () => {
    const messagesServiceMock = MessageTesting.mockMessageService(
      (destination, callback) => {
        if (destination == FileDiscoveryWebSocketTopics.FileDiscovered) {
          discoveredSubscriptions.push(callback);
        }
        if (destination == FileDiscoveryWebSocketTopics.FileStatusChanged) {
          discoveryChangedSubscriptions.push(callback);
        }
        if (destination == FileDiscoveryWebSocketTopics.ProgressUpdate) {
          discoveryProgressSubscriptions.push(callback);
        }
      });

    await TestBed.configureTestingModule({
    declarations: [
        FileDiscoveryComponent,
        LoadedContentStubComponent,
        PageHeaderStubComponent,
        NewDiscoveredFilesBadgeComponent,
        TableComponent
    ],
    imports: [NgbModule],
    providers: [
        {
            provide: MessagesService,
            useValue: messagesServiceMock
        },
        {
            provide: FileDiscoveryClient,
            useValue: jasmine.createSpyObj('FileDiscoveryClient', ['getStatuses', 'discover', 'stopDiscovery'])
        },
        {
            provide: FileDetailsClient,
            useValue: jasmine.createSpyObj('FileDetailsClient', ['download', 'getDiscoveredFiles'])
        },
        provideHttpClient(withInterceptorsFromDi()),
        provideHttpClientTesting()
    ]
})
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FileDiscoveryComponent);
    component = fixture.componentInstance;
    component.discoveryStatusBySource.clear();
    component.discoveryProgressBySource.clear();

    fileDiscoveryClient = TestBed.inject(FileDiscoveryClient) as jasmine.SpyObj<FileDiscoveryClient>;
    fileDetailsClient = TestBed.inject(FileDetailsClient) as jasmine.SpyObj<FileDetailsClient>;

    fileDiscoveryClient.getStatuses.and.returnValue({subscribe: (s: (f: any) => any) => s([])} as any);
    fileDetailsClient.getDiscoveredFiles.and.returnValue({subscribe: (s: (f: any) => any) => s([])} as any);

    discoveredSubscriptions = [];
    discoveryChangedSubscriptions = [];
    discoveryProgressSubscriptions = [];

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should refresh info on init', () => {
    const newStatus: FileDiscoveryStatus = {
      source: 'someSource',
      isInProgress: true
    };
    const expectedFileDetails: PageFileDetails = {
      content: [{
        id: "someFileId",
        gameId: "someGameId",
        sourceFileDetails: {
          sourceId: "someSourceId",
          originalGameTitle: "Some current game",
          originalFileName: "Some original file name",
          version: "Some version",
          url: "some.url",
          size: "3 GB",
          fileTitle: "currentGame.exe"
        },
        backupDetails: {
          status: FileBackupStatus.InProgress
        }
      }]
    };

    fileDiscoveryClient.getStatuses.and.returnValue({subscribe: (s: (f: any) => any) => s([newStatus])} as any);
    fileDetailsClient.getDiscoveredFiles.and
      .returnValue({subscribe: (s: (f: any) => any) => s(expectedFileDetails)} as any);

    component.ngOnInit();

    expect(component.discoveryStatusBySource.get('someSource')).toBeTrue();
    expect(component.discoveredFiles).toEqual(expectedFileDetails);
    expect(component.newDiscoveredCount).toBe(0);
    expect(component.filesAreLoading).toBeFalse();
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
      source: 'someSource',
      isInProgress: true
    };
    const newStatus2: FileDiscoveryStatus = {
      source: 'someSource',
      isInProgress: false
    };

    discoveryChangedSubscriptions[0]({body: JSON.stringify(newStatus1)});
    expect(component.discoveryStatusBySource.get('someSource')).toBeTrue();
    discoveryChangedSubscriptions[0]({body: JSON.stringify(newStatus2)});
    expect(component.discoveryStatusBySource.get('someSource')).toBeFalse();
  });

  it('should start file discovery', () => {
    const observableMock: any = createSpyObj('Observable', ['subscribe']);
    fileDiscoveryClient.discover.and.returnValue(observableMock);

    component.startDiscovery();

    expect(observableMock.subscribe).toHaveBeenCalled();
  });

  it('should stop file discovery', () => {
    const observableMock: any = createSpyObj('Observable', ['subscribe']);
    fileDiscoveryClient.stopDiscovery.and.returnValue(observableMock);

    component.stopDiscovery();

    expect(observableMock.subscribe).toHaveBeenCalled();
  });

  it('should enqueue file', () => {
    spyOn(console, 'info');
    const file: FileDetails = {
      id: "someFileId",
      gameId: "someGameId",
      sourceFileDetails: {
        sourceId: "someSourceId",
        originalGameTitle: "Some current game",
        originalFileName: "Some original file name",
        version: "Some version",
        url: "some.url",
        size: "3 GB",
        fileTitle: "currentGame.exe"
      },
      backupDetails: {
        status: FileBackupStatus.Discovered
      }
    };
    const observableMock: any = createSpyObj('Observable', ['subscribe', 'pipe']);
    observableMock.pipe.and.returnValue(observableMock);

    fileDetailsClient.download.and.returnValue(observableMock);

    component.enqueueFile(file);
    expect(file.backupDetails?.status).toEqual(FileBackupStatus.Enqueued);
    expect(observableMock.subscribe).toHaveBeenCalled();
    expect(console.info).toHaveBeenCalled();
  });

  it('should update discovery progress', () => {
    const progressUpdate: FileDiscoveryProgressUpdateEvent = {
      source: 'someSource',
      percentage: 25,
      timeLeftSeconds: 1234
    };
    discoveryProgressSubscriptions[0]({body: JSON.stringify(progressUpdate)});

    expect(component.discoveryProgressBySource.get('someSource')).toEqual(progressUpdate);
  });

  it('should get progress list', () => {
    expect(component.getProgressList()).toEqual([]);
    const expectedProgress: FileDiscoveryProgressUpdateEvent = {
      source: 'someSource',
      percentage: 25,
      timeLeftSeconds: 1234
    };

    component.discoveryProgressBySource.set('someSource', expectedProgress);
    expect(component.getProgressList()).toEqual([expectedProgress]);
  });

  it('should correctly check if is in progress', () => {
    expect(component.isInProgress('someSource')).toBeFalse();

    component.discoveryStatusBySource.set('someSource', true);
    expect(component.isInProgress('someSource')).toBeTrue();
  });

  it('should dequeue file when enqueueFile throws', () => {
    spyOn(console, 'info');
    spyOn(console, 'error');
    const file: FileDetails = {
      id: "someFileId",
      gameId: "someGameId",
      sourceFileDetails: {
        sourceId: "someSourceId",
        originalGameTitle: "Some current game",
        originalFileName: "Some original file name",
        version: "Some version",
        url: "some.url",
        size: "3 GB",
        fileTitle: "currentGame.exe"
      },
      backupDetails: {
        status: FileBackupStatus.Enqueued
      }
    };
    const expectedError = new Error("error1");
    const observableMock: any = createSpyObj('Observable', ['subscribe', 'pipe']);
    observableMock.pipe.and.returnValue(observableMock);

    fileDetailsClient.download.and.returnValue(new Observable(subscriber => {
      expect(file.backupDetails?.status).toEqual(FileBackupStatus.Enqueued);
      subscriber.error(expectedError);
    }));

    component.enqueueFile(file);

    expect(file.backupDetails?.status).toEqual(FileBackupStatus.Discovered);
    expect(console.error).toHaveBeenCalled();
    expect(console.info).toHaveBeenCalled();
  });

  it('should return all statuses', () => {
    expect(component.getStatuses()).toEqual([]);
    const expectedStatus: FileDiscoveryStatus = {
      source: 'someSource',
      isInProgress: true
    }

    component.discoveryStatusBySource.set('someSource', true);
    expect(component.getStatuses()).toEqual([expectedStatus]);
  });

  it('should return if discovery is ongoing', () => {
    expect(component.discoveryOngoing()).toBeFalse();

    component.discoveryStatusBySource.set('someSource', false);
    expect(component.discoveryOngoing()).toBeFalse();

    component.discoveryStatusBySource.set('someSource', true);
    expect(component.discoveryOngoing()).toBeTrue();
  })

  it('should log an error when discoverFilesFor is called', () => {
    spyOn(console, 'error');
    component.discoverFilesFor('someSource');
    expect(console.error).toHaveBeenCalled();
  });
});
