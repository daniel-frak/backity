import {ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';

import {QueueComponent} from './queue.component';
import {
  FileBackupMessageTopics,
  FileCopiesClient,
  FileCopy,
  FileCopyNaturalId,
  FileCopyReplicationProgressUpdatedEvent,
  FileCopyStatus,
  FileCopyStatusChangedEvent,
  FileCopyWithContext,
  StorageSolutionsClient,
  StorageSolutionStatus,
  StorageSolutionStatusesResponse
} from "@backend";
import {MessageService} from "@app/shared/backend/services/message.service";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {of, throwError} from "rxjs";
import {MessageSimulator} from "@app/shared/testing/message-simulator";
import {provideRouter} from "@angular/router";
import {TestPage} from "@app/shared/testing/objects/test-page";
import {TestFileCopy} from "@app/shared/testing/objects/test-file-copy";
import {TestFileCopyStatusChangedEvent} from "@app/shared/testing/objects/test-file-copy-status-changed-event";
import {TestFileCopyWithContext} from '@app/shared/testing/objects/test-file-copy-with-context';
import {TestProgressUpdatedEvent} from "@app/shared/testing/objects/test-progress-updated-event";
import {deepClone} from "@app/shared/testing/deep-clone";
import {AutoLayoutComponent} from "@app/shared/components/auto-layout/auto-layout.component";
import {AutoLayoutStubComponent} from "@app/shared/components/auto-layout/auto-layout.stub.component";
import {Page} from "@app/shared/components/table/page";
import createSpyObj = jasmine.createSpyObj;
import anything = jasmine.anything;
import SpyObj = jasmine.SpyObj;

describe('QueueComponent', () => {
  let component: QueueComponent;
  let fixture: ComponentFixture<QueueComponent>;

  let fileCopiesClient: SpyObj<FileCopiesClient>;
  let storageSolutionsClient: SpyObj<StorageSolutionsClient>;
  let messageService: SpyObj<MessageService>;
  let notificationService: NotificationService;

  let initialEnqueuedFileCopy: FileCopy = TestFileCopy.enqueued();
  let initialFileCopyWithContext: FileCopyWithContext = TestFileCopyWithContext.withFileCopy(initialEnqueuedFileCopy);
  let initialQueue: Page<FileCopyWithContext> = TestPage.of([initialFileCopyWithContext]);
  let initialStorageSolutionStatusResponse: StorageSolutionStatusesResponse;

  let messageSimulator: MessageSimulator;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [QueueComponent],
      providers: [
        {
          provide: FileCopiesClient,
          useValue: createSpyObj('FileCopiesClient', ['getFileCopyQueue', 'cancelFileCopy'])
        },
        {
          provide: StorageSolutionsClient,
          useValue: createSpyObj('StorageSolutionsClient', ['getStorageSolutionStatuses'])
        },
        {
          provide: MessageService,
          useValue: createSpyObj('MessageService', ['watch'])
        },
        {
          provide: NotificationService,
          useValue: createSpyObj('NotificationService', ['showSuccess', 'showFailure'])
        },
        provideRouter([])
      ]
    })
      .overrideComponent(QueueComponent, {
        remove: {imports: [AutoLayoutComponent]},
        add: {imports: [AutoLayoutStubComponent]}
      })
      .compileComponents();

    fileCopiesClient = TestBed.inject(FileCopiesClient) as SpyObj<FileCopiesClient>;
    storageSolutionsClient = TestBed.inject(StorageSolutionsClient) as SpyObj<StorageSolutionsClient>;
    messageService = TestBed.inject(MessageService) as SpyObj<MessageService>;
    notificationService = TestBed.inject(NotificationService);

    messageSimulator = MessageSimulator.given(messageService);

    fileCopiesClient.getFileCopyQueue.and.returnValue(of({content: []}) as any);
    storageSolutionsClient.getStorageSolutionStatuses.and.returnValue(of({statuses: {}}) as any);

    initialEnqueuedFileCopy = TestFileCopy.enqueued();
    initialFileCopyWithContext = TestFileCopyWithContext.withFileCopy(initialEnqueuedFileCopy);
    initialQueue = TestPage.of([initialFileCopyWithContext]);
    initialStorageSolutionStatusResponse = {
      statuses: {
        "someStorageSolutionId": "CONNECTED"
      }
    };

    fixture = TestBed.createComponent(QueueComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should subscribe to message topics on initialization', () => {
    fixture.detectChanges();

    expect(messageService.watch).toHaveBeenCalledWith(FileBackupMessageTopics.TopicBackupsStatusChanged);
    expect(messageService.watch).toHaveBeenCalledWith(FileBackupMessageTopics.TopicBackupsProgressUpdate);
  });

  it('should show failure notification given error when refresh is called', fakeAsync(() => {
    const mockError = new Error('test error');
    fileCopiesClient.getFileCopyQueue.withArgs(anything())
      .and.returnValue(throwError(() => mockError));

    fixture.detectChanges(); // triggers ngOnInit → refresh
    tick();

    expect(notificationService.showFailure).toHaveBeenCalledWith(
      'Error fetching enqueued files', mockError);
    expect(component.fileCopiesAreLoading()).toBeFalse();
  }));

  it('refresh should do nothing given file copies are still loading', fakeAsync(() => {
    fixture.detectChanges();
    component.fileCopiesAreLoading.set(true);
    component.fileCopyWithContextPage.set(TestPage.of([]));

    component.refresh();
    tick();

    expect(fileCopiesClient.getFileCopyQueue).toHaveBeenCalledTimes(1); // Only the one from init
    expect(storageSolutionsClient.getStorageSolutionStatuses).toHaveBeenCalledTimes(1);
  }))

  it('should reload files and storage solution statuses when refresh is called', fakeAsync(() => {
    fileCopiesClient.getFileCopyQueue.withArgs(anything())
      .and.returnValue(of(deepClone(initialQueue) as any));
    storageSolutionsClient.getStorageSolutionStatuses
      .and.returnValue(of(deepClone(initialStorageSolutionStatusResponse) as any));

    fixture.detectChanges(); // ngOnInit -> refresh
    tick();

    expect(fileCopiesClient.getFileCopyQueue).toHaveBeenCalledWith({
      page: 0,
      size: component.pageSize()
    });
    expect(storageSolutionsClient.getStorageSolutionStatuses).toHaveBeenCalled();
    expect(component.fileCopiesAreLoading()).toBeFalse();
    assertQueueContains(initialFileCopyWithContext);
    expect(fixture.nativeElement.textContent).toContain("CONNECTED");
  }))

  function assertQueueContains(fileCopyWithContext: FileCopyWithContext) {
    expect(fixture.nativeElement.textContent).toContain(fileCopyWithContext.gameFile.fileTitle);
  }

  it('should retrieve file copies and storage solution statuses on init', fakeAsync(() => {
    fileCopiesClient.getFileCopyQueue.withArgs(anything())
      .and.returnValue(of(deepClone(initialQueue) as any));
    storageSolutionsClient.getStorageSolutionStatuses
      .and.returnValue(of(deepClone(initialStorageSolutionStatusResponse) as any));

    fixture.detectChanges(); // triggers ngOnInit
    tick();

    expect(fileCopiesClient.getFileCopyQueue).toHaveBeenCalled()
    expect(storageSolutionsClient.getStorageSolutionStatuses).toHaveBeenCalled();
    expect(component.fileCopiesAreLoading()).toBe(false);
  }));

  it('should get storage solution status', async () => {
    component.storageSolutionStatusesById.set(new Map([["someStorageSolutionId", StorageSolutionStatus.Connected]]));

    const result: StorageSolutionStatus | undefined =
      component.getStorageSolutionStatus("someStorageSolutionId");

    expect(result).toEqual(StorageSolutionStatus.Connected);
  });

  it('should remove file copy from queue when status changed event is received' +
    ' and new status not enqueued or in progress', fakeAsync(() => {
    fixture.detectChanges();
    tick();

    component.fileCopyWithContextPage.set(TestPage.of([initialFileCopyWithContext]));
    simulateFileCopyStatusChangedEventReceived(
      initialEnqueuedFileCopy.id, initialEnqueuedFileCopy.naturalId, FileCopyStatus.StoredIntegrityVerified);

    assertQueueDoesNotContain(initialFileCopyWithContext);
  }));

  it('should not remove file copy from queue when status changed event is received' +
    ' and new status is enqueued', fakeAsync(() => {
    fixture.detectChanges();
    tick();

    component.fileCopyWithContextPage.set(TestPage.of([initialFileCopyWithContext]));
    simulateFileCopyStatusChangedEventReceived(
      initialEnqueuedFileCopy.id, initialEnqueuedFileCopy.naturalId, FileCopyStatus.Enqueued);

    assertQueueContains(initialFileCopyWithContext);
  }));

  it('should not remove file copy from queue when status changed event is received' +
    ' and new status is in progress', fakeAsync(() => {
    fixture.detectChanges();
    tick();

    component.fileCopyWithContextPage.set(TestPage.of([initialFileCopyWithContext]));
    simulateFileCopyStatusChangedEventReceived(
      initialEnqueuedFileCopy.id, initialEnqueuedFileCopy.naturalId, FileCopyStatus.InProgress);

    assertQueueContains(initialFileCopyWithContext);
  }));

  it('should not remove file copy from queue when status changed event is received and page is undefined',
    () => {
      component.fileCopyWithContextPage.set(undefined);

      emitFileCopyStatusChanged(TestFileCopyStatusChangedEvent.withContent(
        'any', {gameFileId: 'any', backupTargetId: 'any'}, FileCopyStatus.StoredIntegrityVerified));

      expect(component.fileCopyWithContextPage()).toBeUndefined();
    });

  it('should not remove file copy from queue when status changed event is received and item' +
    ' is not found in content', () => {
    const item = TestFileCopyWithContext.withFileCopy(TestFileCopy.enqueued());
    item.fileCopy.id = 'notFoundId';
    component.fileCopyWithContextPage.set(initialQueue);

    emitFileCopyStatusChanged(TestFileCopyStatusChangedEvent.withContent(
      item.fileCopy.id, item.fileCopy.naturalId, FileCopyStatus.StoredIntegrityVerified));

    expect(component.fileCopyWithContextPage()).toBe(initialQueue);
  });

  it('should update file copy status and progress in queue when status changed event is received' +
    ' and new status is in progress and current progress is undefined', fakeAsync(() => {
    fixture.detectChanges();
    tick();

    initialFileCopyWithContext.progress = undefined;
    component.fileCopyWithContextPage.set(TestPage.of([initialFileCopyWithContext]));
    simulateFileCopyStatusChangedEventReceived(
      initialEnqueuedFileCopy.id, initialEnqueuedFileCopy.naturalId, FileCopyStatus.InProgress);

    assertQueueContains(initialFileCopyWithContext);
    expect(firstFileCopyWithContextInQueue()?.fileCopy.status).toEqual(FileCopyStatus.InProgress);
    expect(firstFileCopyWithContextInQueue()?.progress?.percentage).toEqual(0);
  }));

  it('should not update file copy progress in queue when status changed event is received' +
    ' and new status is in progress and current progress exists', fakeAsync(() => {
    fixture.detectChanges();
    tick();

    initialFileCopyWithContext.progress = {
      percentage: 50,
      timeLeftSeconds: 99
    }
    component.fileCopyWithContextPage.set(TestPage.of([initialFileCopyWithContext]));
    simulateFileCopyStatusChangedEventReceived(
      initialEnqueuedFileCopy.id, initialEnqueuedFileCopy.naturalId, FileCopyStatus.InProgress);

    assertQueueContains(initialFileCopyWithContext);
    expect(firstFileCopyWithContextInQueue()?.fileCopy.status).toEqual(FileCopyStatus.InProgress);
    expect(firstFileCopyWithContextInQueue()?.progress?.percentage).toEqual(50);
  }));

  function firstFileCopyWithContextInQueue(): FileCopyWithContext | undefined {
    return component.fileCopyWithContextPage()?.content?.[0];
  }

  function simulateFileCopyStatusChangedEventReceived(
    fileCopyId: string, fileCopyNaturalId: FileCopyNaturalId, newStatus: FileCopyStatus): void {
    const event: FileCopyStatusChangedEvent =
      TestFileCopyStatusChangedEvent.withContent(fileCopyId, fileCopyNaturalId, newStatus);

    emitFileCopyStatusChanged(event);
    fixture.detectChanges();
  }

  function emitFileCopyStatusChanged(event: FileCopyStatusChangedEvent) {
    messageSimulator.emit(FileBackupMessageTopics.TopicBackupsStatusChanged, event);
  }

  function assertQueueDoesNotContain(fileCopyWithContext: FileCopyWithContext) {
    expect(fixture.nativeElement.textContent).not.toContain(fileCopyWithContext.gameFile.fileTitle);
  }

  it('should not remove file copy from queue when status changed event is received but file copy not found',
    fakeAsync(() => {
      fixture.detectChanges();
      tick();

      component.fileCopyWithContextPage.set(initialQueue);
      const notFoundFileCopy = TestFileCopy.inProgress();
      notFoundFileCopy.id = 'notFoundFileCopyId';
      notFoundFileCopy.naturalId.gameFileId = 'notFoundFileCopyId';

      simulateFileCopyStatusChangedEventReceived(
        notFoundFileCopy.id, notFoundFileCopy.naturalId, FileCopyStatus.InProgress);

      expect(component.fileCopyWithContextPage()).toBe(initialQueue);
      assertQueueContains(initialFileCopyWithContext);
    }));

  it('should update file copy progress in queue when progress update event is received' +
    ' given file copy found',
    fakeAsync(() => {
      fixture.detectChanges();
      tick();

      component.fileCopyWithContextPage.set(TestPage.of([initialFileCopyWithContext]));
      simulateReplicationProgressUpdateEventReceived(
        initialEnqueuedFileCopy.id, initialEnqueuedFileCopy.naturalId);

      expect(fixture.nativeElement.textContent).toContain('25%');
    }));

  function simulateReplicationProgressUpdateEventReceived(
    fileCopyId: string, fileCopyNaturalId: FileCopyNaturalId): void {
    const event: FileCopyReplicationProgressUpdatedEvent =
      TestProgressUpdatedEvent.twentyFivePercent(fileCopyId, fileCopyNaturalId);
    emitProgressUpdated(event);
    fixture.detectChanges();
  }

  function emitProgressUpdated(event: FileCopyReplicationProgressUpdatedEvent) {
    messageSimulator.emit(FileBackupMessageTopics.TopicBackupsProgressUpdate, event);
  }

  it('should not update file copy progress in queue when progress update event is received' +
    ' given file copy not found',
    fakeAsync(() => {
      fixture.detectChanges();
      tick();

      component.fileCopyWithContextPage.set(initialQueue);
      simulateReplicationProgressUpdateEventReceived('unknownFileCopyId',
        {gameFileId: 'unknownGameFileId', backupTargetId: 'unknownBackupTargetId'});

      expect(component.fileCopyWithContextPage()).toBe(initialQueue);
      expect(fixture.nativeElement.textContent).not.toContain('25%');
    }));

  it('should not update file copy progress in queue when progress update event is received' +
    ' given page is undefined',
    fakeAsync(() => {
      fixture.detectChanges();
      tick();

      component.fileCopyWithContextPage.set(undefined);
      simulateReplicationProgressUpdateEventReceived('unknownFileCopyId',
        {gameFileId: 'unknownGameFileId', backupTargetId: 'unknownBackupTargetId'});

      expect(component.fileCopyWithContextPage()).toBe(undefined);
    }));

  it('cancelBackup should cancel backup and refresh', fakeAsync(() => {
    const fileCopyWithContext: FileCopyWithContext = TestFileCopyWithContext.withFileCopy(TestFileCopy.enqueued());
    fileCopiesClient.cancelFileCopy.and.returnValue(of(null) as any);
    fileCopiesClient.getFileCopyQueue.and.returnValue(of(initialQueue) as any);
    storageSolutionsClient.getStorageSolutionStatuses.and.returnValue(of(initialStorageSolutionStatusResponse) as any);

    fixture.detectChanges();
    tick();
    component.cancelBackup(fileCopyWithContext);
    tick();

    expect(fileCopiesClient.cancelFileCopy).toHaveBeenCalledWith(fileCopyWithContext.fileCopy.id);
    expect(notificationService.showSuccess).toHaveBeenCalledWith(`Backup canceled`);
    expect(fileCopiesClient.getFileCopyQueue).toHaveBeenCalled();
  }));

  it('should log error when cancelling file copy backup fails', fakeAsync(() => {
    const fileCopyWithContext = TestFileCopyWithContext.withFileCopy(TestFileCopy.enqueued());
    const mockError = new Error('Backup error');
    fileCopiesClient.cancelFileCopy.and.returnValue(throwError(() => mockError));
    fileCopiesClient.getFileCopyQueue.and.returnValue(of(initialQueue) as any);
    storageSolutionsClient.getStorageSolutionStatuses.and.returnValue(of(initialStorageSolutionStatusResponse) as any);
    fixture.detectChanges();
    tick();

    component.cancelBackup(fileCopyWithContext);
    tick();

    expect(notificationService.showFailure).toHaveBeenCalledWith(
      `An error occurred while trying to cancel the backup`, fileCopyWithContext, mockError);
    expect(fileCopyWithContext.progress).toBeUndefined();
  }));

  it('onClickCancelBackup should return a callable that delegates to cancelBackup',
    async () => {
      const fileCopyWithContext: FileCopyWithContext = TestFileCopyWithContext.withFileCopy(TestFileCopy.enqueued());

      await component.onClickCancelBackup(fileCopyWithContext)();

      expect(fileCopiesClient.cancelFileCopy).toHaveBeenCalledWith(fileCopyWithContext.fileCopy.id);
    });

  it('should not update file copy status in queue when status changed event is received' +
    ' and page is undefined', () => {
    component.fileCopyWithContextPage.set(undefined);

    emitFileCopyStatusChanged(TestFileCopyStatusChangedEvent.withContent(
      'any', {gameFileId: 'any', backupTargetId: 'any'}, FileCopyStatus.InProgress));

    expect(component.fileCopyWithContextPage()).toBeUndefined();
  });

  it('onReplicationProgressChanged should return early if page is undefined', () => {
    component.fileCopyWithContextPage.set(undefined);

    emitProgressUpdated({fileCopyId: 'someId', percentage: 1, timeLeftSeconds: 1} as any);

    expect(component.fileCopyWithContextPage()).toBeUndefined();
  });

  it('should not update file copy status in queue when status changed event is received' +
    ' and item is not found in content', () => {
    const item: FileCopyWithContext = TestFileCopyWithContext.withFileCopy(TestFileCopy.enqueued());
    item.fileCopy.id = 'notFoundId';
    component.fileCopyWithContextPage.set(initialQueue);

    emitFileCopyStatusChanged(TestFileCopyStatusChangedEvent.withContent(
      item.fileCopy.id, item.fileCopy.naturalId, FileCopyStatus.InProgress));

    expect(component.fileCopyWithContextPage()).toBe(initialQueue);
  });

  it('onReplicationProgressChanged should update progress', fakeAsync(() => {
    component.fileCopyWithContextPage.set(TestPage.of([initialFileCopyWithContext]));

    emitProgressUpdated({
      fileCopyId: initialFileCopyWithContext.fileCopy.id,
      percentage: 75,
      timeLeftSeconds: 10
    } as any);

    expect(firstFileCopyWithContextInQueue()?.progress?.percentage).toEqual(75);
  }));

  it('should block refresh when already loading', async () => {
    fileCopiesClient.getFileCopyQueue.calls.reset();
    component.fileCopiesAreLoading.set(true);

    await component.refreshAction();

    expect(fileCopiesClient.getFileCopyQueue).not.toHaveBeenCalled();
  });
});
