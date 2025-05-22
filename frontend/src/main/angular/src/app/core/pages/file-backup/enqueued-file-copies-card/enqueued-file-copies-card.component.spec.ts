import {ComponentFixture, TestBed} from '@angular/core/testing';

import {EnqueuedFileCopiesCardComponent} from './enqueued-file-copies-card.component';
import {
  FileBackupMessageTopics,
  FileBackupStartedEvent,
  GameFile,
  FileCopyProcessingStatus,
  FileCopiesClient,
  PageFileCopy, FileCopy
} from "@backend";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {of, throwError} from "rxjs";
import {provideRouter} from "@angular/router";
import {MessageTesting} from "@app/shared/testing/message-testing";
import {By} from "@angular/platform-browser";
import {TestGameFile} from "@app/shared/testing/objects/test-game-file";
import {TestFileBackupStartedEvent} from "@app/shared/testing/objects/test-file-backup-started-event";
import {TestPage} from "@app/shared/testing/objects/test-page";
import createSpyObj = jasmine.createSpyObj;
import anything = jasmine.anything;
import SpyObj = jasmine.SpyObj;
import createSpy = jasmine.createSpy;
import {TestFileCopy} from "@app/shared/testing/objects/test-file-copy";

describe('EnqueuedFileCopiesCardComponent', () => {
  let component: EnqueuedFileCopiesCardComponent;
  let fixture: ComponentFixture<EnqueuedFileCopiesCardComponent>;
  let fileCopiesClient: SpyObj<FileCopiesClient>;
  let messagesService: SpyObj<MessagesService>;
  let notificationService: NotificationService;

  const aGameFile: GameFile = TestGameFile.any();
  const enqueuedFileCopy: FileCopy = TestFileCopy.enqueued();
  const initialEnqueuedDownloads: PageFileCopy = TestPage.of([enqueuedFileCopy]);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EnqueuedFileCopiesCardComponent],
      providers: [
        {
          provide: FileCopiesClient,
          useValue: createSpyObj('FileCopiesClient', ['getFileCopiesWithStatus'])
        },
        {
          provide: MessagesService,
          useValue: createSpyObj('MessagesService', ["watch"])
        },
        {
          provide: NotificationService,
          useValue: createSpyObj('NotificationService', ['showSuccess', 'showFailure'])
        },
        provideRouter([])
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(EnqueuedFileCopiesCardComponent);
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
      FileBackupMessageTopics.Started
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
    expectEnqueuedGameTitleToContain(enqueuedFileCopy.naturalId.gameFileId);
  });

  function expectEnqueuedGameTitleToContain(expectedGameTitle: string) {
    expectGameTitleIn('#download-queue', expectedGameTitle);
  }

  function expectGameTitleIn(selector: string, title: string) {
    const table = fixture.debugElement.query(By.css(selector));
    expect(table.nativeElement.textContent).toContain(title);
  }

  it('should log an error when removeFromQueue is called', async () => {
    await component.removeFromQueue();
    expect(notificationService.showFailure).toHaveBeenCalledWith('Removing from queue not yet implemented');
  });

  it('should update currently downloaded game', async () => {
    const fileBackupStartedEvent: FileBackupStartedEvent = TestFileBackupStartedEvent.for(aGameFile, enqueuedFileCopy);

    await MessageTesting.simulateWebSocketMessageReceived(fixture, messagesService,
      FileBackupMessageTopics.Started, fileBackupStartedEvent);

    expectDownloadQueueToBeEmpty();
  });

  function expectDownloadQueueToBeEmpty() {
    const table = fixture.debugElement.query(By.css('#download-queue'));
    expect(table.nativeElement.textContent).toContain('No data');
  }
});
