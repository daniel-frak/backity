import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ProcessedFileCopiesCardComponent} from './processed-file-copies-card.component';
import {FileCopy, FileCopyProcessingStatus, FileCopiesClient} from "@backend";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {provideRouter} from "@angular/router";
import {of, throwError} from "rxjs";
import {MessageTesting} from "@app/shared/testing/message-testing";
import {By} from "@angular/platform-browser";
import {TestPage} from "@app/shared/testing/objects/test-page";
import createSpyObj = jasmine.createSpyObj;
import SpyObj = jasmine.SpyObj;
import anything = jasmine.anything;
import {TestFileCopy} from "@app/shared/testing/objects/test-file-copy";

describe('ProcessedFileCopiesCardComponent', () => {
  let component: ProcessedFileCopiesCardComponent;
  let fixture: ComponentFixture<ProcessedFileCopiesCardComponent>;
  let fileCopiesClient: SpyObj<FileCopiesClient>;
  let messagesService: SpyObj<MessagesService>;
  let notificationService: NotificationService;

  const processedFileCopy: FileCopy = TestFileCopy.storedIntegrityUnknown();
  const initialProcessedFiles = TestPage.of([processedFileCopy]);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProcessedFileCopiesCardComponent],
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

    fixture = TestBed.createComponent(ProcessedFileCopiesCardComponent);
    component = fixture.componentInstance;
    fileCopiesClient = TestBed.inject(FileCopiesClient) as SpyObj<FileCopiesClient>;
    messagesService = TestBed.inject(MessagesService) as SpyObj<MessagesService>;
    notificationService = TestBed.inject(NotificationService);

    fileCopiesClient.getFileCopiesWithStatus.withArgs(FileCopyProcessingStatus.Processed, anything())
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
      fileCopiesClient.getFileCopiesWithStatus.withArgs(FileCopyProcessingStatus.Processed, anything())
        .and.returnValue(throwError(() => mockError));

      await component.refreshProcessedFileCopies();

      expect(notificationService.showFailure).toHaveBeenCalledWith(
        'Error fetching processed files', mockError);
      expect(component.fileCopiesAreLoading).toBeFalse();
    });

  it('should retrieve files on init', async () => {
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(fileCopiesClient.getFileCopiesWithStatus).toHaveBeenCalledWith(FileCopyProcessingStatus.Processed, {
      page: 0,
      size: component.pageSize
    });
    expect(component.fileCopiesAreLoading).toBe(false);
    expectProcessedFileCopyToContain(processedFileCopy.naturalId.gameFileId!);
  });

  function expectProcessedFileCopyToContain(expectedString: string) {
    expectGameTitleIn('#processed-files', expectedString);
  }

  function expectGameTitleIn(selector: string, title: string) {
    const table = fixture.debugElement.query(By.css(selector));
    expect(table.nativeElement.textContent).toContain(title);
  }
});
