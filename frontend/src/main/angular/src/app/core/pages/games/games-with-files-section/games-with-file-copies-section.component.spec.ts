import {ComponentFixture, TestBed} from '@angular/core/testing';
import {
  BackupTarget,
  BackupTargetsClient,
  Configuration,
  EnqueueFileCopyRequest,
  FileBackupMessageTopics,
  FileCopiesClient,
  FileCopy,
  FileCopyNaturalId,
  FileCopyStatus,
  FileCopyStatusChangedEvent,
  FileDownloadProgressUpdatedEvent,
  GamesClient,
  GameWithFileCopies,
  PageGameWithFileCopies,
  StorageSolutionsClient,
  StorageSolutionStatus,
  StorageSolutionStatusesResponse
} from "@backend";
import {of, throwError} from "rxjs";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {ModalService} from "@app/shared/services/modal-service/modal.service";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {By} from "@angular/platform-browser";
import {MessageTesting} from "@app/shared/testing/message-testing";
import {DebugElement} from "@angular/core";
import {provideRouter} from "@angular/router";

import {GamesWithFileCopiesSectionComponent} from './games-with-file-copies-section.component';
import {TestGameWithFileCopies} from '@app/shared/testing/objects/test-game-with-file-copies';
import {TestPage} from "@app/shared/testing/objects/test-page";
import {TestFileCopyStatusChangedEvent} from "@app/shared/testing/objects/test-file-copy-status-changed-event";
import {TestFileCopy} from "@app/shared/testing/objects/test-file-copy";
import {TestBackupTarget} from '@app/shared/testing/objects/test-backup-target';
import {TestProgressUpdatedEvent} from "@app/shared/testing/objects/test-progress-updated-event";
import {TestProgress} from "@app/shared/testing/objects/test-progress";
import {AutoLayoutComponent} from "@app/shared/components/auto-layout/auto-layout.component";
import {AutoLayoutStubComponent} from "@app/shared/components/auto-layout/auto-layout.stub.component";
import {deepClone} from "@app/shared/testing/deep-clone";
import {
  PotentialFileCopyWithContext
} from "@app/core/pages/games/games-with-files-section/potential-file-copy-with-context";
import {TestGameFile} from "@app/shared/testing/objects/test-game-file";
import createSpyObj = jasmine.createSpyObj;
import SpyObj = jasmine.SpyObj;
import {PotentialFileCopyFactory} from "@app/core/pages/games/games-with-files-section/potential-file-copy";

