import {ComponentFixture, TestBed} from '@angular/core/testing';

import {InProgressFilesCardComponent} from './in-progress-files-card.component';
import {
  FileBackupMessageTopics,
  FileDownloadProgressUpdatedEvent,
  FileBackupStartedEvent,
  FileBackupStatus,
  FileBackupStatusChangedEvent,
  GameFile,
  GameFilesClient
} from "@backend";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {of, throwError} from "rxjs";
import {provideRouter} from "@angular/router";
import {MessageTesting} from "@app/shared/testing/message-testing";
import {By} from "@angular/platform-browser";
import {TestGameFile} from "@app/shared/testing/objects/test-game-file";
import {TestFileBackupStartedEvent} from "@app/shared/testing/objects/test-file-backup-started-event";
import {TestProgressUpdatedEvent} from "@app/shared/testing/objects/test-progress-updated-event";
import SpyObj = jasmine.SpyObj;
import createSpy = jasmine.createSpy;
import createSpyObj = jasmine.createSpyObj;
import {TestFileBackupStatusChangedEvent} from "@app/shared/testing/objects/test-file-backup-status-changed-event";

const NOTHING_BEING_BACKED_UP_TEXT = 'Nothing is currently being backed up';

describe('InProgressFilesCardComponent', () => {
  let component: InProgressFilesCardComponent;
  let fixture: ComponentFixture<InProgressFilesCardComponent>;
  let gameFilesClient: SpyObj<GameFilesClient>;
  let messagesService: SpyObj<MessagesService>;
  let notificationService: NotificationService;

  const inProgressGameFile: GameFile = TestGameFile.inProgress();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InProgressFilesCardComponent],
      providers: [
        {
          provide: GameFilesClient,
          useValue: createSpyObj('GameFilesClient', ['getCurrentlyDownloading'])
        },
        {
          provide: MessagesService,
          useValue: createSpyObj('MessagesService', ["watch"])
        },
        {
          provide: NotificationService, useValue: createSpyObj('NotificationService', ['showSuccess', 'showFailure'])
        },
        provideRouter([])
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(InProgressFilesCardComponent);
    component = fixture.componentInstance;
    gameFilesClient = TestBed.inject(GameFilesClient) as SpyObj<GameFilesClient>;
    messagesService = TestBed.inject(MessagesService) as SpyObj<MessagesService>;
    notificationService = TestBed.inject(NotificationService);

    gameFilesClient.getCurrentlyDownloading
      .and.returnValue(of(JSON.parse(JSON.stringify(inProgressGameFile))) as any);

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
      FileBackupMessageTopics.Started,
      FileBackupMessageTopics.ProgressUpdate,
      FileBackupMessageTopics.StatusChanged
    ]);
    expect(component['subscriptions'].length).toBe(3);
  });

  it('should show failure notification given error when refreshCurrentlyDownloaded is called',
    async () => {
      const mockError = new Error('test error');
      gameFilesClient.getCurrentlyDownloading.and.returnValue(throwError(() => mockError));

      await component.refreshCurrentlyDownloaded();

      expect(notificationService.showFailure).toHaveBeenCalledWith(
        'Error fetching currently downloaded file', mockError);
      expect(component.currentDownloadIsLoading).toBeFalse();
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

  it('should retrieve files on init', async () => {
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(gameFilesClient.getCurrentlyDownloading).toHaveBeenCalled();
    expect(component.currentDownload).toEqual(inProgressGameFile);
    expect(component.currentDownloadIsLoading).toBe(false);

    expectCurrentlyDownloadingGameTitleToContain(inProgressGameFile.fileSource.originalGameTitle);
  });

  function expectCurrentlyDownloadingGameTitleToContain(expectedGameTitle: string) {
    expectGameTitleIn('#currently-downloading', expectedGameTitle);
  }

  function expectGameTitleIn(selector: string, title: string) {
    const table = fixture.debugElement.query(By.css(selector));
    expect(table.nativeElement.textContent).toContain(title);
  }

  it('should update currently downloaded game', async () => {
    const fileBackupStartedEvent: FileBackupStartedEvent = TestFileBackupStartedEvent.for(inProgressGameFile);
    fileBackupStartedEvent.originalGameTitle = "Updated game title";

    await MessageTesting.simulateWebSocketMessageReceived(fixture, messagesService,
      FileBackupMessageTopics.Started, fileBackupStartedEvent);

    expectCurrentlyDownloadingGameTitleToContain("Updated game title")
  });

  it('should update download progress', async () => {
    const progressUpdatedEvent: FileDownloadProgressUpdatedEvent = TestProgressUpdatedEvent.twentyFivePercent();

    await MessageTesting.simulateWebSocketMessageReceived(fixture, messagesService,
      FileBackupMessageTopics.ProgressUpdate, progressUpdatedEvent);

    const progressBar = fixture.debugElement.query(By.css('.progress'));
    expect(progressBar.nativeElement.textContent).toContain('25%');
  });

  it('should clear currently downloaded game when FileBackupStatusChangedEvent' +
    ' is received with status Success', async () => {
    await simulateFileBackupStatusChangedEventReceived(inProgressGameFile.id, FileBackupStatus.Success);

    const currentlyDownloadingTable = getInProgressFileTable();
    expect(currentlyDownloadingTable.nativeElement.textContent).toContain(NOTHING_BEING_BACKED_UP_TEXT);
  });

  async function simulateFileBackupStatusChangedEventReceived(id: string, newStatus: FileBackupStatus) {
    const statusChangedMessage: FileBackupStatusChangedEvent = TestFileBackupStatusChangedEvent.with(id, newStatus);
    await MessageTesting.simulateWebSocketMessageReceived(fixture, messagesService,
      FileBackupMessageTopics.StatusChanged, statusChangedMessage);
  }

  it('should clear currently downloaded game when FileBackupStatusChangedEvent' +
    ' is received with status Failed', () => {
    simulateFileBackupStatusChangedEventReceived(inProgressGameFile.id, FileBackupStatus.Failed);

    const currentlyDownloadingTable = getInProgressFileTable();
    expect(currentlyDownloadingTable.nativeElement.textContent).toContain(NOTHING_BEING_BACKED_UP_TEXT);
  });

  it('should not clear currently downloaded game when FileBackupStatusChangedEvent' +
    ' is received with status other than Success or Failed', async () => {
    await simulateFileBackupStatusChangedEventReceived(inProgressGameFile.id, FileBackupStatus.InProgress);

    const currentlyDownloadingTable = getInProgressFileTable();
    expect(currentlyDownloadingTable.nativeElement.textContent).not.toContain(NOTHING_BEING_BACKED_UP_TEXT);
  });

  function getInProgressFileTable() {
    return fixture.debugElement.query(By.css('#currently-downloading'));
  }

  it('should not clear currently downloaded game when FileBackupStatusChangedEvent' +
    ' is received with id other than current and status Success', async () => {
    await simulateFileBackupStatusChangedEventReceived('anotherGameFileId', FileBackupStatus.Success);

    const currentlyDownloadingTable = getInProgressFileTable();
    expect(currentlyDownloadingTable.nativeElement.textContent).not.toContain(NOTHING_BEING_BACKED_UP_TEXT);
  });

  it('should not clear currently downloaded game when FileBackupStatusChangedEvent' +
    ' is received with id other than current and status Failed', async () => {
    await simulateFileBackupStatusChangedEventReceived('anotherGameFileId', FileBackupStatus.Failed);

    const currentlyDownloadingTable = getInProgressFileTable();
    expect(currentlyDownloadingTable.nativeElement.textContent).not.toContain(NOTHING_BEING_BACKED_UP_TEXT);
  });

  it('should do nothing when FileBackupStatusChangedEvent' +
    ' is received and currently downloaded file is already cleared', () => {
    gameFilesClient.getCurrentlyDownloading.and.returnValue(of(undefined) as any);
    simulateFileBackupStatusChangedEventReceived('anotherGameFileId', FileBackupStatus.Failed);

    const currentlyDownloadingTable = getInProgressFileTable();
    expect(currentlyDownloadingTable.nativeElement.textContent).toContain(NOTHING_BEING_BACKED_UP_TEXT);
  });
});
