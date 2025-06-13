import {ComponentFixture, TestBed} from '@angular/core/testing';

import {QueueComponent} from './queue.component';
import {
  FileBackupMessageTopics,
  FileCopiesClient,
  FileCopy,
  FileCopyNaturalId,
  FileCopyStatus,
  FileCopyStatusChangedEvent,
  FileCopyWithContext,
  FileDownloadProgressUpdatedEvent,
  PageFileCopyWithContext,
  StorageSolutionsClient,
  StorageSolutionStatus,
  StorageSolutionStatusesResponse
} from "@backend";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {of, throwError} from "rxjs";
import {provideRouter} from "@angular/router";
import {MessageTesting} from "@app/shared/testing/message-testing";
import {TestPage} from "@app/shared/testing/objects/test-page";
import {TestFileCopy} from "@app/shared/testing/objects/test-file-copy";
import {TestFileCopyStatusChangedEvent} from "@app/shared/testing/objects/test-file-copy-status-changed-event";
import {TestFileCopyWithContext} from '@app/shared/testing/objects/test-file-copy-with-context';
import {TestProgressUpdatedEvent} from "@app/shared/testing/objects/test-progress-updated-event";
import {deepClone} from "@app/shared/testing/deep-clone";
import {AutoLayoutComponent} from "@app/shared/components/auto-layout/auto-layout.component";
import {AutoLayoutStubComponent} from "@app/shared/components/auto-layout/auto-layout.stub.component";
import createSpyObj = jasmine.createSpyObj;
import anything = jasmine.anything;
import SpyObj = jasmine.SpyObj;
import createSpy = jasmine.createSpy;

