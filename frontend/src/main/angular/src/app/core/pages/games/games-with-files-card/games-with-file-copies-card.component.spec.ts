import {ComponentFixture, TestBed} from '@angular/core/testing';
import {
  Configuration,
  EnqueueFileCopyRequest,
  FileBackupMessageTopics,
  FileCopiesClient,
  BackupTargetsClient,
  FileCopy,
  FileCopyStatus,
  FileCopyStatusChangedEvent,
  GamesClient,
  GameWithFileCopies,
  PageGameWithFileCopies, FileCopyNaturalId
} from "@backend";
import {of, throwError} from "rxjs";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {ModalService} from "@app/shared/services/modal-service/modal.service";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {By} from "@angular/platform-browser";
import {MessageTesting} from "@app/shared/testing/message-testing";
import {DebugElement} from "@angular/core";
import {provideRouter} from "@angular/router";

import {GamesWithFileCopiesCardComponent} from './games-with-file-copies-card.component';
import {TestGame} from '@app/shared/testing/objects/test-game';
import {TestPage} from "@app/shared/testing/objects/test-page";
import {TestFileCopyStatusChangedEvent} from "@app/shared/testing/objects/test-file-copy-status-changed-event";
import {TestFileCopy} from "@app/shared/testing/objects/test-file-copy";
import createSpyObj = jasmine.createSpyObj;
import SpyObj = jasmine.SpyObj;
import { TestBackupTarget } from '@app/shared/testing/objects/test-backup-target';

