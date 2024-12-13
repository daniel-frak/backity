import {ComponentFixture, TestBed} from '@angular/core/testing';
import {GamesComponent} from './games.component';
import {provideHttpClientTesting} from "@angular/common/http/testing";
import {
  FileBackupMessageTopics,
  FileBackupsClient,
  FileBackupStatus,
  FileBackupStatusChangedEvent,
  GameFile,
  GameFilesClient,
  GamesClient,
  PageGameWithFiles
} from "@backend";
import {of, throwError} from "rxjs";
import {PageHeaderStubComponent} from "@app/shared/components/page-header/page-header.component.stub";
import {TableComponent} from "@app/shared/components/table/table.component";
import {LoadedContentStubComponent} from "@app/shared/components/loaded-content/loaded-content.component.stub";
import {TableColumnDirective} from "@app/shared/components/table/column-directive/table-column.directive";
import {provideHttpClient, withInterceptorsFromDi} from '@angular/common/http';
import {ButtonComponent} from "@app/shared/components/button/button.component";
import {FileStatusBadgeComponent} from "@app/core/pages/games/file-status-badge/file-status-badge.component";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {ModalService} from "@app/shared/services/modal-service/modal.service";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {By} from "@angular/platform-browser";
import {MessageTesting} from "@app/shared/testing/message-testing";
import {DebugElement} from "@angular/core";
import createSpyObj = jasmine.createSpyObj;
import Spy = jasmine.Spy;
import SpyObj = jasmine.SpyObj;

