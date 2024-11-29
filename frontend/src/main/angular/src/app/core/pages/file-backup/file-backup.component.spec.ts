import {ComponentFixture, TestBed} from '@angular/core/testing';
import {FileBackupComponent} from './file-backup.component';
import {provideHttpClientTesting} from "@angular/common/http/testing";
import {PageHeaderStubComponent} from "@app/shared/components/page-header/page-header.component.stub";
import {TableComponent} from "@app/shared/components/table/table.component";
import {LoadedContentStubComponent} from "@app/shared/components/loaded-content/loaded-content.component.stub";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {of} from "rxjs";
import {
  FileBackupMessageTopics,
  FileBackupProgressUpdatedEvent,
  FileBackupStartedEvent,
  FileBackupStatus,
  FileBackupStatusChangedEvent,
  GameFile,
  GameFileProcessingStatus,
  GameFilesClient,
  PageGameFile
} from "@backend";
import {By} from "@angular/platform-browser";
import {TableColumnDirective} from "@app/shared/components/table/column-directive/table-column.directive";
import {Client, IMessage, StompSubscription} from "@stomp/stompjs";
import {provideHttpClient, withInterceptorsFromDi} from '@angular/common/http';
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {ButtonComponent} from "@app/shared/components/button/button.component";
import anything = jasmine.anything;
import SpyObj = jasmine.SpyObj;
import createSpyObj = jasmine.createSpyObj;
import createSpy = jasmine.createSpy;