describe('GamesWithFileCopiesCardComponent', () => {
  let component: GamesWithFileCopiesCardComponent;
  let fixture: ComponentFixture<GamesWithFileCopiesCardComponent>;

  let gamesClient: SpyObj<GamesClient>;
  let fileCopiesClient: SpyObj<FileCopiesClient>;
  let backupTargetsClient: SpyObj<BackupTargetsClient>;
  let messagesService: SpyObj<MessagesService>;
  let notificationService: SpyObj<NotificationService>;
  let modalService: SpyObj<ModalService>;
  let mockWindow = {location: {href: ''}};

  const localFolderBackupTarget = TestBackupTarget.localFolder();
  const s3BackupTarget = TestBackupTarget.s3();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GamesWithFileCopiesCardComponent],
      providers: [
        provideRouter([]),
        {provide: GamesClient, useValue: createSpyObj('GamesClient', ['getGames'])},
        {
          provide: FileCopiesClient,
          useValue: createSpyObj('FileCopiesClient', ['deleteFileCopy', 'enqueueFileCopy'])
        },
        {
          provide: BackupTargetsClient,
          useValue: createSpyObj('BackupTargetsClient', ['getBackupTargets'])
        },
        {provide: MessagesService, useValue: createSpyObj('MessagesService', ['watch'])},
        {provide: NotificationService, useValue: createSpyObj('NotificationService', ['showSuccess', 'showFailure'])},
        {provide: ModalService, useValue: createSpyObj('ModalService', ['withConfirmationModal'])},
        {provide: 'Window', useValue: mockWindow}
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(GamesWithFileCopiesCardComponent);
    component = fixture.componentInstance;
    gamesClient = TestBed.inject(GamesClient) as SpyObj<GamesClient>;
    fileCopiesClient = TestBed.inject(FileCopiesClient) as SpyObj<FileCopiesClient>;
    backupTargetsClient = TestBed.inject(BackupTargetsClient) as SpyObj<BackupTargetsClient>;
    messagesService = TestBed.inject(MessagesService) as SpyObj<MessagesService>;
    notificationService = TestBed.inject(NotificationService) as SpyObj<NotificationService>;
    modalService = TestBed.inject(ModalService) as SpyObj<ModalService>;

    autoConfirmModals();

    MessageTesting.mockWatch(messagesService, (destination, callback) => {
      // Do nothing
    });

    backupTargetsClient.getBackupTargets.and.returnValue(of([localFolderBackupTarget, s3BackupTarget]) as any);
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
    const gameWithFileCopies: GameWithFileCopies = TestGame.withDiscoveredFileCopy();
    gameWithFileCopies.gameFilesWithCopies[0].fileCopies[0].naturalId.backupTargetId = localFolderBackupTarget.id;
    const gameWithFileCopiesPage: PageGameWithFileCopies = TestPage.of([gameWithFileCopies]);
    gamesClient.getGames.and.returnValue(of(gameWithFileCopiesPage) as any);

    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(gamesClient.getGames).toHaveBeenCalledWith({page: 0, size: component.pageSize});
    expect(component.gameWithFileCopiesPage).toEqual(gameWithFileCopiesPage);
    expect(component.gamesAreLoading).toBeFalse();

    const pageText = fixture.debugElement.nativeElement.textContent;
    expect(pageText).toContain(gameWithFileCopies.title);
    expect(pageText).toContain(gameWithFileCopies.gameFilesWithCopies[0].gameFile.fileSource.fileTitle);
    expect(pageText).toContain(localFolderBackupTarget.title);
    expect(pageText).toContain(s3BackupTarget.title);
  });

  it('should log an error when games cannot be retrieved', async () => {
    const mockError = new Error('Discovery failed');

    gamesClient.getGames.and.returnValue(throwError(() => mockError));

    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(notificationService.showFailure).toHaveBeenCalledWith('Error fetching games', mockError);
  });

  it('should back up file copy and set its status to Enqueued', async () => {
    const fileCopy = TestFileCopy.enqueued();
    fileCopiesClient.enqueueFileCopy.and.returnValue(of(null) as any);

    await component.enqueueFileCopy(fileCopy);

    expect(fileCopy.status).toBe(FileCopyStatus.Enqueued);
    let enqueueRequest = enqueueFileCopyRequestFrom(fileCopy);
    expect(fileCopiesClient.enqueueFileCopy).toHaveBeenCalledWith(enqueueRequest);
    expect(notificationService.showSuccess).toHaveBeenCalledWith(`File copy enqueued`);
  });

  function enqueueFileCopyRequestFrom(fileCopy: FileCopy) {
    let enqueueRequest: EnqueueFileCopyRequest = {
      fileCopyNaturalId: fileCopy.naturalId
    };
    return enqueueRequest;
  }

  it('should set file status to Discovered and log error when backup fails', async () => {
    const fileCopy = TestFileCopy.enqueued();
    fileCopy.naturalId.backupTargetId = localFolderBackupTarget.id;
    const mockError = new Error('Backup error');
    fileCopiesClient.enqueueFileCopy.and.returnValue(throwError(() => mockError));

    await component.enqueueFileCopy(fileCopy);

    expect(fileCopy.status).toBe(FileCopyStatus.Discovered);
    let enqueueRequest = enqueueFileCopyRequestFrom(fileCopy);
    expect(fileCopiesClient.enqueueFileCopy).toHaveBeenCalledWith(enqueueRequest);
    expect(notificationService.showFailure).toHaveBeenCalledWith(
      `An error occurred while trying to enqueue a file`,
      fileCopy, mockError);
  });

  it('should log an error for unimplemented operations', async () => {
    const operations = [
      {
        method: () => component.onClickCancelBackup('someFileCopyId')(),
        message: 'Removing from queue not yet implemented'
      },
      {
        method: () => component.onClickViewFilePath('someFileCopyId')(),
        message: 'Viewing file paths not yet implemented'
      },
      {
        method: () => component.onClickViewError('someFileCopyId')(),
        message: 'Viewing errors not yet implemented'
      }
    ];

    for (const op of operations) {
      await op.method();
      expect(notificationService.showFailure).toHaveBeenCalledWith(op.message);
    }
  });

  it('should delete file copy and refresh list', async () => {
    const gameFileId = 'someGameFileId';
    fileCopiesClient.deleteFileCopy.and.returnValue(of(null) as any);
    const gameWithFileCopies = TestGame.withDiscoveredFileCopy();
    gameWithFileCopies.gameFilesWithCopies[0].fileCopies[0].naturalId.backupTargetId = localFolderBackupTarget.id;
    const gameWithFileCopiesPage: PageGameWithFileCopies = TestPage.of([gameWithFileCopies]);
    gamesClient.getGames.and.returnValue(of(gameWithFileCopiesPage) as any);

    await component.deleteFileCopy(gameFileId);

    expect(fileCopiesClient.deleteFileCopy).toHaveBeenCalledWith(gameFileId);
    expect(gamesClient.getGames).toHaveBeenCalled();
    expect(notificationService.showSuccess).toHaveBeenCalledWith('Deleted file copy');
  });

  it('should log error when file copy could not be deleted', async () => {
    const gameFileId = 'someGameFileId';
    const mockError = new Error('Backup error');
    fileCopiesClient.deleteFileCopy.and.returnValue(throwError(() => mockError));

    await component.deleteFileCopy(gameFileId);

    expect(notificationService.showFailure).toHaveBeenCalledWith(
      `An error occurred while trying to delete a file copy`, gameFileId, mockError);
  });

  it('should update file status when status changed event is received', async () => {
    const gameWithFileCopies: GameWithFileCopies = TestGame.withDiscoveredFileCopy();
    gameWithFileCopies.gameFilesWithCopies[0].fileCopies[0].naturalId.backupTargetId = localFolderBackupTarget.id;
    const gameWithFileCopiesPage: PageGameWithFileCopies = TestPage.of([gameWithFileCopies]);
    gamesClient.getGames.and.returnValue(of(gameWithFileCopiesPage) as any);

    const fileCopy = (gameWithFileCopies.gameFilesWithCopies[0].fileCopies)[0];
    await simulateFileCopyStatusChangedEventReceived(
      fileCopy.id, fileCopy.naturalId, FileCopyStatus.InProgress);

    expect(component.gameWithFileCopiesPage?.content?.[0]?.gameFilesWithCopies[0]?.fileCopies[0]?.status)
      .toBe(FileCopyStatus.InProgress);
    const gameListTable: DebugElement = getGameListTable();
    expect(gameListTable.nativeElement.textContent).toContain(FileCopyStatus.InProgress);
  });

  async function simulateFileCopyStatusChangedEventReceived(
    fileCopyId: string, fileCopyNaturalId: FileCopyNaturalId, newStatus: FileCopyStatus): Promise<void> {
    const statusChangedMessage: FileCopyStatusChangedEvent =
      TestFileCopyStatusChangedEvent.with(fileCopyId, fileCopyNaturalId, newStatus);
    await MessageTesting.simulateWebSocketMessageReceived(fixture, messagesService,
      FileBackupMessageTopics.StatusChanged, statusChangedMessage);
  }

  function getGameListTable() {
    return fixture.debugElement.query(By.css('[data-testid="game-list"]'));
  }

  it('should handle StatusChanged event when no matching fileCopy exists', async () => {
    const gameWithFileCopies: GameWithFileCopies = TestGame.withDiscoveredFileCopy();
    gameWithFileCopies.gameFilesWithCopies[0].fileCopies[0].naturalId.backupTargetId = localFolderBackupTarget.id;
    component.gameWithFileCopiesPage = TestPage.of([gameWithFileCopies]);
    const fileCopy = (gameWithFileCopies.gameFilesWithCopies[0].fileCopies)[0];
    await simulateFileCopyStatusChangedEventReceived(
      "nonExistentFileCopyId", fileCopy.naturalId, FileCopyStatus.InProgress);

    expect(component.gameWithFileCopiesPage?.content?.[0]?.gameFilesWithCopies[0]?.fileCopies[0]?.status)
      .toBe(FileCopyStatus.Discovered);
    const gameListTable: DebugElement = getGameListTable();
    expect(gameListTable.nativeElement.textContent).not.toContain(FileCopyStatus.InProgress);
  });

  it('should download file', async () => {
    const gameWithFileCopies: GameWithFileCopies = TestGame.withSuccessfulFileCopy();
    gameWithFileCopies.gameFilesWithCopies[0].fileCopies[0].naturalId.backupTargetId = localFolderBackupTarget.id;
    const gameWithFileCopiesPage: PageGameWithFileCopies = TestPage.of([gameWithFileCopies]);
    gamesClient.getGames.and.returnValue(of(gameWithFileCopiesPage) as any);

    const fileCopy: FileCopy = gameWithFileCopies.gameFilesWithCopies[0].fileCopies[0];
    const configuration: SpyObj<Configuration> = createSpyObj('Configuration', ['encodeParam']);
    configuration.basePath = 'someBasePath';
    configuration.encodeParam.withArgs({
      name: "fileCopyId", value: fileCopy.id, in: "path", style: "simple", explode: false, dataType: "string",
      dataFormat: undefined
    }).and.returnValue(fileCopy.id);
    fileCopiesClient.configuration = configuration;

    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    const gameListTable: DebugElement = getGameListTable();
    const downloadBtn: DebugElement = gameListTable.query(By.css('[data-testid="download-file-copy-btn"]'));

    downloadBtn.nativeElement.click();

    expect(mockWindow.location.href).toBe(`someBasePath/api/file-copies/${fileCopy.id}`);
  });

  it('should return an empty string when backup target name is unavailable', async () => {
    const gameWithFileCopies: GameWithFileCopies = TestGame.withDiscoveredFileCopy();
    // Make sure backupTargetId doesn't match any existing backup target
    gameWithFileCopies.gameFilesWithCopies[0].fileCopies[0].naturalId.backupTargetId = 'nonExistentBackupTargetId';
    const gameWithFileCopiesPage: PageGameWithFileCopies = TestPage.of([gameWithFileCopies]);

    gamesClient.getGames.and.returnValue(of(gameWithFileCopiesPage) as any);

    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    // Simulate the `getBackupTargetName` call
    const backupTargetName = component.getBackupTargetName('nonExistentBackupTargetId');

    expect(backupTargetName).toBe('');
  });
});
