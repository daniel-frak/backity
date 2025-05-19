import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ProcessedFilesCardComponent} from './processed-files-card.component';
import {GameFile, GameFileProcessingStatus, GameFilesClient} from "@backend";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {provideRouter} from "@angular/router";
import {of, throwError} from "rxjs";
import {MessageTesting} from "@app/shared/testing/message-testing";
import {By} from "@angular/platform-browser";
import {TestGameFile} from "@app/shared/testing/objects/test-game-file";
import {TestPage} from "@app/shared/testing/objects/test-page";
import createSpyObj = jasmine.createSpyObj;
import SpyObj = jasmine.SpyObj;
import anything = jasmine.anything;

describe('ProcessedFilesCardComponent', () => {
  let component: ProcessedFilesCardComponent;
  let fixture: ComponentFixture<ProcessedFilesCardComponent>;
  let gameFilesClient: SpyObj<GameFilesClient>;
  let messagesService: SpyObj<MessagesService>;
  let notificationService: NotificationService;

  const processedGameFile: GameFile = TestGameFile.successfullyProcessed();
  const initialProcessedFiles = TestPage.of([processedGameFile]);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProcessedFilesCardComponent],
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

    fixture = TestBed.createComponent(ProcessedFilesCardComponent);
    component = fixture.componentInstance;
    gameFilesClient = TestBed.inject(GameFilesClient) as SpyObj<GameFilesClient>;
    messagesService = TestBed.inject(MessagesService) as SpyObj<MessagesService>;
    notificationService = TestBed.inject(NotificationService);

    gameFilesClient.getGameFiles.withArgs(GameFileProcessingStatus.Processed, anything())
      .and.returnValue(of(JSON.parse(JSON.stringify(initialProcessedFiles))) as any);

    MessageTesting.mockWatch(messagesService, (destination, callback) => {
      // Do nothing
    });
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show failure notification given error when refreshProcessedFiles is called',
    async () => {
      const mockError = new Error('test error');
      gameFilesClient.getGameFiles.withArgs(GameFileProcessingStatus.Processed, anything())
        .and.returnValue(throwError(() => mockError));

      await component.refreshProcessedFiles();

      expect(notificationService.showFailure).toHaveBeenCalledWith(
        'Error fetching processed files', mockError);
      expect(component.filesAreLoading).toBeFalse();
    });

  it('should retrieve files on init', async () => {
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(gameFilesClient.getGameFiles).toHaveBeenCalledWith(GameFileProcessingStatus.Processed, {
      page: 0,
      size: component.pageSize
    });
    expect(component.filesAreLoading).toBe(false);
    expectProcessedGameTitleToContain(processedGameFile.fileSource.originalGameTitle);
  });

  function expectProcessedGameTitleToContain(expectedGameTitle: string) {
    expectGameTitleIn('#processed-files', expectedGameTitle);
  }

  function expectGameTitleIn(selector: string, title: string) {
    const table = fixture.debugElement.query(By.css(selector));
    expect(table.nativeElement.textContent).toContain(title);
  }
});
