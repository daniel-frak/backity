import {ComponentFixture, TestBed} from '@angular/core/testing';
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
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {ModalService} from "@app/shared/services/modal-service/modal.service";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {By} from "@angular/platform-browser";
import {MessageTesting} from "@app/shared/testing/message-testing";
import {DebugElement} from "@angular/core";
import {provideRouter} from "@angular/router";

import {GamesWithFilesCardComponent} from './games-with-files-card.component';
import {TestGameFile} from "@app/shared/testing/objects/test-game-file";
import {TestGame} from '@app/shared/testing/objects/test-game';
import {TestPage} from "@app/shared/testing/objects/test-page";
import {TestFileBackupStatusChangedEvent} from "@app/shared/testing/objects/test-file-backup-status-changed-event";
import createSpyObj = jasmine.createSpyObj;
import SpyObj = jasmine.SpyObj;

describe('GamesWithFilesCardComponent', () => {
  let component: GamesWithFilesCardComponent;
  let fixture: ComponentFixture<GamesWithFilesCardComponent>;

  let gamesClient: SpyObj<GamesClient>;
  let gameFilesClient: SpyObj<GameFilesClient>;
  let fileBackupsClient: SpyObj<FileBackupsClient>;
  let messagesService: SpyObj<MessagesService>;
  let notificationService: SpyObj<NotificationService>;
  let modalService: SpyObj<ModalService>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GamesWithFilesCardComponent],
      providers: [
        provideRouter([]),
        {provide: GamesClient, useValue: createSpyObj('GamesClient', ['getGames'])},
        {provide: GameFilesClient, useValue: createSpyObj('GameFilesClient', ['enqueueFileBackup'])},
        {provide: FileBackupsClient, useValue: createSpyObj('FileBackupsClient', ['deleteFileBackup'])},
        {provide: MessagesService, useValue: createSpyObj('MessagesService', ['watch'])},
        {provide: NotificationService, useValue: createSpyObj('NotificationService', ['showSuccess', 'showFailure'])},
        {provide: ModalService, useValue: createSpyObj('ModalService', ['withConfirmationModal'])}
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(GamesWithFilesCardComponent);
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
    modalService.withConfirmationModal
      .and.callFake((message: string, callback: () => Promise<void>) => callback());
  }

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with loading state', () => {
    expect(component.gamesAreLoading).toBeTrue();
  });

  it('should get games on init', async () => {
    const gameFile: GameFile = TestGameFile.discovered();
    const game: { id: string; title: string; files: GameFile[] } = TestGame.withFiles([gameFile]);
    const gameWithFilesPage: PageGameWithFiles = TestPage.of([game]);
    gamesClient.getGames.and.returnValue(of(gameWithFilesPage) as any);

    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(gamesClient.getGames).toHaveBeenCalledWith({page: 0, size: component.pageSize});
    expect(component.gameWithFilesPage).toEqual(gameWithFilesPage);
    expect(component.gamesAreLoading).toBeFalse();

    const pageText = fixture.debugElement.nativeElement.textContent;
    expect(pageText).toContain(game.title);
    expect(pageText).toContain(gameFile.gameProviderFile.fileTitle);
  });

  it('should log an error when games cannot be retrieved', async () => {
    const mockError = new Error('Discovery failed');

    gamesClient.getGames.and.returnValue(throwError(() => mockError));

    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(notificationService.showFailure).toHaveBeenCalledWith('Error fetching games', mockError);
  });

  it('should back up game file and set its status to Enqueued', async () => {
    const gameFile = TestGameFile.enqueued();
    gameFilesClient.enqueueFileBackup.and.returnValue(of(null) as any);

    await component.enqueueFileBackup(gameFile);

    expect(gameFile.fileBackup?.status).toBe(FileBackupStatus.Enqueued);
    expect(gameFilesClient.enqueueFileBackup).toHaveBeenCalledWith(gameFile.id);
    expect(notificationService.showSuccess).toHaveBeenCalledWith(`File backup enqueued`);
  });

  it('should set file status to Discovered and log error when backup fails', async () => {
    const gameFile = TestGameFile.enqueued();
    const mockError = new Error('Backup error');
    gameFilesClient.enqueueFileBackup.and.returnValue(throwError(() => mockError));

    await component.enqueueFileBackup(gameFile);

    expect(gameFile.fileBackup?.status).toBe(FileBackupStatus.Discovered);
    expect(gameFilesClient.enqueueFileBackup).toHaveBeenCalledWith(gameFile.id);
    expect(notificationService.showFailure).toHaveBeenCalledWith(
      `An error occurred while trying to enqueue a file`,
      gameFile, mockError);
  });

  it('should log an error for various operations', async () => {
    const operations = [
      {
        method: () => component.cancelBackup('someGameFileId'),
        message: 'Removing from queue not yet implemented'
      },
      {
        method: () => component.viewFilePath('someFileId'),
        message: 'Viewing file paths not yet implemented'
      },
      {
        method: () => component.download('someFileId'),
        message: 'Downloading files not yet implemented'
      },
      {
        method: () => component.viewError('someFileId'),
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
    fileBackupsClient.deleteFileBackup.and.returnValue(of(null) as any);
    const gameWithFilesPage: PageGameWithFiles = TestPage.of([TestGame.withDiscoveredFile()]);
    gamesClient.getGames.and.returnValue(of(gameWithFilesPage) as any);

    await component.deleteBackup(gameFileId);

    expect(fileBackupsClient.deleteFileBackup).toHaveBeenCalledWith(gameFileId);
    expect(gamesClient.getGames).toHaveBeenCalled();
    expect(notificationService.showSuccess).toHaveBeenCalledWith('Deleted file backup');
  });

  it('should log error when file backup could not be deleted', async () => {
    const gameFileId = 'someGameFileId';
    const mockError = new Error('Backup error');
    fileBackupsClient.deleteFileBackup.and.returnValue(throwError(() => mockError));

    await component.deleteBackup(gameFileId);

    expect(notificationService.showFailure).toHaveBeenCalledWith(
      `An error occurred while trying to delete a file backup`, gameFileId, mockError);
  });

  it('should update file status when status changed event is received', async () => {
    const gameFile: GameFile = TestGameFile.discovered();
    component.gameWithFilesPage = TestPage.of([TestGame.withFiles([gameFile])]);
    await simulateBackupStatusChangedEventReceived(gameFile.id, FileBackupStatus.InProgress);

    expect(component.gameWithFilesPage?.content?.[0].files?.[0].fileBackup?.status).toBe(FileBackupStatus.InProgress);
    const gameListTable: DebugElement = getGameListTable();
    expect(gameListTable.nativeElement.textContent).toContain(FileBackupStatus.InProgress);
  });

  async function simulateBackupStatusChangedEventReceived(
    gameFileId: string, newStatus: FileBackupStatus): Promise<void> {
    const statusChangedMessage: FileBackupStatusChangedEvent =
      TestFileBackupStatusChangedEvent.with(gameFileId, newStatus);
    await MessageTesting.simulateWebSocketMessageReceived(fixture, messagesService,
      FileBackupMessageTopics.StatusChanged, statusChangedMessage);
  }

  function getGameListTable() {
    return fixture.debugElement.query(By.css('[data-testid="game-list"]'));
  }

  it('should handle StatusChanged event when no matching file exists', async () => {
    const gameFile: GameFile = TestGameFile.discovered();
    component.gameWithFilesPage = TestPage.of([TestGame.withFiles([gameFile])]);
    await simulateBackupStatusChangedEventReceived("nonExistentGameId", FileBackupStatus.InProgress);

    expect(component.gameWithFilesPage?.content?.[0].files?.[0].fileBackup?.status).toBe(FileBackupStatus.Discovered);
    const gameListTable: DebugElement = getGameListTable();
    expect(gameListTable.nativeElement.textContent).not.toContain(FileBackupStatus.InProgress);
  });
});