describe('QueueComponent', () => {
  let component: QueueComponent;
  let fixture: ComponentFixture<QueueComponent>;

  let fileCopiesClient: SpyObj<FileCopiesClient>;
  let storageSolutionsClient: SpyObj<StorageSolutionsClient>;
  let messagesService: SpyObj<MessagesService>;
  let notificationService: NotificationService;

  let initialEnqueuedFileCopy: FileCopy = TestFileCopy.enqueued();
  let initialFileCopyWithContext: FileCopyWithContext = TestFileCopyWithContext.withFileCopy(initialEnqueuedFileCopy);
  let initialQueue: PageFileCopyWithContext = TestPage.of([initialFileCopyWithContext]);
  let initialStorageSolutionStatusResponse: StorageSolutionStatusesResponse;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [QueueComponent],
      providers: [
        {
          provide: FileCopiesClient,
          useValue: createSpyObj('FileCopiesClient', ['getFileCopyQueue'])
        },
        {
          provide: StorageSolutionsClient,
          useValue: createSpyObj('StorageSolutionsClient', ['getStorageSolutionStatuses'])
        },
        {
          provide: MessagesService,
          useValue: createSpyObj('MessagesService', ['watch'])
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

    fixture = TestBed.createComponent(QueueComponent);
    component = fixture.componentInstance;

    fileCopiesClient = TestBed.inject(FileCopiesClient) as SpyObj<FileCopiesClient>;
    storageSolutionsClient = TestBed.inject(StorageSolutionsClient) as SpyObj<StorageSolutionsClient>;
    messagesService = TestBed.inject(MessagesService) as SpyObj<MessagesService>;
    notificationService = TestBed.inject(NotificationService);

    initialEnqueuedFileCopy = TestFileCopy.enqueued();
    initialFileCopyWithContext = TestFileCopyWithContext.withFileCopy(initialEnqueuedFileCopy);
    initialQueue = TestPage.of([initialFileCopyWithContext]);
    initialStorageSolutionStatusResponse = {
      statuses: {
        "someStorageSolutionId": "CONNECTED"
      }
    };

    MessageTesting.mockWatch(messagesService, (destination, callback) => {
      // Do nothing
    });
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should subscribe to message topics on initialization', () => {
    const topicsSubscribed: string[] = [];
    MessageTesting.mockWatch(messagesService, (destination, callback) =>
      topicsSubscribed.push(destination));

    component.ngOnInit();

    expect(topicsSubscribed).toEqual([
      FileBackupMessageTopics.StatusChanged,
      FileBackupMessageTopics.ProgressUpdate
    ]);
    expect(component['subscriptions'].length).toBe(2);
  });

  it('should unsubscribe from message topics on destruction', () => {
    const subscription = aMockSubscription();
    component['subscriptions'].push(subscription);

    component.ngOnDestroy();

    expect(subscription.unsubscribe).toHaveBeenCalled();
  });

  function aMockSubscription() {
    return ({unsubscribe: createSpy()}) as any;
  }

  it('should show failure notification given error when refresh is called',
    async () => {
      const mockError = new Error('test error');
      fileCopiesClient.getFileCopyQueue.withArgs(anything())
        .and.returnValue(throwError(() => mockError));

      await component.refresh();

      expect(notificationService.showFailure).toHaveBeenCalledWith(
        'Error fetching enqueued files', mockError);
      expect(component.fileCopiesAreLoading).toBeFalse();
    });

  it('should retrieve files on init', async () => {
    fileCopiesClient.getFileCopyQueue.withArgs(anything())
      .and.returnValue(of(deepClone(initialQueue)) as any);
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(fileCopiesClient.getFileCopyQueue).toHaveBeenCalledWith({
      page: 0,
      size: component.pageSize
    });
    expect(component.fileCopiesAreLoading).toBe(false);
    assertQueueContains(initialFileCopyWithContext);
  });

  function assertQueueContains(fileCopyWithContext: FileCopyWithContext) {
    expect(fixture.nativeElement.textContent).toContain(fileCopyWithContext.gameFile.fileSource.fileTitle);
  }

  it('should retrieve storage solution statuses on init', async () => {
    fileCopiesClient.getFileCopyQueue.withArgs(anything())
      .and.returnValue(of(deepClone(initialQueue) as any));
    storageSolutionsClient.getStorageSolutionStatuses
      .and.returnValue(of(deepClone(initialStorageSolutionStatusResponse) as any));
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(storageSolutionsClient.getStorageSolutionStatuses).toHaveBeenCalled();
    expect(component.fileCopiesAreLoading).toBe(false);
    expect(fixture.nativeElement.textContent).toContain("CONNECTED");
  });

  it('should get storage solution status', async () => {
    component.storageSolutionStatusesById = new Map<string, StorageSolutionStatus>();
    component.storageSolutionStatusesById.set("someStorageSolutionId", StorageSolutionStatus.Connected);

    const result: StorageSolutionStatus | undefined =
      component.getStorageSolutionStatus("someStorageSolutionId");

    expect(result).toEqual(StorageSolutionStatus.Connected);
  });

  it('should log an error when removeFromQueue is called', async () => {
    await component.removeFromQueue();
    expect(notificationService.showFailure).toHaveBeenCalledWith('Removing from queue not yet implemented');
  });

  it('should remove file copy from queue when status changed event is received' +
    ' and new status not enqueued or in progress', async () => {
    component.fileCopyWithContextPage = TestPage.of([initialFileCopyWithContext]);
    await simulateFileCopyStatusChangedEventReceived(
      initialEnqueuedFileCopy.id, initialEnqueuedFileCopy.naturalId, FileCopyStatus.StoredIntegrityVerified);

    assertQueueDoesNotContain(initialFileCopyWithContext);
  });

  it('should not remove file copy from queue when status changed event is received' +
    ' and new status is enqueued', async () => {
    component.fileCopyWithContextPage = TestPage.of([initialFileCopyWithContext]);
    await simulateFileCopyStatusChangedEventReceived(
      initialEnqueuedFileCopy.id, initialEnqueuedFileCopy.naturalId, FileCopyStatus.Enqueued);

    assertQueueContains(initialFileCopyWithContext);
  });

  it('should not remove file copy from queue when status changed event is received' +
    ' and new status is in progress', async () => {
    component.fileCopyWithContextPage = TestPage.of([initialFileCopyWithContext]);
    await simulateFileCopyStatusChangedEventReceived(
      initialEnqueuedFileCopy.id, initialEnqueuedFileCopy.naturalId, FileCopyStatus.InProgress);

    assertQueueContains(initialFileCopyWithContext);
  });

  it('should update file copy status and progress in queue when status changed event is received' +
    ' and new status is in progress and current progress is undefined', async () => {
    component.fileCopyWithContextPage = TestPage.of([initialFileCopyWithContext]);
    initialFileCopyWithContext.progress = undefined;
    await simulateFileCopyStatusChangedEventReceived(
      initialEnqueuedFileCopy.id, initialEnqueuedFileCopy.naturalId, FileCopyStatus.InProgress);

    assertQueueContains(initialFileCopyWithContext);
    expect(firstFileCopyWithContextInQueue()?.fileCopy.status).toEqual(FileCopyStatus.InProgress);
    expect(firstFileCopyWithContextInQueue()?.progress?.percentage).toEqual(0);
  });

  it('should not update file copy progress in queue when status changed event is received' +
    ' and new status is in progress and current progress exists', async () => {
    initialFileCopyWithContext.progress = {
      percentage: 50,
      timeLeftSeconds: 99
    }
    component.fileCopyWithContextPage = TestPage.of([initialFileCopyWithContext]);
    await simulateFileCopyStatusChangedEventReceived(
      initialEnqueuedFileCopy.id, initialEnqueuedFileCopy.naturalId, FileCopyStatus.InProgress);

    assertQueueContains(initialFileCopyWithContext);
    expect(firstFileCopyWithContextInQueue()?.fileCopy.status).toEqual(FileCopyStatus.InProgress);
    expect(firstFileCopyWithContextInQueue()?.progress?.percentage).toEqual(50);
  });

  function firstFileCopyWithContextInQueue(): FileCopyWithContext | undefined {
    return component.fileCopyWithContextPage?.content?.[0];
  }

  async function simulateFileCopyStatusChangedEventReceived(
    fileCopyId: string, fileCopyNaturalId: FileCopyNaturalId, newStatus: FileCopyStatus): Promise<void> {
    const statusChangedMessage: FileCopyStatusChangedEvent =
      TestFileCopyStatusChangedEvent.withContent(fileCopyId, fileCopyNaturalId, newStatus);
    await MessageTesting.simulateWebSocketMessageReceived(fixture, messagesService,
      FileBackupMessageTopics.StatusChanged, statusChangedMessage);
  }

  function assertQueueDoesNotContain(fileCopyWithContext: FileCopyWithContext) {
    expect(fixture.nativeElement.textContent).not.toContain(fileCopyWithContext.gameFile.fileSource.fileTitle);
  }

  it('should not remove file copy from queue when status changed event is received but file copy not found',
    async () => {
      component.fileCopyWithContextPage = TestPage.of([initialFileCopyWithContext]);
      const notFoundFileCopy = TestFileCopy.inProgress();
      notFoundFileCopy.id = 'notFoundFileCopyId';
      notFoundFileCopy.naturalId.gameFileId = 'notFoundFileCopyId';

      await simulateFileCopyStatusChangedEventReceived(
        notFoundFileCopy.id, notFoundFileCopy.naturalId, FileCopyStatus.InProgress);

      assertQueueContains(initialFileCopyWithContext);
    });

  it('should update file copy progress in queue when progress update event is received' +
    ' given file copy found',
    async () => {
      component.fileCopyWithContextPage = TestPage.of([initialFileCopyWithContext]);
      await simulateReplicationProgressUpdateEventReceived(
        initialEnqueuedFileCopy.id, initialEnqueuedFileCopy.naturalId);

      expect(fixture.nativeElement.textContent).toContain('25%');
    });

  async function simulateReplicationProgressUpdateEventReceived(
    fileCopyId: string, fileCopyNaturalId: FileCopyNaturalId): Promise<void> {
    const event: FileDownloadProgressUpdatedEvent =
      TestProgressUpdatedEvent.twentyFivePercent(fileCopyId, fileCopyNaturalId);
    await MessageTesting.simulateWebSocketMessageReceived(fixture, messagesService,
      FileBackupMessageTopics.ProgressUpdate, event);
  }

  it('should not update file copy progress in queue when progress update event is received' +
    ' given file copy not found',
    async () => {
      component.fileCopyWithContextPage = TestPage.of([initialFileCopyWithContext]);
      await simulateReplicationProgressUpdateEventReceived('unknownFileCopyId',
        {gameFileId: 'unknownGameFileId', backupTargetId: 'unknownBackupTargetId'});

      expect(fixture.nativeElement.textContent).not.toContain('25%');
    });
});
