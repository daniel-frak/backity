import {ComponentFixture, TestBed} from '@angular/core/testing';

import {FileBackupComponent} from './file-backup.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {PageHeaderStubComponent} from "@app/shared/components/page-header/page-header.component.stub";
import {TableComponent} from "@app/shared/components/table/table.component";
import {LoadedContentStubComponent} from "@app/shared/components/loaded-content/loaded-content.component.stub";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {of} from "rxjs";
import {BackupsClient, FileBackupStatus, GameFileDetails, PageHttpDtoGameFileDetails} from "@backend";
import {By} from "@angular/platform-browser";
import {TableColumnDirective} from "@app/shared/components/table/column-directive/table-column.directive";

describe('DownloadsComponent', () => {
  let component: FileBackupComponent;
  let fixture: ComponentFixture<FileBackupComponent>;
  let backupsClient: jasmine.SpyObj<BackupsClient>;
  let messagesService: jasmine.SpyObj<MessagesService>;

  const enqueuedDownloads: PageHttpDtoGameFileDetails = {
    content: [{
      id: "someGameFileId",
      sourceFileDetails: {
        originalGameTitle: "Some queued game",
        fileTitle: "queuedGame.exe",
        size: "1 GB"
      },
      backupDetails: {
        status: FileBackupStatus.Discovered
      }
    }]
  };
  const processedFiles: PageHttpDtoGameFileDetails = {
    content: [{
      id: "someGameFileId",
      sourceFileDetails: {
        originalGameTitle: "Some processed game",
        fileTitle: "processedGame.exe",
        size: "2 GB"
      },
      backupDetails: {
        status: FileBackupStatus.Success
      }
    }]
  };
  const currentlyProcessing: GameFileDetails = {
    id: "someGameFileId",
    sourceFileDetails: {
      originalGameTitle: "Some current game",
      fileTitle: "currentGame.exe",
      size: "3 GB"
    },
    backupDetails: {
      status: FileBackupStatus.InProgress
    }
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        FileBackupComponent,
        PageHeaderStubComponent,
        LoadedContentStubComponent,
        TableComponent,
        TableColumnDirective
      ],
      imports: [
        HttpClientTestingModule
      ],
      providers: [
        {
          provide: BackupsClient,
          useValue: jasmine.createSpyObj('BackupsClient',
            ['getQueueItems', 'getProcessedFiles', 'getCurrentlyProcessing'])
        },
        {
          provide: MessagesService,
          useValue: jasmine.createSpyObj('MessagesService', ["onConnect"])
        }
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FileBackupComponent);
    component = fixture.componentInstance;
    backupsClient = TestBed.inject(BackupsClient) as jasmine.SpyObj<BackupsClient>;
    messagesService = TestBed.inject(MessagesService) as jasmine.SpyObj<MessagesService>;

    backupsClient.getQueueItems.and.returnValue(of(enqueuedDownloads) as any);
    backupsClient.getProcessedFiles.and.returnValue(of(processedFiles) as any);
    backupsClient.getCurrentlyProcessing.and.returnValue(of(currentlyProcessing) as any);
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should subscribe to message topics on initialization', () => {
    const mockSubscription = {unsubscribe: jasmine.createSpy()};
    messagesService.onConnect.and.callFake((callback) => {
      callback({subscribe: () => mockSubscription} as any);
    });

    component.ngOnInit();

    expect(messagesService.onConnect).toHaveBeenCalled();
    expect(component['stompSubscriptions']).toContain(mockSubscription as any);
  });

  it('should retrieve files', () => {
    fixture.detectChanges();

    expect(backupsClient.getQueueItems)
      .toHaveBeenCalledWith({
        page: 0,
        size: component['pageSize']
      });
    expect(component.enqueuedDownloads).toEqual(enqueuedDownloads);
    expect(backupsClient.getProcessedFiles).toHaveBeenCalled();
    expect(component.processedFiles).toEqual(processedFiles);
    expect(backupsClient.getCurrentlyProcessing).toHaveBeenCalled();
    expect(component.currentDownload).toEqual(currentlyProcessing);
    expect(component.filesAreLoading).toBe(false);

    const currentlyDownloadingTable = fixture.debugElement.query(By.css('#currently-downloading'));
    expect(currentlyDownloadingTable.nativeElement.textContent).toContain("Some current game");

    const queueTable = fixture.debugElement.query(By.css('#download-queue'));

    expect(queueTable.nativeElement.textContent).toContain("Some queued game");

    const processedTable = fixture.debugElement.query(By.css('#processed-files'));
    expect(processedTable.nativeElement.textContent).toContain("Some processed game");
  });

  it('should log an error when removeFromQueue is called', () => {
    spyOn(console, 'error');
    component.removeFromQueue();
    expect(console.error).toHaveBeenCalledWith('Removing from queue not yet implemented');
  });
});
