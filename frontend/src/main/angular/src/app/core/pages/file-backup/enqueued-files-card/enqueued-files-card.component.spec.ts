import {ComponentFixture, TestBed} from '@angular/core/testing';

import {EnqueuedFilesCardComponent} from './enqueued-files-card.component';
import {
  FileBackupMessageTopics,
  FileBackupStartedEvent,
  GameFile,
  GameFileProcessingStatus,
  GameFilesClient,
  PageGameFile
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

describe('EnqueuedFilesCardComponent', () => {
  let component: EnqueuedFilesCardComponent;
  let fixture: ComponentFixture<EnqueuedFilesCardComponent>;
  let gameFilesClient: SpyObj<GameFilesClient>;
  let messagesService: SpyObj<MessagesService>;
  let notificationService: NotificationService;

  const enqueuedGameFile: GameFile = TestGameFile.enqueued();
  const initialEnqueuedDownloads: PageGameFile = TestPage.of([enqueuedGameFile]);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EnqueuedFilesCardComponent],
      providers: [
        {
          provide: GameFilesClient,
          useValue: createSpyObj('GameFilesClient', ['getGameFiles'])
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

    fixture = TestBed.createComponent(EnqueuedFilesCardComponent);
    component = fixture.componentInstance;
    gameFilesClient = TestBed.inject(GameFilesClient) as SpyObj<GameFilesClient>;
    messagesService = TestBed.inject(MessagesService) as SpyObj<MessagesService>;
    notificationService = TestBed.inject(NotificationService);

    gameFilesClient.getGameFiles.withArgs(GameFileProcessingStatus.Enqueued, anything())
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
      gameFilesClient.getGameFiles.withArgs(GameFileProcessingStatus.Enqueued, anything())
        .and.returnValue(throwError(() => mockError));

      await component.refreshEnqueuedFiles();

      expect(notificationService.showFailure).toHaveBeenCalledWith(
        'Error fetching enqueued files', mockError);
      expect(component.filesAreLoading).toBeFalse();
    });

  it('should retrieve files on init', async () => {
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(gameFilesClient.getGameFiles).toHaveBeenCalledWith(GameFileProcessingStatus.Enqueued, {
      page: 0,
      size: component.pageSize
    });
    expect(component.filesAreLoading).toBe(false);
    expectEnqueuedGameTitleToContain(enqueuedGameFile.fileSource.originalGameTitle);
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
    const fileBackupStartedEvent: FileBackupStartedEvent = TestFileBackupStartedEvent.for(enqueuedGameFile);

    await MessageTesting.simulateWebSocketMessageReceived(fixture, messagesService,
      FileBackupMessageTopics.Started, fileBackupStartedEvent);

    expectDownloadQueueToBeEmpty();
  });

  function expectDownloadQueueToBeEmpty() {
    const table = fixture.debugElement.query(By.css('#download-queue'));
    expect(table.nativeElement.textContent).toContain('No data');
  }
});