describe('GamesComponent', () => {
  let component: GamesComponent;
  let fixture: ComponentFixture<GamesComponent>;

  let gamesClient: SpyObj<GamesClient>;
  let gameFilesClient: SpyObj<GameFilesClient>;
  let fileBackupsClient: SpyObj<FileBackupsClient>;
  let messagesService: SpyObj<MessagesService>;
  let notificationService: SpyObj<NotificationService>;
  let modalService: SpyObj<ModalService>;

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

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        ButtonComponent,
        GamesComponent,
        TableComponent,
        TableColumnDirective,
        FileStatusBadgeComponent,
        PageHeaderStubComponent,
        LoadedContentStubComponent
      ],
      providers: [
        {provide: GamesClient, useValue: createSpyObj('GamesClient', ['getGames'])},
        {provide: GameFilesClient, useValue: createSpyObj('GameFilesClient', ['enqueueFileBackup'])},
        {provide: FileBackupsClient, useValue: createSpyObj('FileBackupsClient', ['deleteFileBackup'])},
        {provide: MessagesService, useValue: createSpyObj('MessagesService', ['watch'])},
        {provide: NotificationService, useValue: createSpyObj('NotificationService', ['showSuccess', 'showFailure'])},
        {provide: ModalService, useValue: createSpyObj('ModalService', ['withConfirmationModal'])},
        provideHttpClient(withInterceptorsFromDi()),
        provideHttpClientTesting()
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(GamesComponent);
    component = fixture.componentInstance;
    gamesClient = TestBed.inject(GamesClient) as SpyObj<GamesClient>;
    gameFilesClient = TestBed.inject(GameFilesClient) as SpyObj<GameFilesClient>;
    fileBackupsClient = TestBed.inject(FileBackupsClient) as SpyObj<FileBackupsClient>;
    messagesService = TestBed.inject(MessagesService) as SpyObj<MessagesService>;
    notificationService = TestBed.inject(NotificationService) as SpyObj<NotificationService>;
    modalService = TestBed.inject(ModalService) as SpyObj<ModalService>;

    autoConfirmModals();

    MessageTesting.mockWatch(messagesService, (destination, callback) => {
      // Do nothing
    });
  });

  function autoConfirmModals() {
    (modalService.withConfirmationModal as Spy)
      .and.callFake((message: string, callback: () => Promise<void>) => callback());
  }

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with loading state', () => {
    expect(component.gamesAreLoading).toBeTrue();
  });

  it('should get games on init', async () => {
    const gameFile = {...sampleGameFile, gameProviderFile: {...sampleGameFile.gameProviderFile, fileTitle: 'game.exe'}};

    const mockGames: PageGameWithFiles = {
      content: [{
        id: "someGameId",
        title: "someGameTitle",
        files: [gameFile]
      }]
    };
    (gamesClient.getGames as Spy).and.returnValue(of(mockGames));

    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(gamesClient.getGames).toHaveBeenCalledWith({page: 0, size: 20});
    expect(component.gameWithFilesPage).toEqual(mockGames);
    expect(component.gamesAreLoading).toBeFalse();

    const pageText = fixture.debugElement.nativeElement.textContent;
    expect(pageText).toContain('someGameTitle');
    expect(pageText).toContain('game.exe');
  });

  it('should log an error when games cannot be retrieved', async () => {
    const mockError = new Error('Discovery failed');

    (gamesClient.getGames as Spy).and.returnValue(throwError(() => mockError));

    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(notificationService.showFailure).toHaveBeenCalledWith('Error fetching games', undefined, mockError);
  });

  it('should back up game file and set its status to Enqueued', async () => {
    const gameFile = {...sampleGameFile, fileBackup: {status: FileBackupStatus.Enqueued}};
    (gameFilesClient.enqueueFileBackup as Spy).and.returnValue(of(null));

    await component.enqueueFileBackup(gameFile)();

    expect(gameFile.fileBackup?.status).toBe(FileBackupStatus.Enqueued);
    expect(gameFilesClient.enqueueFileBackup).toHaveBeenCalledWith(gameFile.id);
    expect(notificationService.showSuccess).toHaveBeenCalledWith(`File backup enqueued`);
  });

  it('should set file status to Discovered and log error when backup fails', async () => {
    const gameFile = {
      ...sampleGameFile,
      fileBackup: {
        status: FileBackupStatus.Discovered
      }
    };
    const mockError = new Error('Backup error');
    (gameFilesClient.enqueueFileBackup as Spy).and.returnValue(throwError(() => mockError));

    await component.enqueueFileBackup(gameFile)();

    expect(gameFile.fileBackup?.status).toBe(FileBackupStatus.Discovered);
    expect(gameFilesClient.enqueueFileBackup).toHaveBeenCalledWith(gameFile.id);
    expect(notificationService.showFailure).toHaveBeenCalledWith(
      `An error occurred while trying to enqueue a file`,
      undefined, gameFile, mockError);
  });

  it('should log an error for various operations', async () => {
    const operations = [
      {
        method: () => component.cancelBackup('someGameFileId')(),
        message: 'Removing from queue not yet implemented'
      },
      {
        method: () => component.viewFilePath('someFileId')(),
        message: 'Viewing file paths not yet implemented'
      },
      {
        method: () => component.download('someFileId')(),
        message: 'Downloading files not yet implemented'
      },
      {
        method: () => component.viewError('someFileId')(),
        message: 'Viewing errors not yet implemented'
      }
    ];

    for (const op of operations) {
      await op.method();
      expect(notificationService.showFailure).toHaveBeenCalledWith(op.message);
    }
  });

  it('should delete file backup and refresh list', async () => {
    const gameFileId = 'someGameFileId';
    (fileBackupsClient.deleteFileBackup as Spy).and.returnValue(of(null));
    const mockGames: PageGameWithFiles = {
      content: [{
        id: "someGameId",
        title: "someGameTitle",
        files: []
      }]
    };
    (gamesClient.getGames as Spy).and.returnValue(of(mockGames));

    await component.deleteBackup(gameFileId)();

    expect(fileBackupsClient.deleteFileBackup).toHaveBeenCalledWith(gameFileId);
    expect(gamesClient.getGames).toHaveBeenCalled();
    expect(notificationService.showSuccess).toHaveBeenCalledWith('Deleted file backup');
  });

  it('should log error when file backup could not be deleted', async () => {
    const gameFileId = 'someGameFileId';
    const mockError = new Error('Backup error');
    (fileBackupsClient.deleteFileBackup as Spy).and.returnValue(throwError(() => mockError));

    await component.deleteBackup(gameFileId)();

    expect(notificationService.showFailure).toHaveBeenCalledWith(
      `An error occurred while trying to delete a file backup`, undefined, gameFileId, mockError);
  });

  it('should update file status when status changed event is received', async () => {
    const gameFile = {
      ...sampleGameFile,
      fileBackup: {
        status: FileBackupStatus.Discovered
      }
    };
    mockGameFilesPage(gameFile);
    await simulateBackupStatusChangedEventReceived(gameFile.id, FileBackupStatus.InProgress);

    expect(component.gameWithFilesPage?.content?.[0].files?.[0].fileBackup?.status).toBe(FileBackupStatus.InProgress);
    const gameListTable: DebugElement = getGameListTable();
    expect(gameListTable.nativeElement.textContent).toContain('IN_PROGRESS');
  });

  function mockGameFilesPage(gameFile: GameFile) {
    component.gameWithFilesPage = {
      content: [{
        id: "someGameId",
        title: "someGameTitle",
        files: [gameFile]
      }]
    };
  }

  async function simulateBackupStatusChangedEventReceived(gameFileId: string, newStatus: FileBackupStatus) {
    const statusChangedMessage: FileBackupStatusChangedEvent = {
      gameFileId: gameFileId,
      newStatus: newStatus
    };
    await MessageTesting.simulateWebSocketMessageReceived(fixture, messagesService,
      FileBackupMessageTopics.StatusChanged, statusChangedMessage);
  }

  function getGameListTable() {
    return fixture.debugElement.query(By.css('[data-testid="game-list"]'));
  }

  it('should handle StatusChanged event when no matching file exists', async () => {
    const gameFile = {
      ...sampleGameFile,
      fileBackup: {
        status: FileBackupStatus.Discovered
      }
    };
    mockGameFilesPage(gameFile);
    await simulateBackupStatusChangedEventReceived("nonExistentGameId", FileBackupStatus.InProgress);

    expect(component.gameWithFilesPage?.content?.[0].files?.[0].fileBackup?.status).toBe(FileBackupStatus.Discovered);
    const gameListTable: DebugElement = getGameListTable();
    expect(gameListTable.nativeElement.textContent).not.toContain('IN_PROGRESS');
  });

});
