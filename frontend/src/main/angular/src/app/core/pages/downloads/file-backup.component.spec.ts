import {ComponentFixture, TestBed} from '@angular/core/testing';
import {FileBackupComponent} from './file-backup.component';
import { provideHttpClientTesting } from "@angular/common/http/testing";
import {PageHeaderStubComponent} from "@app/shared/components/page-header/page-header.component.stub";
import {TableComponent} from "@app/shared/components/table/table.component";
import {LoadedContentStubComponent} from "@app/shared/components/loaded-content/loaded-content.component.stub";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {of} from "rxjs";
import {
  FileBackupMessageTopics,
  FileBackupProgressUpdatedEvent,
  FileBackupStartedEvent,
  FileBackupStatus, FileBackupStatusChangedEvent,
  GameFile, GameFileProcessingStatus,
  GameFilesClient,
  PageGameFile
} from "@backend";
import {By} from "@angular/platform-browser";
import {TableColumnDirective} from "@app/shared/components/table/column-directive/table-column.directive";
import {Client, IMessage, StompSubscription} from "@stomp/stompjs";
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';

describe('FileBackupComponent', () => {
  let component: FileBackupComponent;
  let fixture: ComponentFixture<FileBackupComponent>;
  let gameFilesClient: jasmine.SpyObj<GameFilesClient>;
  let messagesService: jasmine.SpyObj<MessagesService>;

  const sampleGameFile: GameFile = {
    id: "someFileId",
    gameId: "someGameId",
    gameProviderFile: {
      gameProviderId: "someGameProviderId",
      originalGameTitle: "Some game",
      originalFileName: "Some original file name",
      version: "Some version",
      url: "some.url",
      size: "3 GB",
      fileTitle: "currentGame.exe"
    },
    fileBackup: {
      status: "DISCOVERED"
    }
  };

  const enqueuedDownloads: PageGameFile = {
    content: [{
      ...sampleGameFile,
      gameProviderFile: {
        ...sampleGameFile.gameProviderFile,
        originalGameTitle: "Some queued game",
        fileTitle: "queuedGame.exe",
        size: "1 GB"
      },
      fileBackup: {
        status: FileBackupStatus.Discovered
      }
    }]
  };

  const processedFiles: PageGameFile = {
    content: [{
      ...sampleGameFile,
      gameProviderFile: {
        ...sampleGameFile.gameProviderFile,
        originalGameTitle: "Some processed game",
        fileTitle: "processedGame.exe",
        size: "2 GB"
      },
      fileBackup: {
        status: FileBackupStatus.Success
      }
    }]
  };

  const currentlyProcessedGameFile: GameFile = {
    ...sampleGameFile,
    gameProviderFile: {
      ...sampleGameFile.gameProviderFile,
      originalGameTitle: "Some current game",
      fileTitle: "currentGame.exe",
      size: "3 GB",
    },
    fileBackup: {
      status: FileBackupStatus.InProgress,
      filePath: 'some/file/path'
    }
  };

  const expectedCurrentlyProcessing: FileBackupStartedEvent = {
    gameFileId: "someFileId",
    originalGameTitle: "Some current game",
    originalFileName: "Some original file name",
    version: "Some version",
    size: "3 GB",
    fileTitle: "currentGame.exe",
    filePath: "some/file/path"
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
    imports: [],
    providers: [
        {
            provide: GameFilesClient,
            useValue: jasmine.createSpyObj('GameFilesClient', ['getGameFiles', 'getCurrentlyDownloading'])
        },
        {
            provide: MessagesService,
            useValue: jasmine.createSpyObj('MessagesService', ["onConnect"])
        },
        provideHttpClient(withInterceptorsFromDi()),
        provideHttpClientTesting()
    ]
})
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FileBackupComponent);
    component = fixture.componentInstance;
    gameFilesClient = TestBed.inject(GameFilesClient) as jasmine.SpyObj<GameFilesClient>;
    messagesService = TestBed.inject(MessagesService) as jasmine.SpyObj<MessagesService>;

    gameFilesClient.getGameFiles.withArgs(GameFileProcessingStatus.Enqueued, jasmine.anything())
      .and.returnValue(of(enqueuedDownloads) as any);
    gameFilesClient.getGameFiles.withArgs(GameFileProcessingStatus.Processed, jasmine.anything())
      .and.returnValue(of(processedFiles) as any);
    gameFilesClient.getCurrentlyDownloading.and.returnValue(of(currentlyProcessedGameFile) as any);
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should subscribe to message topics on initialization', () => {
    const mockSubscription = aMockSubscription();
    const topicsSubscribed: string[] = [];

    messagesService.onConnect.and.callFake((callback) => {
      callback({
        subscribe: (destination: string, callback: (message: IMessage) => void) => {
          topicsSubscribed.push(destination);
          return mockSubscription;
        }
      } as Client);
    });

    component.ngOnInit();

    expect(messagesService.onConnect).toHaveBeenCalled();
    expect(topicsSubscribed).toContain(FileBackupMessageTopics.Started);
    expect(topicsSubscribed).toContain(FileBackupMessageTopics.ProgressUpdate);
    expect(topicsSubscribed).toContain(FileBackupMessageTopics.StatusChanged);
    expect(component['stompSubscriptions'].length).toEqual(3);
  });

  function aMockSubscription(): StompSubscription {
    return {unsubscribe: jasmine.createSpy()} as any;
  }

  it('should unsubscribe from message topics on destruction', () => {
    const mockSubscription: StompSubscription = {unsubscribe: jasmine.createSpy()} as any;

    component['stompSubscriptions'].push(mockSubscription);

    component.ngOnDestroy();

    expect(mockSubscription.unsubscribe).toHaveBeenCalled();
  });

  it('should retrieve files', () => {
    fixture.detectChanges();

    expect(gameFilesClient.getGameFiles)
      .toHaveBeenCalledWith(
        GameFileProcessingStatus.Enqueued, {
        page: 0,
        size: component['pageSize']
      });
    expect(component.enqueuedDownloads).toEqual(enqueuedDownloads);
    expect(gameFilesClient.getGameFiles)
      .toHaveBeenCalledWith(
        GameFileProcessingStatus.Processed, {
          page: 0,
          size: component['pageSize']
        });
    expect(component.processedFiles).toEqual(processedFiles);
    expect(gameFilesClient.getCurrentlyDownloading).toHaveBeenCalled();
    expect(component.currentDownload).toEqual(expectedCurrentlyProcessing);
    expect(component.filesAreLoading).toBe(false);

    expectCurrentlyDownloadingGameTitleToContain("Some current game");
    expectEnqueuedGameTitleToContain("Some queued game");
    expectProcessedGameTitleToContain("Some processed game");
  });

  function expectCurrentlyDownloadingGameTitleToContain(expectedGameTitle: string) {
    const currentlyDownloadingTable = fixture.debugElement.query(By.css('#currently-downloading'));
    expect(currentlyDownloadingTable.nativeElement.textContent).toContain(expectedGameTitle);
  }

  function expectEnqueuedGameTitleToContain(expectedGameTitle: string) {
    const queueTable = fixture.debugElement.query(By.css('#download-queue'));
    expect(queueTable.nativeElement.textContent).toContain(expectedGameTitle);
  }

  function expectProcessedGameTitleToContain(expectedGameTitle: string) {
    const processedTable = fixture.debugElement.query(By.css('#processed-files'));
    expect(processedTable.nativeElement.textContent).toContain(expectedGameTitle);
  }

  it('should log an error when removeFromQueue is called', () => {
    spyOn(console, 'error');
    component.removeFromQueue();
    expect(console.error).toHaveBeenCalledWith('Removing from queue not yet implemented');
  });

  it('should update currently downloaded game', () => {
    const newCurrentlyProcessing: FileBackupStartedEvent = {...expectedCurrentlyProcessing};
    newCurrentlyProcessing.originalGameTitle = "Updated game title";
    const payload: IMessage = {body: JSON.stringify(newCurrentlyProcessing)} as any;
    const topicCallback = getCallbackForTopic(FileBackupMessageTopics.Started);

    fixture.detectChanges();
    topicCallback.execute(payload);
    fixture.detectChanges();

    expectCurrentlyDownloadingGameTitleToContain("Updated game title");
  });

  function getCallbackForTopic(topic: string) {
    const mockSubscription: StompSubscription = aMockSubscription();
    let topicCallback = {
      execute: (message: IMessage) => {
        console.error("Topic callback not found for: " + topic)
      }
    };
    messagesService.onConnect.and.callFake((callback) => {
      callback({
        subscribe: (destination: string, callback: (message: IMessage) => void) => {
          if (destination == topic) {
            console.log("Test log: Comparing " + destination + " vs " + topic + " - " + (destination === topic));
            console.info("Test log: Topic found! " + destination);
            topicCallback.execute = callback;
          }
          return mockSubscription;
        }
      } as Client);
    });
    return topicCallback;
  }

  it('should update download progress', () => {
    const progressUpdateMessage: FileBackupProgressUpdatedEvent = {
      percentage: 25,
      timeLeftSeconds: 999
    };
    const payload: IMessage = {body: JSON.stringify(progressUpdateMessage)} as any;
    const topicCallback = getCallbackForTopic(FileBackupMessageTopics.ProgressUpdate);

    fixture.detectChanges();
    topicCallback.execute(payload);
    fixture.detectChanges();

    const progressBar = fixture.debugElement.query(By.css('.progress'));
    expect(progressBar.nativeElement.textContent).toContain('25%');
  });

  it('should clear currently downloaded game when FileBackupStatusChangedEvent' +
    ' is received with status Success', () => {
    mockFileBackupStatusChangedEventReceived(expectedCurrentlyProcessing.gameFileId, FileBackupStatus.Success);

    const currentlyDownloadingTable = fixture.debugElement.query(By.css('#currently-downloading'));
    expect(currentlyDownloadingTable.nativeElement.textContent).toContain('Nothing is currently being backed up');
  });

  function mockFileBackupStatusChangedEventReceived(id: string, newStatus: FileBackupStatus) {
    const statusChangedMessage: FileBackupStatusChangedEvent = {
      gameFileId: id,
      newStatus: newStatus
    };
    const payload: IMessage = {body: JSON.stringify(statusChangedMessage)} as any;
    const topicCallback = getCallbackForTopic(FileBackupMessageTopics.StatusChanged);

    fixture.detectChanges();
    topicCallback.execute(payload);
    fixture.detectChanges();
  }

  it('should clear currently downloaded game when FileBackupStatusChangedEvent' +
    ' is received with status Failed', () => {
    mockFileBackupStatusChangedEventReceived(expectedCurrentlyProcessing.gameFileId, FileBackupStatus.Failed);

    const currentlyDownloadingTable = fixture.debugElement.query(By.css('#currently-downloading'));
    expect(currentlyDownloadingTable.nativeElement.textContent).toContain('Nothing is currently being backed up');
  });

  it('should not clear currently downloaded game when FileBackupStatusChangedEvent' +
    ' is received with status other than Success or Failed', () => {
    mockFileBackupStatusChangedEventReceived(expectedCurrentlyProcessing.gameFileId, FileBackupStatus.InProgress);

    const currentlyDownloadingTable = fixture.debugElement.query(By.css('#currently-downloading'));
    expect(currentlyDownloadingTable.nativeElement.textContent).not.toContain('Nothing is currently being backed up');
  });

  it('should not clear currently downloaded game when FileBackupStatusChangedEvent' +
    ' is received with id other than current and status Success', () => {
    mockFileBackupStatusChangedEventReceived('anotherGameFileId', FileBackupStatus.Success);

    const currentlyDownloadingTable = fixture.debugElement.query(By.css('#currently-downloading'));
    expect(currentlyDownloadingTable.nativeElement.textContent).not.toContain('Nothing is currently being backed up');
  });

  it('should not clear currently downloaded game when FileBackupStatusChangedEvent' +
    ' is received with id other than current and status Failed', () => {
    mockFileBackupStatusChangedEventReceived('anotherGameFileId', FileBackupStatus.Failed);

    const currentlyDownloadingTable = fixture.debugElement.query(By.css('#currently-downloading'));
    expect(currentlyDownloadingTable.nativeElement.textContent).not.toContain('Nothing is currently being backed up');
  });

  it('should do nothing when when FileBackupStatusChangedEvent' +
    ' is received and currently downloaded file is already cleared', () => {
    fixture.detectChanges();
    component.currentDownload = undefined;
    mockFileBackupStatusChangedEventReceived('anotherGameFileId', FileBackupStatus.Failed);

    const currentlyDownloadingTable = fixture.debugElement.query(By.css('#currently-downloading'));
    expect(currentlyDownloadingTable.nativeElement.textContent).toContain('Nothing is currently being backed up');
  });
});
