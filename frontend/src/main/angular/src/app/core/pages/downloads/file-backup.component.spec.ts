import {ComponentFixture, TestBed} from '@angular/core/testing';

import {FileBackupComponent} from './file-backup.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {PageHeaderStubComponent} from "@app/shared/components/page-header/page-header.component.stub";
import {TableComponent} from "@app/shared/components/table/table.component";
import {LoadedContentStubComponent} from "@app/shared/components/loaded-content/loaded-content.component.stub";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {of} from "rxjs";
import {
  FileBackupStartedMessage,
  FileBackupStatus,
  GameFileDetails,
  GameFileDetailsClient,
  PageGameFileDetails
} from "@backend";
import {By} from "@angular/platform-browser";
import {TableColumnDirective} from "@app/shared/components/table/column-directive/table-column.directive";

describe('FileBackupComponent', () => {
  let component: FileBackupComponent;
  let fixture: ComponentFixture<FileBackupComponent>;
  let gameFileDetailsClient: jasmine.SpyObj<GameFileDetailsClient>;
  let messagesService: jasmine.SpyObj<MessagesService>;

  const enqueuedDownloads: PageGameFileDetails = {
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
  const processedFiles: PageGameFileDetails = {
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
  const currentlyProcessedGameFileDetails: GameFileDetails = {
    id: "someGameFileId",
    sourceFileDetails: {
      originalGameTitle: "Some current game",
      originalFileName: "Some original file name",
      version: "Some version",
      size: "3 GB",
      fileTitle: "currentGame.exe"
    }
  };
  const expectedCurrentlyProcessing: FileBackupStartedMessage = {
    gameFileDetailsId: "someGameFileId",
    originalGameTitle: "Some current game",
    originalFileName: "Some original file name",
    version: "Some version",
    fileTitle: "currentGame.exe",
    size: "3 GB"
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
          provide: GameFileDetailsClient,
          useValue: jasmine.createSpyObj('GameFileDetailsClient',
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
    gameFileDetailsClient = TestBed.inject(GameFileDetailsClient) as jasmine.SpyObj<GameFileDetailsClient>;
    messagesService = TestBed.inject(MessagesService) as jasmine.SpyObj<MessagesService>;

    gameFileDetailsClient.getQueueItems.and.returnValue(of(enqueuedDownloads) as any);
    gameFileDetailsClient.getProcessedFiles.and.returnValue(of(processedFiles) as any);
    gameFileDetailsClient.getCurrentlyProcessing.and.returnValue(of(currentlyProcessedGameFileDetails) as any);
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

    expect(gameFileDetailsClient.getQueueItems)
      .toHaveBeenCalledWith({
        page: 0,
        size: component['pageSize']
      });
    expect(component.enqueuedDownloads).toEqual(enqueuedDownloads);
    expect(gameFileDetailsClient.getProcessedFiles).toHaveBeenCalled();
    expect(component.processedFiles).toEqual(processedFiles);
    expect(gameFileDetailsClient.getCurrentlyProcessing).toHaveBeenCalled();
    expect(component.currentDownload).toEqual(expectedCurrentlyProcessing);
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
