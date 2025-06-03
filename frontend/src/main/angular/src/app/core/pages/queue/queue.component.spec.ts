import {ComponentFixture, TestBed} from '@angular/core/testing';

import {QueueComponent} from './queue.component';
import {
  FileBackupMessageTopics,
  FileCopiesClient,
  FileCopy,
  FileCopyNaturalId,
  FileCopyProcessingStatus,
  FileCopyStatus,
  FileCopyStatusChangedEvent,
  PageFileCopy
} from "@backend";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {of, throwError} from "rxjs";
import {provideRouter} from "@angular/router";
import {MessageTesting} from "@app/shared/testing/message-testing";
import {By} from "@angular/platform-browser";
import {TestPage} from "@app/shared/testing/objects/test-page";
import {TestFileCopy} from "@app/shared/testing/objects/test-file-copy";
import {TestFileCopyStatusChangedEvent} from "@app/shared/testing/objects/test-file-copy-status-changed-event";
import createSpyObj = jasmine.createSpyObj;
import anything = jasmine.anything;
import SpyObj = jasmine.SpyObj;
import createSpy = jasmine.createSpy;

describe('QueueComponent', () => {
  let component: QueueComponent;
  let fixture: ComponentFixture<QueueComponent>;

  let fileCopiesClient: SpyObj<FileCopiesClient>;
  let messagesService: SpyObj<MessagesService>;
  let notificationService: NotificationService;

  const enqueuedFileCopy: FileCopy = TestFileCopy.enqueued();
  const initialEnqueuedDownloads: PageFileCopy = TestPage.of([enqueuedFileCopy]);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [QueueComponent],
      providers: [
        {
          provide: FileCopiesClient,
          useValue: createSpyObj('FileCopiesClient', ['getFileCopiesWithStatus'])
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
      .compileComponents();

    fixture = TestBed.createComponent(QueueComponent);
    component = fixture.componentInstance;

    fileCopiesClient = TestBed.inject(FileCopiesClient) as SpyObj<FileCopiesClient>;
    messagesService = TestBed.inject(MessagesService) as SpyObj<MessagesService>;
    notificationService = TestBed.inject(NotificationService);

    fileCopiesClient.getFileCopiesWithStatus.withArgs(FileCopyProcessingStatus.Enqueued, anything())
      .and.returnValue(of(JSON.parse(JSON.stringify(initialEnqueuedDownloads))) as any);

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
      FileBackupMessageTopics.StatusChanged
    ]);
    expect(component['subscriptions'].length).toBe(1);
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

  it('should show failure notification given error when refreshEnqueuedFiles is called',
    async () => {
      const mockError = new Error('test error');
      fileCopiesClient.getFileCopiesWithStatus.withArgs(FileCopyProcessingStatus.Enqueued, anything())
        .and.returnValue(throwError(() => mockError));

      await component.refreshEnqueuedFileCopies();

      expect(notificationService.showFailure).toHaveBeenCalledWith(
        'Error fetching enqueued files', mockError);
      expect(component.fileCopiesAreLoading).toBeFalse();
    });

  it('should retrieve files on init', async () => {
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(fileCopiesClient.getFileCopiesWithStatus).toHaveBeenCalledWith(FileCopyProcessingStatus.Enqueued, {
      page: 0,
      size: component.pageSize
    });
    expect(component.fileCopiesAreLoading).toBe(false);
    assertQueueContains(enqueuedFileCopy.naturalId.gameFileId);
  });

  function assertQueueContains(expectedValue: string) {
    const queue = fixture.debugElement.query(By.css('#download-queue'));
    expect(queue.nativeElement.textContent).toContain(expectedValue);
  }

  it('should log an error when removeFromQueue is called', async () => {
    await component.removeFromQueue();
    expect(notificationService.showFailure).toHaveBeenCalledWith('Removing from queue not yet implemented');
  });

  it('should remove file copy from queue when status changed event is received', async () => {
    component.fileCopyPage = TestPage.of([enqueuedFileCopy]);
    await simulateFileCopyStatusChangedEventReceived(
      enqueuedFileCopy.id, enqueuedFileCopy.naturalId, FileCopyStatus.InProgress);

    assertQueueDoesNotContain(enqueuedFileCopy.naturalId.gameFileId);
  });

  async function simulateFileCopyStatusChangedEventReceived(
    fileCopyId: string, fileCopyNaturalId: FileCopyNaturalId, newStatus: FileCopyStatus): Promise<void> {
    const statusChangedMessage: FileCopyStatusChangedEvent =
      TestFileCopyStatusChangedEvent.with(fileCopyId, fileCopyNaturalId, newStatus);
    await MessageTesting.simulateWebSocketMessageReceived(fixture, messagesService,
      FileBackupMessageTopics.StatusChanged, statusChangedMessage);
  }

  function assertQueueDoesNotContain(expectedValue: string) {
    const queue = fixture.debugElement.query(By.css('#download-queue'));
    expect(queue.nativeElement.textContent).not.toContain(expectedValue);
  }

  it('should not remove file copy from queue when status changed event is received but file copy not found',
    async () => {
      component.fileCopyPage = TestPage.of([enqueuedFileCopy]);
      const notFoundFileCopy = TestFileCopy.inProgress();
      notFoundFileCopy.id = 'notFoundFileCopyId';
      notFoundFileCopy.naturalId.gameFileId = 'notFoundFileCopyId';

      await simulateFileCopyStatusChangedEventReceived(
        notFoundFileCopy.id, notFoundFileCopy.naturalId, FileCopyStatus.InProgress);

      assertQueueContains(enqueuedFileCopy.naturalId.gameFileId);
    });
});