describe('GamesWithFileCopiesSectionComponent', () => {
  let component: GamesWithFileCopiesSectionComponent;
  let fixture: ComponentFixture<GamesWithFileCopiesSectionComponent>;

  let gamesClient: SpyObj<GamesClient>;
  let fileCopiesClient: SpyObj<FileCopiesClient>;
  let backupTargetsClient: SpyObj<BackupTargetsClient>;
  let storageSolutionsClient: SpyObj<StorageSolutionsClient>;
  let messagesService: SpyObj<MessagesService>;
  let notificationService: SpyObj<NotificationService>;
  let modalService: SpyObj<ModalService>;
  let mockWindow = {location: {href: ''}};

  let localFolderBackupTarget: BackupTarget;
  let s3BackupTarget: BackupTarget;
  let initialStorageSolutionStatusResponse: StorageSolutionStatusesResponse;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GamesWithFileCopiesSectionComponent],
      providers: [
        provideRouter([]),
        {provide: GamesClient, useValue: createSpyObj('GamesClient', ['getGames'])},
        {
          provide: FileCopiesClient,
          useValue: createSpyObj('FileCopiesClient', ['deleteFileCopy', 'enqueueFileCopy', 'cancelFileCopy'])
        },
        {
          provide: BackupTargetsClient,
          useValue: createSpyObj('BackupTargetsClient', ['getBackupTargets'])
        },
        {
          provide: StorageSolutionsClient,
          useValue: createSpyObj('StorageSolutionsClient', ['getStorageSolutionStatuses'])
        },
        {provide: MessagesService, useValue: createSpyObj('MessagesService', ['watch'])},
        {provide: NotificationService, useValue: createSpyObj('NotificationService', ['showSuccess', 'showFailure'])},
        {provide: ModalService, useValue: createSpyObj('ModalService', ['withConfirmationModal'])},
        {provide: 'Window', useValue: mockWindow}
      ]
    })
      .overrideComponent(GamesWithFileCopiesSectionComponent, {
        remove: {imports: [AutoLayoutComponent]},
        add: {imports: [AutoLayoutStubComponent]}
      })
      .compileComponents();

    fixture = TestBed.createComponent(GamesWithFileCopiesSectionComponent);
    component = fixture.componentInstance;
    gamesClient = TestBed.inject(GamesClient) as SpyObj<GamesClient>;
    fileCopiesClient = TestBed.inject(FileCopiesClient) as SpyObj<FileCopiesClient>;
    backupTargetsClient = TestBed.inject(BackupTargetsClient) as SpyObj<BackupTargetsClient>;
    storageSolutionsClient = TestBed.inject(StorageSolutionsClient) as SpyObj<StorageSolutionsClient>;
    messagesService = TestBed.inject(MessagesService) as SpyObj<MessagesService>;
    notificationService = TestBed.inject(NotificationService) as SpyObj<NotificationService>;
    modalService = TestBed.inject(ModalService) as SpyObj<ModalService>;

    localFolderBackupTarget = TestBackupTarget.localFolder();
    s3BackupTarget = TestBackupTarget.s3();
    initialStorageSolutionStatusResponse = {
      statuses: {
        "someStorageSolutionId": StorageSolutionStatus.Connected
      }
    };

    autoConfirmModals();

    MessageTesting.mockWatch(messagesService, (destination, callback) => {
      // Do nothing
    });

    backupTargetsClient.getBackupTargets.and.returnValue(of([localFolderBackupTarget, s3BackupTarget]) as any);
    mockStorageSolutionStatuses(initialStorageSolutionStatusResponse);
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

  it('should refresh on init', async () => {
    const gameWithFileCopies: GameWithFileCopies = TestGameWithFileCopies.withInProgressFileCopy();
    const fileCopyWithProgress = gameWithFileCopies.gameFilesWithCopies[0].fileCopiesWithProgress[0];
    fileCopyWithProgress.fileCopy.naturalId.backupTargetId = localFolderBackupTarget.id;
    const gameWithFileCopiesPage: PageGameWithFileCopies = TestPage.of([gameWithFileCopies]);
    gamesClient.getGames.and.returnValue(of(gameWithFileCopiesPage) as any);

    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(gamesClient.getGames).toHaveBeenCalledWith({page: 0, size: component.pageSize});
    expect(component.gameWithFileCopiesPage).toEqual(gameWithFileCopiesPage);
    expect(component.storageSolutionStatusesById.get("someStorageSolutionId"))
      .toEqual(StorageSolutionStatus.Connected);
    expect(component.gamesAreLoading).toBeFalse();

    const pageText = fixture.debugElement.nativeElement.textContent;
    expect(pageText).toContain(gameWithFileCopies.title);
    expect(pageText).toContain(gameWithFileCopies.gameFilesWithCopies[0].gameFile.fileSource.fileTitle);
    expect(pageText).toContain(localFolderBackupTarget.name);
    expect(pageText).toContain(s3BackupTarget.name);
    expect(pageText).toContain(fileCopyWithProgress.progress!.percentage + "%");
  });

  it('should log an error when games cannot be retrieved', async () => {
    const mockError = new Error('Discovery failed');

    gamesClient.getGames.and.returnValue(throwError(() => mockError));

    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(notificationService.showFailure).toHaveBeenCalledWith('Error fetching games', mockError);
  });

  it('should enqueue file copy and set its status to Enqueued', async () => {
    const fileCopy = TestFileCopy.enqueued();
    fileCopiesClient.enqueueFileCopy.and.returnValue(of(null) as any);

    await component.enqueueFileCopy(fileCopy);

    expect(fileCopy.status).toBe(FileCopyStatus.Enqueued);
    const enqueueRequest = enqueueFileCopyRequestFrom(fileCopy);
    expect(fileCopiesClient.enqueueFileCopy).toHaveBeenCalledWith(enqueueRequest);
    expect(notificationService.showSuccess).toHaveBeenCalledWith(`File copy enqueued`);
  });

  function enqueueFileCopyRequestFrom(fileCopy: FileCopy) {
    const enqueueRequest: EnqueueFileCopyRequest = {
      fileCopyNaturalId: fileCopy.naturalId
    };
    return enqueueRequest;
  }

  it('should cancel file copy backup and set its status to Tracked', async () => {
    const fileCopy = TestFileCopy.inProgress();
    fileCopiesClient.cancelFileCopy.and.returnValue(of(null) as any);
    const potentialFileCopyWithContext: PotentialFileCopyWithContext = {
      gameFile: TestGameFile.any(),
      potentialFileCopy: fileCopy,
      progress: TestProgress.twentyFivePercent(),
      backupTarget: TestBackupTarget.localFolder(),
      storageSolutionStatus: StorageSolutionStatus.Connected
    };
    component.potentialFileCopiesWithContextByGameFileId.set(fileCopy.naturalId.gameFileId,
      [potentialFileCopyWithContext]);

    await component.cancelBackup(fileCopy);

    expect(fileCopy.status).toBe(FileCopyStatus.Tracked);
    expect(fileCopiesClient.cancelFileCopy).toHaveBeenCalledWith(fileCopy.id);
    expect(notificationService.showSuccess).toHaveBeenCalledWith(`Backup cancelled`);
    expect(potentialFileCopyWithContext.progress).toBeUndefined();
  });

  it('cancelBackup should do nothing if potentialFileCopy doesn\'t have id', async () => {
    const fileCopy =
      PotentialFileCopyFactory.missing('someGameFileId', 'someBackupTargetId');
    const potentialFileCopyWithContext: PotentialFileCopyWithContext = {
      gameFile: TestGameFile.any(),
      potentialFileCopy: fileCopy,
      progress: TestProgress.twentyFivePercent(),
      backupTarget: TestBackupTarget.localFolder(),
      storageSolutionStatus: StorageSolutionStatus.Connected
    };
    component.potentialFileCopiesWithContextByGameFileId.set(fileCopy.naturalId.gameFileId,
      [potentialFileCopyWithContext]);

    await component.cancelBackup(fileCopy);

    expect(fileCopy.status).toBeUndefined();
    expect(fileCopiesClient.cancelFileCopy).not.toHaveBeenCalled();
    expect(notificationService.showSuccess).not.toHaveBeenCalled();
    expect(potentialFileCopyWithContext.progress).not.toBeUndefined();
  });

  it('should log error when cancelling file copy backup fails', async () => {
    const fileCopy = TestFileCopy.enqueued();
    const mockError = new Error('Backup error');
    fileCopiesClient.cancelFileCopy.and.returnValue(throwError(() => mockError));

    await component.cancelBackup(fileCopy);

    expect(notificationService.showFailure).toHaveBeenCalledWith(
      `An error occurred while trying to cancel the backup`, fileCopy, mockError);
  });

  it('should log error when enqueuing file copy fails', async () => {
    const fileCopy = TestFileCopy.tracked();
    fileCopy.naturalId.backupTargetId = localFolderBackupTarget.id;
    const mockError = new Error('Backup error');
    fileCopiesClient.enqueueFileCopy.and.returnValue(throwError(() => mockError));

    await component.enqueueFileCopy(fileCopy);

    expect(notificationService.showFailure).toHaveBeenCalledWith(
      `An error occurred while trying to enqueue a file`,
      fileCopy, mockError);
  });

  it('should log an error for unimplemented operations', async () => {
    const operations = [
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
    const gameWithFileCopies = TestGameWithFileCopies.withTrackedFileCopy();
    gameWithFileCopies.gameFilesWithCopies[0].fileCopiesWithProgress[0].fileCopy.naturalId.backupTargetId =
      localFolderBackupTarget.id;
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

  it('should update file copy status when status changed event is received', async () => {
    const gameWithFileCopies: GameWithFileCopies = TestGameWithFileCopies.withTrackedFileCopy();
    mockGameWithFileCopiesExists(gameWithFileCopies);
    await simulateStatusChangedEventReceivedToInProgressForFirstFileCopy(gameWithFileCopies);

    expect(component.gameWithFileCopiesPage?.content?.[0]?.gameFilesWithCopies[0]
      ?.fileCopiesWithProgress[0].fileCopy?.status)
      .toBe(FileCopyStatus.InProgress);
    const gameList: DebugElement = getGameList();
    expect(gameList.nativeElement.textContent).toContain(FileCopyStatus.InProgress);
  });

  async function simulateFileCopyStatusChangedEventReceived(
    fileCopyId: string, fileCopyNaturalId: FileCopyNaturalId, newStatus: FileCopyStatus): Promise<void> {
    const statusChangedMessage: FileCopyStatusChangedEvent =
      TestFileCopyStatusChangedEvent.withContent(fileCopyId, fileCopyNaturalId, newStatus);
    await MessageTesting.simulateWebSocketMessageReceived(fixture, messagesService,
      FileBackupMessageTopics.StatusChanged, statusChangedMessage);
  }

  function getGameList() {
    return fixture.debugElement.query(By.css('[data-testid="game-list"]'));
  }

  function mockGameWithFileCopiesExists(gameWithFileCopies: GameWithFileCopies) {
    gameWithFileCopies.gameFilesWithCopies[0].fileCopiesWithProgress[0].fileCopy.naturalId.backupTargetId =
      localFolderBackupTarget.id;
    const gameWithFileCopiesPage: PageGameWithFileCopies = TestPage.of([gameWithFileCopies]);
    gamesClient.getGames.and.returnValue(of(gameWithFileCopiesPage) as any);
  }

  async function simulateStatusChangedEventReceivedToInProgressForFirstFileCopy(
    gameWithFileCopies: GameWithFileCopies) {
    const fileCopy = (gameWithFileCopies.gameFilesWithCopies[0].fileCopiesWithProgress)[0]?.fileCopy;
    await simulateFileCopyStatusChangedEventReceived(
      fileCopy.id, fileCopy.naturalId, FileCopyStatus.InProgress);
  }

  it('should remove file update progress when status changed event is received', async () => {
    const gameWithFileCopies: GameWithFileCopies = TestGameWithFileCopies.withTrackedFileCopy();
    mockGameWithFileCopiesExists(gameWithFileCopies);

    expect(component.potentialFileCopiesWithContextByGameFileId!.get(
      gameWithFileCopies.gameFilesWithCopies[0].gameFile.id)?.[0]?.progress)
      .toBe(undefined);
  });

  it('should handle StatusChanged event when no matching fileCopy exists', async () => {
    const gameWithFileCopies: GameWithFileCopies = TestGameWithFileCopies.withTrackedFileCopy();
    const anotherGameWithFileCopies: GameWithFileCopies = TestGameWithFileCopies.withTrackedFileCopy();
    anotherGameWithFileCopies.gameFilesWithCopies[0].fileCopiesWithProgress[0]
      .fileCopy.naturalId.backupTargetId = 'anotherBackupTargetId';
    mockGameWithFileCopiesExists(gameWithFileCopies);
    await simulateStatusChangedEventReceivedToInProgressForFirstFileCopy(anotherGameWithFileCopies);
    const gameListTable: DebugElement = getGameList();
    expect(gameListTable.nativeElement.textContent).not.toContain(FileCopyStatus.InProgress);
  });

  it('should update file copy progress when progress changed event is received' +
    ' and file copy is found in list',
    async () => {
      const gameWithFileCopies: GameWithFileCopies = TestGameWithFileCopies.withInProgressFileCopy();
      const fileCopy = gameWithFileCopies.gameFilesWithCopies[0].fileCopiesWithProgress[0].fileCopy;
      fileCopy.naturalId.backupTargetId = localFolderBackupTarget.id;
      const gameWithFileCopiesPage: PageGameWithFileCopies = TestPage.of([gameWithFileCopies]);
      gamesClient.getGames.and.returnValue(of(gameWithFileCopiesPage) as any);

      const progressChangedMessage: FileDownloadProgressUpdatedEvent =
        TestProgressUpdatedEvent.twentyFivePercent(fileCopy.id, fileCopy.naturalId);
      await simulateFileCopyProgressChangedEventReceived(progressChangedMessage);

      expect(component.potentialFileCopiesWithContextByGameFileId!.get(
        gameWithFileCopies.gameFilesWithCopies[0].gameFile.id)?.[0]?.progress)
        .toEqual(TestProgress.twentyFivePercent());
    });

  it('should not update file copy progress when progress changed event is received' +
    ' and file copy is not found in list',
    async () => {
      const gameWithFileCopies: GameWithFileCopies = TestGameWithFileCopies.withTrackedFileCopy();
      const gameWithFileCopiesPage: PageGameWithFileCopies = TestPage.of([gameWithFileCopies]);
      gamesClient.getGames.and.returnValue(of(gameWithFileCopiesPage) as any);

      const progressChangedMessage: FileDownloadProgressUpdatedEvent =
        TestProgressUpdatedEvent.twentyFivePercent(
          'unknownFileCopyId', {gameFileId: 'unknownGameFileId', backupTargetId: 'unknownBackupTargetId'});
      await simulateFileCopyProgressChangedEventReceived(progressChangedMessage);

      expect(component.potentialFileCopiesWithContextByGameFileId!.get(
        gameWithFileCopies.gameFilesWithCopies[0].gameFile.id)?.[0]?.progress)
        .toBeUndefined();
    });

  async function simulateFileCopyProgressChangedEventReceived(progressChangedMessage: FileDownloadProgressUpdatedEvent):
    Promise<void> {
    await MessageTesting.simulateWebSocketMessageReceived(fixture, messagesService,
      FileBackupMessageTopics.ProgressUpdate, progressChangedMessage);
  }

  function mockStorageSolutionStatuses(response: StorageSolutionStatusesResponse) {
    storageSolutionsClient.getStorageSolutionStatuses
      .and.returnValue(of(deepClone(response)) as any);
  }

  it('should download file', async () => {
    const gameWithFileCopies: GameWithFileCopies = TestGameWithFileCopies.withStoredUnverifiedFileCopy();
    gameWithFileCopies.gameFilesWithCopies[0].fileCopiesWithProgress[0].fileCopy.naturalId.backupTargetId =
      localFolderBackupTarget.id;
    const gameWithFileCopiesPage: PageGameWithFileCopies = TestPage.of([gameWithFileCopies]);
    gamesClient.getGames.and.returnValue(of(gameWithFileCopiesPage) as any);

    const fileCopy: FileCopy = gameWithFileCopies.gameFilesWithCopies[0].fileCopiesWithProgress[0].fileCopy;
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

    const gameListTable: DebugElement = getGameList();
    const downloadBtn: DebugElement = gameListTable.query(By.css('[data-testid="download-file-copy-btn"]'));

    downloadBtn.nativeElement.click();

    expect(mockWindow.location.href).toBe(`someBasePath/api/file-copies/${fileCopy.id}`);
  });
});
