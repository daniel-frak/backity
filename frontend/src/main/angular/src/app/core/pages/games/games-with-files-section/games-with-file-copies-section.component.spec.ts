import {ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {
  BackupTarget,
  BackupTargetsClient,
  Configuration,
  EnqueueFileCopyRequest,
  FileBackupMessageTopics,
  FileCopiesClient,
  FileCopy,
  FileCopyNaturalId,
  FileCopyReplicationProgressUpdatedEvent,
  FileCopyStatus,
  FileCopyStatusChangedEvent,
  FileCopyWithProgress,
  GamesClient,
  GameWithFileCopies,
  SourceFile,
  StorageSolutionsClient,
  StorageSolutionStatus,
  StorageSolutionStatusesResponse
} from "@backend";
import {of, throwError} from "rxjs";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {ModalService} from "@app/shared/services/modal-service/modal.service";
import {MessageService} from "@app/shared/backend/services/message.service";
import {MessageSimulator} from "@app/shared/testing/message-simulator";
import {By} from "@angular/platform-browser";
import {DebugElement} from "@angular/core";
import {ActivatedRoute, provideRouter} from "@angular/router";

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
import {
  PotentialFileCopyWithContext
} from "@app/core/pages/games/games-with-files-section/potential-file-copy-with-context";
import {TestSourceFile} from "@app/shared/testing/objects/test-source-file";
import {PotentialFileCopyFactory} from "@app/core/pages/games/games-with-files-section/potential-file-copy";
import {Page} from "@app/shared/components/table/page";
import createSpyObj = jasmine.createSpyObj;
import SpyObj = jasmine.SpyObj;

describe('GamesWithFileCopiesSectionComponent', () => {
  let component: GamesWithFileCopiesSectionComponent;
  let fixture: ComponentFixture<GamesWithFileCopiesSectionComponent>;

  let gamesClient: SpyObj<GamesClient>;
  let fileCopiesClient: SpyObj<FileCopiesClient>;
  let backupTargetsClient: SpyObj<BackupTargetsClient>;
  let storageSolutionsClient: SpyObj<StorageSolutionsClient>;
  let messageService: SpyObj<MessageService>;
  let notificationService: SpyObj<NotificationService>;
  let modalService: SpyObj<ModalService>;
  let mockWindow: any;

  let messageSimulator: MessageSimulator;

  let localFolderBackupTarget: BackupTarget;
  let s3BackupTarget: BackupTarget;
  let initialStorageSolutionStatusResponse: StorageSolutionStatusesResponse;

  beforeEach(async () => {
    mockWindow = {location: {href: ''}};
    await TestBed.configureTestingModule({
      imports: [GamesWithFileCopiesSectionComponent],
      providers: [
        provideRouter([]),
        {provide: ActivatedRoute, useValue: {queryParams: of({})}},
        {provide: GamesClient, useValue: createSpyObj('GamesClient', ['getGames'])},
        {
          provide: FileCopiesClient,
          useValue: createSpyObj('FileCopiesClient', ['enqueueFileCopy', 'cancelFileCopy', 'deleteFileCopy'])
        },
        {
          provide: BackupTargetsClient,
          useValue: createSpyObj('BackupTargetsClient', ['getBackupTargets'])
        },
        {
          provide: StorageSolutionsClient,
          useValue: createSpyObj('StorageSolutionsClient', ['getStorageSolutionStatuses'])
        },
        {provide: MessageService, useValue: createSpyObj('MessageService', ['watch'])},
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

    gamesClient = TestBed.inject(GamesClient) as SpyObj<GamesClient>;
    fileCopiesClient = TestBed.inject(FileCopiesClient) as SpyObj<FileCopiesClient>;
    (fileCopiesClient as any).configuration = new Configuration({basePath: 'http://localhost:8080'});
    backupTargetsClient = TestBed.inject(BackupTargetsClient) as SpyObj<BackupTargetsClient>;
    storageSolutionsClient = TestBed.inject(StorageSolutionsClient) as SpyObj<StorageSolutionsClient>;
    messageService = TestBed.inject(MessageService) as SpyObj<MessageService>;
    notificationService = TestBed.inject(NotificationService) as SpyObj<NotificationService>;
    modalService = TestBed.inject(ModalService) as SpyObj<ModalService>;

    messageSimulator = MessageSimulator.given(messageService);

    localFolderBackupTarget = TestBackupTarget.localFolder();
    s3BackupTarget = TestBackupTarget.s3();
    initialStorageSolutionStatusResponse = {
      statuses: {
        "someStorageSolutionId": StorageSolutionStatus.Connected
      }
    };

    backupTargetsClient.getBackupTargets.and.returnValue(of([localFolderBackupTarget, s3BackupTarget]) as any);
    storageSolutionsClient.getStorageSolutionStatuses.and.returnValue(of(initialStorageSolutionStatusResponse) as any);
    gamesClient.getGames.and.returnValue(of({content: []}) as any);

    fixture = TestBed.createComponent(GamesWithFileCopiesSectionComponent);
    component = fixture.componentInstance;
    autoConfirmModals();
  });

  function autoConfirmModals() {
    modalService.withConfirmationModal
      .and.callFake((message: string, callback: () => Promise<void>) => callback());
  }

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should refresh on init', fakeAsync(() => {
    const gameWithFileCopies: GameWithFileCopies = TestGameWithFileCopies.withInProgressFileCopy();
    const fileCopyWithProgress = gameWithFileCopies.sourceFilesWithCopies[0].fileCopiesWithProgress[0];
    fileCopyWithProgress.fileCopy.naturalId.backupTargetId = localFolderBackupTarget.id;
    const gameWithFileCopiesPage: Page<GameWithFileCopies> = TestPage.of([gameWithFileCopies]);
    gamesClient.getGames.and.returnValue(of(gameWithFileCopiesPage) as any);
    backupTargetsClient.getBackupTargets.and.returnValue(of([localFolderBackupTarget, s3BackupTarget]) as any);
    storageSolutionsClient.getStorageSolutionStatuses.and.returnValue(of(initialStorageSolutionStatusResponse) as any);

    fixture.detectChanges(); // triggers ngOnInit -> refresh
    tick();

    expect(gamesClient.getGames).toHaveBeenCalledWith({page: 0, size: component.pageSize()}, '', null as any);
    expect(component.gameWithFileCopiesPage()).toEqual(gameWithFileCopiesPage);
    expect(component.storageSolutionStatusesById().get("someStorageSolutionId"))
      .toEqual(StorageSolutionStatus.Connected);
    expect(component.gamesAreLoading()).toBeFalse();

    const pageText = fixture.debugElement.nativeElement.textContent;
    expect(pageText).toContain(gameWithFileCopies.title);
    expect(pageText).toContain(gameWithFileCopies.sourceFilesWithCopies[0].sourceFile.fileTitle);
    expect(pageText).toContain(localFolderBackupTarget.name);
    expect(pageText).toContain(s3BackupTarget.name);
    expect(pageText).toContain(fileCopyWithProgress.progress!.percentage + "%");
  }));

  it('submitting the search form should reset page to 1 and perform a search', fakeAsync(() => {
    component.pageNumber.set(3);

    fixture.detectChanges();
    tick();
    gamesClient.getGames.calls.reset();

    component.searchForm.controls.searchBox.setValue('doom');
    const form: DebugElement = fixture.debugElement.query(By.css('[data-testid="search-form"]'));
    form.triggerEventHandler('ngSubmit', {});
    tick();

    expect(gamesClient.getGames).toHaveBeenCalledWith(
      { page: 0, size: component.pageSize() },
      'doom',
      null as any
    );
  }));

  it('should download file', async () => {
    const fileCopyId = 'someFileCopyId';

    await component.download(fileCopyId);

    expect(mockWindow.location.href).toContain('http://localhost:8080/api/file-copies/someFileCopyId');
  });

  it('onClickDownload should return a callable that delegates to download', async () => {
    await component.onClickDownload('someFileCopyId')();

    expect(mockWindow.location.href).toContain('http://localhost:8080/api/file-copies/someFileCopyId');
  });

  it('should log an error when games cannot be retrieved', fakeAsync(() => {
    const mockError = new Error('Discovery failed');

    gamesClient.getGames.and.returnValue(throwError(() => mockError));
    backupTargetsClient.getBackupTargets.and.returnValue(of([]) as any);
    storageSolutionsClient.getStorageSolutionStatuses.and.returnValue(of({statuses: {}}) as any);

    fixture.detectChanges();
    tick();

    expect(notificationService.showFailure).toHaveBeenCalledWith('Error fetching games', mockError);
  }));

  it('should block refresh when already loading', async () => {
    gamesClient.getGames.calls.reset();
    component.gamesAreLoading.set(true);

    await component.refresh();

    expect(gamesClient.getGames).not.toHaveBeenCalled();
  });

  it('should enqueue file copy and set its status to Enqueued', async () => {
    const fileCopy = TestFileCopy.enqueued();
    fileCopy.naturalId.backupTargetId = localFolderBackupTarget.id;
    fileCopiesClient.enqueueFileCopy.and.returnValue(of(null) as any);

    // seed component state with matching context to allow immutable update
    const ctx: PotentialFileCopyWithContext = {
      sourceFile: TestSourceFile.any(),
      potentialFileCopy: fileCopy as any,
      progress: undefined,
      backupTarget: localFolderBackupTarget,
      storageSolutionStatus: StorageSolutionStatus.Connected
    };
    const key = `${fileCopy.naturalId.sourceFileId}-${fileCopy.naturalId.backupTargetId}`;
    component.potentialFileCopiesWithContext.set(new Map([[key, ctx]]));

    await component.enqueueFileCopy(fileCopy as any);

    const updated = component.getPotentialFileCopyWithContext(
      fileCopy.naturalId.sourceFileId, fileCopy.naturalId.backupTargetId)!;
    expect(updated.potentialFileCopy.status).toBe(FileCopyStatus.Enqueued);
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
    fileCopy.naturalId.backupTargetId = localFolderBackupTarget.id;
    fileCopiesClient.cancelFileCopy.and.returnValue(of(null) as any);
    const potentialFileCopyWithContext: PotentialFileCopyWithContext = {
      sourceFile: TestSourceFile.any(),
      potentialFileCopy: fileCopy as any,
      progress: TestProgress.twentyFivePercent(),
      backupTarget: localFolderBackupTarget,
      storageSolutionStatus: StorageSolutionStatus.Connected
    };

    const key = `${fileCopy.naturalId.sourceFileId}-${fileCopy.naturalId.backupTargetId}`;
    component.potentialFileCopiesWithContext.set(new Map([[key, potentialFileCopyWithContext]]));

    await component.cancelBackup(fileCopy as any);

    const updated = component.getPotentialFileCopyWithContext(
      fileCopy.naturalId.sourceFileId, fileCopy.naturalId.backupTargetId)!;
    expect(updated.potentialFileCopy.status).toBe(FileCopyStatus.Tracked);
    expect(fileCopiesClient.cancelFileCopy).toHaveBeenCalledWith(fileCopy.id);
    expect(notificationService.showSuccess).toHaveBeenCalledWith(`Backup canceled`);
    expect(updated.progress).toBeUndefined();
  });

  it('cancelBackup should do nothing if potentialFileCopy doesn\'t have id', async () => {
    const fileCopy =
      PotentialFileCopyFactory.missing('someSourceFileId', 'someBackupTargetId');
    const potentialFileCopyWithContext: PotentialFileCopyWithContext = {
      sourceFile: TestSourceFile.any(),
      potentialFileCopy: fileCopy,
      progress: TestProgress.twentyFivePercent(),
      backupTarget: TestBackupTarget.localFolder(),
      storageSolutionStatus: StorageSolutionStatus.Connected
    };

    const key = `${fileCopy.naturalId.sourceFileId}-${fileCopy.naturalId.backupTargetId}`;
    component.potentialFileCopiesWithContext.set(new Map([[key, potentialFileCopyWithContext]]));

    await component.cancelBackup(fileCopy);

    const fileCopyInComponent: PotentialFileCopyWithContext = component.getPotentialFileCopyWithContext(
      fileCopy.naturalId.sourceFileId, fileCopy.naturalId.backupTargetId)!;
    expect(fileCopyInComponent.potentialFileCopy.status).toBeUndefined();
    expect(fileCopyInComponent.progress).not.toBeUndefined();
    expect(fileCopiesClient.cancelFileCopy).not.toHaveBeenCalled();
    expect(notificationService.showSuccess).not.toHaveBeenCalled();
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

  it('should delete file copy and refresh list', fakeAsync(() => {
    const sourceFileId = 'someSourceFileId';
    fileCopiesClient.deleteFileCopy.and.returnValue(of(null) as any);
    const gameWithFileCopies = TestGameWithFileCopies.withTrackedFileCopy();
    gameWithFileCopies.sourceFilesWithCopies[0].fileCopiesWithProgress[0].fileCopy.naturalId.backupTargetId =
      localFolderBackupTarget.id;
    const gameWithFileCopiesPage: Page<GameWithFileCopies> = TestPage.of([gameWithFileCopies]);
    gamesClient.getGames.and.returnValue(of(gameWithFileCopiesPage) as any);

    component.deleteFileCopy(sourceFileId);
    tick();

    expect(fileCopiesClient.deleteFileCopy).toHaveBeenCalledWith(sourceFileId);
    expect(gamesClient.getGames).toHaveBeenCalled();
    expect(notificationService.showSuccess).toHaveBeenCalledWith('Deleted file copy');
  }));

  it('onClickDeleteFileCopy should return a callable that deletes the file copy', async () => {
    await component.onClickDeleteFileCopy('someFileCopyId')();

    expect(fileCopiesClient.deleteFileCopy).toHaveBeenCalledWith('someFileCopyId');
  });

  it('should log error when file copy could not be deleted', fakeAsync(() => {
    const sourceFileId = 'someSourceFileId';
    const mockError = new Error('Backup error');
    fileCopiesClient.deleteFileCopy.and.returnValue(throwError(() => mockError));

    component.deleteFileCopy(sourceFileId);
    tick();

    expect(notificationService.showFailure).toHaveBeenCalledWith(
      `An error occurred while trying to delete a file copy`, sourceFileId, mockError);
  }));

  it('should update file copy status when status changed event is received', fakeAsync(() => {
    const gameWithFileCopies: GameWithFileCopies = TestGameWithFileCopies.withTrackedFileCopy();
    changeFirstFileCopyBackupTarget(gameWithFileCopies, localFolderBackupTarget.id);
    mockGameWithFileCopiesExists(gameWithFileCopies);

    fixture.detectChanges();
    tick();

    simulateStatusChangedEventReceivedToInProgressForFirstFileCopy(gameWithFileCopies);

    const sourceFile: SourceFile = gameWithFileCopies.sourceFilesWithCopies[0].sourceFile;
    const backupTargetId: string = localFolderBackupTarget.id;
    const fileCopyInComponent: PotentialFileCopyWithContext =
      component.getPotentialFileCopyWithContext(sourceFile.id, backupTargetId)!;
    const gameList: DebugElement = getGameList();
    expect(fileCopyInComponent.potentialFileCopy.status)
      .toBe(FileCopyStatus.InProgress);
    expect(gameList).toBeTruthy();
    expect(gameList.nativeElement.textContent).toContain(FileCopyStatus.InProgress);
  }));

  function simulateFileCopyStatusChangedEventReceived(
    fileCopyId: string, fileCopyNaturalId: FileCopyNaturalId, newStatus: FileCopyStatus): void {
    const event: FileCopyStatusChangedEvent =
      TestFileCopyStatusChangedEvent.withContent(fileCopyId, fileCopyNaturalId, newStatus);
    emitBackupStatusChanged(event);
    fixture.detectChanges();
  }

  function emitBackupStatusChanged(event: FileCopyStatusChangedEvent) {
    messageSimulator.emit(FileBackupMessageTopics.TopicBackupsStatusChanged, event);
  }

  function getGameList() {
    return fixture.debugElement.query(By.css('[data-testid="game-list"]'));
  }

  function changeFirstFileCopyBackupTarget(gameWithFileCopies: GameWithFileCopies, backupTargetId: string) {
    gameWithFileCopies.sourceFilesWithCopies[0].fileCopiesWithProgress[0].fileCopy.naturalId.backupTargetId =
      backupTargetId;
  }

  function mockGameWithFileCopiesExists(gameWithFileCopies: GameWithFileCopies) {
    const gameWithFileCopiesPage: Page<GameWithFileCopies> = TestPage.of([gameWithFileCopies]);
    gamesClient.getGames.and.returnValue(of(gameWithFileCopiesPage) as any);
  }

  function simulateStatusChangedEventReceivedToInProgressForFirstFileCopy(
    gameWithFileCopies: GameWithFileCopies) {
    const fileCopy = (gameWithFileCopies.sourceFilesWithCopies[0].fileCopiesWithProgress)[0]?.fileCopy;
    simulateFileCopyStatusChangedEventReceived(
      fileCopy.id, fileCopy.naturalId, FileCopyStatus.InProgress);
  }

  it('should remove file update progress when status changed event is received', fakeAsync(() => {
    const gameWithFileCopies: GameWithFileCopies = TestGameWithFileCopies.withInProgressFileCopy();
    changeFirstFileCopyBackupTarget(gameWithFileCopies, localFolderBackupTarget.id);

    const fileCopyWithProgress: FileCopyWithProgress =
      gameWithFileCopies.sourceFilesWithCopies[0].fileCopiesWithProgress[0];
    mockGameWithFileCopiesExists(gameWithFileCopies);

    fixture.detectChanges();
    tick();

    simulateFileCopyStatusChangedEventReceived(
      fileCopyWithProgress.fileCopy.id,
      fileCopyWithProgress.fileCopy.naturalId,
      FileCopyStatus.StoredIntegrityUnknown
    );

    expect(component.getPotentialFileCopyWithContext(
      gameWithFileCopies.sourceFilesWithCopies[0].sourceFile.id, localFolderBackupTarget.id)!.progress).toBeUndefined();
  }));

  it('should handle StatusChanged event when no matching fileCopy exists', fakeAsync(() => {
    const gameWithFileCopies: GameWithFileCopies = TestGameWithFileCopies.withTrackedFileCopy();
    changeFirstFileCopyBackupTarget(gameWithFileCopies, localFolderBackupTarget.id);
    const anotherGameWithFileCopies: GameWithFileCopies = TestGameWithFileCopies.withTrackedFileCopy();
    changeFirstFileCopyBackupTarget(anotherGameWithFileCopies, 'anotherBackupTargetId');
    mockGameWithFileCopiesExists(gameWithFileCopies);

    fixture.detectChanges();
    tick();

    simulateStatusChangedEventReceivedToInProgressForFirstFileCopy(anotherGameWithFileCopies);

    const gameListTable: DebugElement = getGameList();
    expect(gameListTable).toBeTruthy();
    expect(gameListTable.nativeElement.textContent).not.toContain(FileCopyStatus.InProgress);
  }));

  it('should update file copy progress when progress changed event is received' +
    ' and file copy is found in list', fakeAsync(() => {
    const gameWithFileCopies: GameWithFileCopies = TestGameWithFileCopies.withInProgressFileCopy();
    const fileCopy = gameWithFileCopies.sourceFilesWithCopies[0].fileCopiesWithProgress[0].fileCopy;
    fileCopy.naturalId.backupTargetId = localFolderBackupTarget.id;
    const gameWithFileCopiesPage: Page<GameWithFileCopies> = TestPage.of([gameWithFileCopies]);
    gamesClient.getGames.and.returnValue(of(gameWithFileCopiesPage) as any);

    fixture.detectChanges();
    tick();

    const progressChangedEvent: FileCopyReplicationProgressUpdatedEvent =
      TestProgressUpdatedEvent.twentyFivePercent(fileCopy.id, fileCopy.naturalId);
    simulateFileCopyProgressChangedEventReceived(progressChangedEvent);

    const sourceFile: SourceFile = gameWithFileCopies.sourceFilesWithCopies[0].sourceFile;
    expect(component.getPotentialFileCopyWithContext(sourceFile.id, localFolderBackupTarget.id)!.progress)
      .toEqual(TestProgress.twentyFivePercent());
  }));

  it('should not update file copy progress when progress changed event is received' +
    ' and file copy is not found in list',
    fakeAsync(() => {
      const gameWithFileCopies: GameWithFileCopies = TestGameWithFileCopies.withTrackedFileCopy();
      const gameWithFileCopiesPage: Page<GameWithFileCopies> = TestPage.of([gameWithFileCopies]);
      gamesClient.getGames.and.returnValue(of(gameWithFileCopiesPage) as any);

      fixture.detectChanges();
      tick();

      const progressChangedEvent: FileCopyReplicationProgressUpdatedEvent =
        TestProgressUpdatedEvent.twentyFivePercent(
          'unknownFileCopyId', {sourceFileId: 'unknownSourceFileId', backupTargetId: 'unknownBackupTargetId'});
      simulateFileCopyProgressChangedEventReceived(progressChangedEvent);

      const sourceFile: SourceFile = gameWithFileCopies.sourceFilesWithCopies[0].sourceFile;
      expect(component.getPotentialFileCopyWithContext(sourceFile.id, localFolderBackupTarget.id)!.progress)
        .toBeUndefined();
    }));

  function simulateFileCopyProgressChangedEventReceived(
    progressChangedEvent: FileCopyReplicationProgressUpdatedEvent): void {
    emitProgressUpdate(progressChangedEvent);
    fixture.detectChanges();
  }

  function emitProgressUpdate(progressChangedEvent: FileCopyReplicationProgressUpdatedEvent) {
    messageSimulator.emit(FileBackupMessageTopics.TopicBackupsProgressUpdate, progressChangedEvent);
  }

  it('refresh should do nothing if already loading', fakeAsync(() => {
    component.gamesAreLoading.set(true);
    component.gameWithFileCopiesPage.set(TestPage.of([]));

    component.refresh();
    tick();

    expect(gamesClient.getGames).not.toHaveBeenCalled();
  }));
});
