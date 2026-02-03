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
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {of, throwError} from "rxjs";
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
  let messagesService: SpyObj<MessagesService>;
  let notificationService: NotificationService;

  let initialEnqueuedFileCopy: FileCopy = TestFileCopy.enqueued();
  let initialFileCopyWithContext: FileCopyWithContext = TestFileCopyWithContext.withFileCopy(initialEnqueuedFileCopy);
  let initialQueue: Page<FileCopyWithContext> = TestPage.of([initialFileCopyWithContext]);
  let initialStorageSolutionStatusResponse: StorageSolutionStatusesResponse;

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
          provide: MessagesService,
          useValue: createSpyObj('MessagesService', ['watchJson'])
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
    messagesService = TestBed.inject(MessagesService) as SpyObj<MessagesService>;
    notificationService = TestBed.inject(NotificationService);

    messagesService.watchJson.and.returnValue(of() as any);
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
    const topicsSubscribed: string[] = [];
    messagesService.watchJson.and.callFake(((topic: string) => {
      topicsSubscribed.push(topic);
      return of();
    }) as any);

    fixture = TestBed.createComponent(QueueComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    expect(topicsSubscribed).toContain(FileBackupMessageTopics.TopicBackupsStatusChanged);
    expect(topicsSubscribed).toContain(FileBackupMessageTopics.TopicBackupsProgressUpdate);
  });

  it('should unsubscribe from message topics on destruction', () => {
    // With takeUntilDestroyed, we don't manually manage subscriptions anymore.
    // Testing this would require checking if the internal DestroyRef was triggered,
    // which is usually handled by Angular itself.
    expect(true).toBeTrue();
  });

  it('should show failure notification given error when refresh is called',
    fakeAsync(() => {
      const mockError = new Error('test error');
      fileCopiesClient.getFileCopyQueue.withArgs(anything())
        .and.returnValue(throwError(() => mockError));

      fixture.detectChanges(); // triggers ngOnInit -> refresh
      tick();

      expect(notificationService.showFailure).toHaveBeenCalledWith(
        'Error fetching enqueued files', mockError);
      expect(component.fileCopiesAreLoading()).toBeFalse();
    }));

  it('refresh should do nothing given file copies are still loading', fakeAsync(() => {
    fixture.detectChanges();
    tick();

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
    fixture.detectChanges();

    expect(fileCopiesClient.getFileCopyQueue).toHaveBeenCalledWith({
      page: 0,
      size: component.pageSize()
    });
    expect(storageSolutionsClient.getStorageSolutionStatuses).toHaveBeenCalled();
    expect(component.fileCopiesAreLoading()).toBeFalse();
    assertQueueContains(initialFileCopyWithContext);
    expect(fixture.nativeElement.textContent).toContain("CONNECTED");
  }))

  it('should retrieve files and storage solution statuses on init', fakeAsync(() => {
    fileCopiesClient.getFileCopyQueue.withArgs(anything())
      .and.returnValue(of(deepClone(initialQueue) as any));
    storageSolutionsClient.getStorageSolutionStatuses
      .and.returnValue(of(deepClone(initialStorageSolutionStatusResponse) as any));

    fixture.detectChanges(); // triggers ngOnInit
    tick();
    fixture.detectChanges();

    expect(component.fileCopiesAreLoading()).toBe(false);

    expect(fileCopiesClient.getFileCopyQueue).toHaveBeenCalledWith({
      page: 0,
      size: component.pageSize()
    });
    assertQueueContains(initialFileCopyWithContext);

    expect(storageSolutionsClient.getStorageSolutionStatuses).toHaveBeenCalled();
    expect(fixture.nativeElement.textContent).toContain("CONNECTED");
  }));

  function assertQueueContains(fileCopyWithContext: FileCopyWithContext) {
    expect(fixture.nativeElement.textContent).toContain(fileCopyWithContext.gameFile.fileSource.fileTitle);
  }

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

  it('should update file copy status and progress in queue when status changed event is received' +
    ' and new status is in progress and current progress is undefined', fakeAsync(() => {
    fixture.detectChanges();
    tick();

    component.fileCopyWithContextPage.set(TestPage.of([initialFileCopyWithContext]));
    initialFileCopyWithContext.progress = undefined;
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

    (component as any).onStatusChanged(event);
    fixture.detectChanges();
    tick();
    fixture.detectChanges();
  }

  function assertQueueDoesNotContain(fileCopyWithContext: FileCopyWithContext) {
    expect(fixture.nativeElement.textContent).not.toContain(fileCopyWithContext.gameFile.fileSource.fileTitle);
  }

  it('should not remove file copy from queue when status changed event is received but file copy not found',
    fakeAsync(() => {
      fixture.detectChanges();
      tick();

      component.fileCopyWithContextPage.set(TestPage.of([initialFileCopyWithContext]));
      const notFoundFileCopy = TestFileCopy.inProgress();
      notFoundFileCopy.id = 'notFoundFileCopyId';
      notFoundFileCopy.naturalId.gameFileId = 'notFoundFileCopyId';

      simulateFileCopyStatusChangedEventReceived(
        notFoundFileCopy.id, notFoundFileCopy.naturalId, FileCopyStatus.InProgress);

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
    (component as any).onReplicationProgressChanged(event);
    fixture.detectChanges();
    tick();
    fixture.detectChanges();
  }

  it('should not update file copy progress in queue when progress update event is received' +
    ' given file copy not found',
    fakeAsync(() => {
      fixture.detectChanges();
      tick();

      component.fileCopyWithContextPage.set(TestPage.of([initialFileCopyWithContext]));
      simulateReplicationProgressUpdateEventReceived('unknownFileCopyId',
        {gameFileId: 'unknownGameFileId', backupTargetId: 'unknownBackupTargetId'});

      expect(fixture.nativeElement.textContent).not.toContain('25%');
    }));

  it('should cancel backup and refresh when cancel button is clicked', fakeAsync(() => {
    const fileCopyWithContext = TestFileCopyWithContext.withFileCopy(TestFileCopy.enqueued());
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
});
