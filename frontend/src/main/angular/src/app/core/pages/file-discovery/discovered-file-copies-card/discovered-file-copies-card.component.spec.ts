import {ComponentFixture, TestBed} from '@angular/core/testing';

import {DiscoveredFileCopiesCardComponent} from './discovered-file-copies-card.component';
import {FileCopyStatus, FileCopy, FileCopiesClient, PageFileCopy, EnqueueFileCopyRequest} from "@backend";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {HttpResponse} from "@angular/common/http";
import {Observable, of, throwError} from "rxjs";
import {catchError} from "rxjs/operators";
import {provideRouter} from "@angular/router";
import {TestPage} from "@app/shared/testing/objects/test-page";
import SpyObj = jasmine.SpyObj;
import createSpyObj = jasmine.createSpyObj;
import {TestFileCopy} from "@app/shared/testing/objects/test-file-copy";

describe('DiscoveredFileCopiesCardComponent', () => {
  let component: DiscoveredFileCopiesCardComponent;
  let fixture: ComponentFixture<DiscoveredFileCopiesCardComponent>;
  let fileCopiesClient: SpyObj<FileCopiesClient>;
  let notificationService: NotificationService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DiscoveredFileCopiesCardComponent],
      providers: [
        {
          provide: FileCopiesClient,
          useValue: jasmine.createSpyObj('FileCopiesClient', ['enqueueFileCopy', 'getFileCopiesWithStatus'])
        },
        {
          provide: NotificationService,
          useValue: createSpyObj('NotificationService', ['showSuccess', 'showFailure'])
        },
        provideRouter([])
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(DiscoveredFileCopiesCardComponent);
    component = fixture.componentInstance;

    fileCopiesClient = TestBed.inject(FileCopiesClient) as SpyObj<FileCopiesClient>;
    notificationService = TestBed.inject(NotificationService);

    const emptyFileCopyPage: PageFileCopy = {content: []};
    fileCopiesClient.getFileCopiesWithStatus.and.returnValue(of(emptyFileCopyPage) as any);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should log an error when discovered files cannot be refreshed', async () => {
    const mockError = new Error('Discovery failed');
    fileCopiesClient.getFileCopiesWithStatus.and.returnValue(throwError(() => mockError));

    await component.refreshDiscoveredFiles();

    expect(notificationService.showFailure).toHaveBeenCalledWith(
      'Error fetching discovered files', mockError);
  });

  it('should enqueue file', async () => {
    const fileCopy: FileCopy = TestFileCopy.discovered();
    const fakeObservable: Observable<HttpResponse<any>> = of(new HttpResponse()).pipe(catchError(e => {
      fileCopy.status = FileCopyStatus.Discovered;
      throw e;
    }));
    fileCopiesClient.enqueueFileCopy.and.returnValue(fakeObservable);

    await component.onClickEnqueueFile(fileCopy)();

    expect(fileCopy.status).toEqual(FileCopyStatus.Enqueued);
    let enqueueRequest = enqueueFileCopyRequestFrom(fileCopy);
    expect(fileCopiesClient.enqueueFileCopy).toHaveBeenCalledWith(enqueueRequest);
    expect(notificationService.showSuccess).toHaveBeenCalledWith(`File copy enqueued`);
  });

  function enqueueFileCopyRequestFrom(fileCopy: FileCopy) {
    let enqueueRequest: EnqueueFileCopyRequest = {
      fileCopyNaturalId: fileCopy.naturalId
    };
    return enqueueRequest;
  }

  it('should dequeue file when enqueueFile throws', async () => {
    const fileCopy: FileCopy = TestFileCopy.enqueued();
    const mockError = new Error("error1");
    const observableMock: any = createSpyObj('Observable', ['subscribe', 'pipe']);
    observableMock.pipe.and.returnValue(observableMock);

    fileCopiesClient.enqueueFileCopy.and.returnValue(new Observable(subscriber => {
      expect(fileCopy.status).toEqual(FileCopyStatus.Enqueued);
      subscriber.error(mockError);
    }));

    await component.enqueueFile(fileCopy);

    expect(fileCopy.status).toEqual(FileCopyStatus.Discovered);
    expect(notificationService.showFailure).toHaveBeenCalledWith(
      'An error occurred while trying to enqueue a file', fileCopy, mockError);
  });

  it('should refresh game files', async () => {
    const expectedFileCopyPage: PageFileCopy = TestPage.of([TestFileCopy.inProgress()]);
    fileCopiesClient.getFileCopiesWithStatus.and.returnValue(of(expectedFileCopyPage) as any);

    await component.refreshDiscoveredFiles();

    expect(component.filePage).toEqual(expectedFileCopyPage);
    expect(component.filesAreLoading).toBeFalse();
  });
});