describe('FileBackupComponent', () => {
  let component: FileBackupComponent;
  let fixture: ComponentFixture<FileBackupComponent>;
  let gameFilesClient: SpyObj<GameFilesClient>;
  let messagesService: SpyObj<MessagesService>;
  let notificationService: NotificationService;

  const aMockSubscription = (): StompSubscription => ({unsubscribe: createSpy()}) as any;

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

  const createPageGameFile = (title: string, fileTitle: string, size: string, status: FileBackupStatus): PageGameFile => ({
    content: [{
      ...sampleGameFile,
      gameProviderFile: {
        ...sampleGameFile.gameProviderFile,
        originalGameTitle: title,
        fileTitle: fileTitle,
        size: size
      },
      fileBackup: {status}
    }]
  });

  const enqueuedDownloads = createPageGameFile("Some queued game", "queuedGame.exe", "1 GB", FileBackupStatus.Discovered);
  const processedFiles = createPageGameFile("Some processed game", "processedGame.exe", "2 GB", FileBackupStatus.Success);
  const currentlyProcessedGameFile: GameFile = {
    ...sampleGameFile,
    gameProviderFile: {
      ...sampleGameFile.gameProviderFile,
      originalGameTitle: "Some current game",
      fileTitle: "currentGame.exe",
      size: "3 GB"
    },
    fileBackup: {status: FileBackupStatus.InProgress, filePath: 'some/file/path'}
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
      imports: [ButtonComponent],
      providers: [
        {
          provide: GameFilesClient,
          useValue: createSpyObj('GameFilesClient', ['getGameFiles', 'getCurrentlyDownloading'])
        },
        {provide: MessagesService, useValue: createSpyObj('MessagesService', ["onConnect"])},
        {
          provide: NotificationService, useValue: createSpyObj('NotificationService',
            ['showSuccess', 'showFailure'])
        },
        provideHttpClient(withInterceptorsFromDi()),
        provideHttpClientTesting()
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(FileBackupComponent);
    component = fixture.componentInstance;
    gameFilesClient = TestBed.inject(GameFilesClient) as SpyObj<GameFilesClient>;
    messagesService = TestBed.inject(MessagesService) as SpyObj<MessagesService>;
    notificationService = TestBed.inject(NotificationService);

    gameFilesClient.getGameFiles.withArgs(GameFileProcessingStatus.Enqueued, anything()).and.returnValue(of(enqueuedDownloads) as any);
    gameFilesClient.getGameFiles.withArgs(GameFileProcessingStatus.Processed, anything()).and.returnValue(of(processedFiles) as any);
    gameFilesClient.getCurrentlyDownloading.and.returnValue(of(currentlyProcessedGameFile) as any);
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  function mockMessageServiceOnConnectAction(
    action: (destination: string, callback: (message: IMessage) => void) => void) {
    const mockSubscription = aMockSubscription();
    messagesService.onConnect.and.callFake((callback) => {
      callback({
        subscribe: (destination: string, callback: (message: IMessage) => void) => {
          action(destination, callback);
          return mockSubscription;
        }
      } as Client);
    });
  }

  it('should subscribe to message topics on initialization', () => {
    const topicsSubscribed: string[] = [];
    mockMessageServiceOnConnectAction((destination, callback) =>
      topicsSubscribed.push(destination));

    component.ngOnInit();

    expect(messagesService.onConnect).toHaveBeenCalled();
    expect(topicsSubscribed).toEqual([
      FileBackupMessageTopics.Started,
      FileBackupMessageTopics.ProgressUpdate,
      FileBackupMessageTopics.StatusChanged
    ]);
    expect(component['stompSubscriptions'].length).toBe(3);
  });

  it('should unsubscribe from message topics on destruction', () => {
    const subscription = aMockSubscription();
    component['stompSubscriptions'].push(subscription);

    component.ngOnDestroy();

    expect(subscription.unsubscribe).toHaveBeenCalled();
  });

  it('should retrieve files on init', async () => {
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    expectFilesToBeLoaded();

    expectCurrentlyDownloadingGameTitleToContain("Some current game");
    expectEnqueuedGameTitleToContain("Some queued game");
    expectProcessedGameTitleToContain("Some processed game");
  });

  function expectCurrentlyDownloadingGameTitleToContain(expectedGameTitle: string) {
    expectGameTitleIn('#currently-downloading', expectedGameTitle);
  }

  function expectEnqueuedGameTitleToContain(expectedGameTitle: string) {
    expectGameTitleIn('#download-queue', expectedGameTitle);
  }

  function expectProcessedGameTitleToContain(expectedGameTitle: string) {
    expectGameTitleIn('#processed-files', expectedGameTitle);
  }

  function expectGameTitleIn(selector: string, title: string) {
    const table = fixture.debugElement.query(By.css(selector));
    expect(table.nativeElement.textContent).toContain(title);
  }

  it('should log an error when removeFromQueue is called', async () => {
    await component.removeFromQueue()();
    expect(notificationService.showFailure).toHaveBeenCalledWith('Removing from queue not yet implemented');
  });

  it('should update currently downloaded game', async () => {
    const fileBackupStartedEvent: FileBackupStartedEvent = {...expectedCurrentlyProcessing};
    fileBackupStartedEvent.originalGameTitle = "Updated game title";

    await simulateWebSocketMessageReceived(FileBackupMessageTopics.Started, fileBackupStartedEvent);

    expectCurrentlyDownloadingGameTitleToContain("Updated game title")
  });

  async function simulateWebSocketMessageReceived<T>(topic: string, message: T) {
    const payload: IMessage = {body: JSON.stringify(message)} as any;
    const topicCallback = getCallbackForTopic(topic);

    fixture.detectChanges();
    await fixture.whenStable();

    topicCallback.execute(payload);
    await fixture.whenStable();

    fixture.detectChanges();
  }

  function getCallbackForTopic(topic: string) {
    let topicCallback = {
      execute: (message: IMessage) => {
        console.error("Topic callback not found for: " + topic)
      }
    };

    mockMessageServiceOnConnectAction((destination, callback) => {
      if (destination == topic) {
        topicCallback.execute = callback;
      }
    });

    return topicCallback;
  }

  it('should update download progress', async () => {
    const progressUpdatedEvent: FileBackupProgressUpdatedEvent = {
      percentage: 25,
      timeLeftSeconds: 999
    };

    await simulateWebSocketMessageReceived(FileBackupMessageTopics.ProgressUpdate, progressUpdatedEvent);

    const progressBar = fixture.debugElement.query(By.css('.progress'));
    expect(progressBar.nativeElement.textContent).toContain('25%');
  });

  function expectFilesToBeLoaded() {
    expect(gameFilesClient.getGameFiles).toHaveBeenCalledWith(GameFileProcessingStatus.Enqueued, {
      page: 0,
      size: component['pageSize']
    });
    expect(gameFilesClient.getGameFiles).toHaveBeenCalledWith(GameFileProcessingStatus.Processed, {
      page: 0,
      size: component['pageSize']
    });
    expect(gameFilesClient.getCurrentlyDownloading).toHaveBeenCalled();
    expect(component.currentDownload).toEqual(expectedCurrentlyProcessing);
    expect(component.filesAreLoading).toBe(false);
  }

  it('should clear currently downloaded game when FileBackupStatusChangedEvent' +
    ' is received with status Success', async () => {
    await simulateFileBackupStatusChangedEventReceived(
      expectedCurrentlyProcessing.gameFileId, FileBackupStatus.Success);

    const currentlyDownloadingTable = fixture.debugElement.query(By.css('#currently-downloading'));
    expect(currentlyDownloadingTable.nativeElement.textContent).toContain('Nothing is currently being backed up');
  });

  async function simulateFileBackupStatusChangedEventReceived(id: string, newStatus: FileBackupStatus) {
    const statusChangedMessage: FileBackupStatusChangedEvent = {
      gameFileId: id,
      newStatus: newStatus
    };
    await simulateWebSocketMessageReceived(FileBackupMessageTopics.StatusChanged, statusChangedMessage);
  }

  it('should clear currently downloaded game when FileBackupStatusChangedEvent' +
    ' is received with status Failed', () => {
    simulateFileBackupStatusChangedEventReceived(expectedCurrentlyProcessing.gameFileId, FileBackupStatus.Failed);

    const currentlyDownloadingTable = fixture.debugElement.query(By.css('#currently-downloading'));
    expect(currentlyDownloadingTable.nativeElement.textContent).toContain('Nothing is currently being backed up');
  });

  it('should not clear currently downloaded game when FileBackupStatusChangedEvent' +
    ' is received with status other than Success or Failed', async () => {
    await simulateFileBackupStatusChangedEventReceived(
      expectedCurrentlyProcessing.gameFileId, FileBackupStatus.InProgress);

    const currentlyDownloadingTable = fixture.debugElement.query(By.css('#currently-downloading'));
    expect(currentlyDownloadingTable.nativeElement.textContent).not.toContain('Nothing is currently being backed up');
  });

  it('should not clear currently downloaded game when FileBackupStatusChangedEvent' +
    ' is received with id other than current and status Success', async () => {
    await simulateFileBackupStatusChangedEventReceived('anotherGameFileId', FileBackupStatus.Success);

    const currentlyDownloadingTable = fixture.debugElement.query(By.css('#currently-downloading'));
    expect(currentlyDownloadingTable.nativeElement.textContent).not.toContain('Nothing is currently being backed up');
  });

  it('should not clear currently downloaded game when FileBackupStatusChangedEvent' +
    ' is received with id other than current and status Failed', async () => {
    await simulateFileBackupStatusChangedEventReceived('anotherGameFileId', FileBackupStatus.Failed);

    const currentlyDownloadingTable = fixture.debugElement.query(By.css('#currently-downloading'));
    expect(currentlyDownloadingTable.nativeElement.textContent).not.toContain('Nothing is currently being backed up');
  });

  it('should do nothing when FileBackupStatusChangedEvent' +
    ' is received and currently downloaded file is already cleared', () => {
    gameFilesClient.getCurrentlyDownloading.and.returnValue(of(undefined) as any);
    simulateFileBackupStatusChangedEventReceived('anotherGameFileId', FileBackupStatus.Failed);

    const currentlyDownloadingTable = fixture.debugElement.query(By.css('#currently-downloading'));
    expect(currentlyDownloadingTable.nativeElement.textContent).toContain('Nothing is currently being backed up');
  });
});