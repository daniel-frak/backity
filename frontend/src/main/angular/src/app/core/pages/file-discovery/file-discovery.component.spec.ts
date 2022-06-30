import {ComponentFixture, TestBed} from '@angular/core/testing';

import {FileDiscoveryComponent} from './file-discovery.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {LoadedContentStubComponent} from "@app/shared/components/loaded-content/loaded-content.component.stub";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {PageHeaderStubComponent} from "@app/shared/components/page-header/page-header.component.stub";
import {
  NewDiscoveredFilesBadgeComponent
} from "@app/core/pages/file-discovery/new-discovered-files-badge/new-discovered-files-badge.component";
import {TableComponent} from "@app/shared/components/table/table.component";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {
  DiscoveredFile,
  DownloadsClient,
  FileDiscoveryClient,
  FileDiscoveryMessageTopics,
  FileDiscoveryStatus,
  PageDiscoveredFile
} from "@backend";
import {Observable} from "rxjs";
import {MessageTesting} from "@app/shared/testing/message-testing";
import createSpyObj = jasmine.createSpyObj;

describe('FileDiscoveryComponent', () => {
  let component: FileDiscoveryComponent;
  let fixture: ComponentFixture<FileDiscoveryComponent>;
  let discoveredSubscriptions: Function[];
  let discoveryChangedSubscriptions: Function[];
  let fileDiscoveryClientMock: any;
  let downloadsClientMock: any;

  beforeEach(async () => {
    discoveredSubscriptions = [];
    discoveryChangedSubscriptions = [];

    const messagesServiceMock = MessageTesting.mockMessageService(
      (destination, callback) => {
        if (destination == FileDiscoveryMessageTopics.Discovery) {
          discoveredSubscriptions.push(callback);
        }
        if (destination == FileDiscoveryMessageTopics.DiscoveryStatus) {
          discoveryChangedSubscriptions.push(callback);
        }
      });

    fileDiscoveryClientMock = createSpyObj(FileDiscoveryClient, ['getStatuses', 'getDiscoveredFiles',
      'discover']);
    fileDiscoveryClientMock.getStatuses.and.returnValue({subscribe: (s: (f: any) => any) => s([])});
    fileDiscoveryClientMock.getDiscoveredFiles.and.returnValue({subscribe: (s: (f: any) => any) => s([])});

    downloadsClientMock = createSpyObj(DownloadsClient, ['download']);

    await TestBed.configureTestingModule({
      declarations: [
        FileDiscoveryComponent,
        LoadedContentStubComponent,
        PageHeaderStubComponent,
        NewDiscoveredFilesBadgeComponent,
        TableComponent
      ],
      imports: [
        HttpClientTestingModule, NgbModule
      ],
      providers: [
        {
          provide: MessagesService,
          useValue: messagesServiceMock
        },
        {
          provide: FileDiscoveryClient,
          useValue: fileDiscoveryClientMock
        },
        {
          provide: DownloadsClient,
          useValue: downloadsClientMock
        }
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FileDiscoveryComponent);
    component = fixture.componentInstance;
    component.discoveryStatuses.clear();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should refresh info on init', () => {
    const newStatus: FileDiscoveryStatus = {
      source: 'someSource',
      inProgress: true
    };
    const expectedDiscoveredFiles: PageDiscoveredFile = {
      content: [{
        name: 'someDiscoveredFile'
      }]
    };

    fileDiscoveryClientMock.getStatuses.and.returnValue({subscribe: (s: (f: any) => any) => s([newStatus])});
    fileDiscoveryClientMock.getDiscoveredFiles.and
      .returnValue({subscribe: (s: (f: any) => any) => s(expectedDiscoveredFiles)});

    component.ngOnInit();

    expect(component.discoveryStatuses.get('someSource')).toBeTrue();
    expect(component.discoveredFiles).toEqual(expectedDiscoveredFiles);
    expect(component.newDiscoveredCount).toBe(0);
    expect(component.filesAreLoading).toBeFalse();
  });

  it('should subscribe to new discoveries', () => {
    expect(discoveredSubscriptions.length).toBe(1);
  });

  it('should subscribe to discovery status updates', () => {
    expect(discoveryChangedSubscriptions.length).toBe(1);
  });

  it('should set newest discovered and increment discovered count on new discovery', () => {
    const expectedDiscoveredFile: DiscoveredFile = {
      name: 'someDiscoveredFile'
    };
    discoveredSubscriptions[0]({body: JSON.stringify(expectedDiscoveredFile)})
    expect(component.newestDiscovered).toEqual(expectedDiscoveredFile);
    expect(component.newDiscoveredCount).toEqual(1);
  });

  it('should unset current download on finish', () => {
    const newStatus1: FileDiscoveryStatus = {
      source: 'someSource',
      inProgress: true
    };
    const newStatus2: FileDiscoveryStatus = {
      source: 'someSource',
      inProgress: false
    };

    discoveryChangedSubscriptions[0]({body: JSON.stringify(newStatus1)});
    expect(component.discoveryStatuses.get('someSource')).toBeTrue();
    discoveryChangedSubscriptions[0]({body: JSON.stringify(newStatus2)});
    expect(component.discoveryStatuses.get('someSource')).toBeFalse();
  });

  it('should discover files', () => {
    const observableMock: any = createSpyObj('Observable', ['subscribe']);
    fileDiscoveryClientMock.discover.and.returnValue(observableMock);

    component.discoverFiles();

    expect(observableMock.subscribe).toHaveBeenCalled();
  });

  it('should enqueue file', () => {
    spyOn(console, 'info');
    const file: DiscoveredFile = {
      name: 'someDiscoveredFile'
    };
    const observableMock: any = createSpyObj('Observable', ['subscribe', 'pipe']);
    observableMock.pipe.and.returnValue(observableMock);

    downloadsClientMock.download.and.returnValue(observableMock);

    component.enqueueFile(file);
    expect(file.enqueued).toBeTrue();
    expect(observableMock.subscribe).toHaveBeenCalled();
    expect(console.info).toHaveBeenCalled();
  });

  it('should dequeue file when enqueueFile throws', () => {
    spyOn(console, 'info');
    spyOn(console, 'error');
    const file: DiscoveredFile = {
      name: 'someDiscoveredFile'
    };
    const expectedError = new Error("error1");
    const observableMock: any = createSpyObj('Observable', ['subscribe', 'pipe']);
    observableMock.pipe.and.returnValue(observableMock);

    downloadsClientMock.download.and.returnValue(new Observable(subscriber => {
      expect(file.enqueued).toBeTrue();
      subscriber.error(expectedError);
    }));

    component.enqueueFile(file);

    expect(file.enqueued).toBeFalse();
    expect(console.error).toHaveBeenCalled();
    expect(console.info).toHaveBeenCalled();
  });

  it('should return all statuses', () => {
    expect(component.getStatuses()).toEqual([]);
    const expectedStatus: FileDiscoveryStatus = {
      source: 'someSource',
      inProgress: true
    }

    component.discoveryStatuses.set('someSource', true);
    expect(component.getStatuses()).toEqual([expectedStatus]);
  });

  it('should return if discovery is ongoing', () => {
    expect(component.discoveryOngoing()).toBeFalse();

    component.discoveryStatuses.set('someSource', false);
    expect(component.discoveryOngoing()).toBeFalse();

    component.discoveryStatuses.set('someSource', true);
    expect(component.discoveryOngoing()).toBeTrue();
  })

  it('should log an error when discoverFilesFor is called', () => {
    spyOn(console, 'error');
    component.discoverFilesFor('someSource');
    expect(console.error).toHaveBeenCalled();
  });
});
