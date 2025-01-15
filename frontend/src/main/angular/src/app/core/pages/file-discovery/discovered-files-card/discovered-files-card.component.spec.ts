import {ComponentFixture, TestBed} from '@angular/core/testing';

import {DiscoveredFilesCardComponent} from './discovered-files-card.component';
import {FileBackupStatus, GameFile, GameFilesClient, PageGameFile} from "@backend";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {HttpResponse} from "@angular/common/http";
import {Observable, of, throwError} from "rxjs";
import {catchError} from "rxjs/operators";
import {provideRouter} from "@angular/router";
import {TestGameFile} from "@app/shared/testing/objects/test-game-file";
import {TestPage} from "@app/shared/testing/objects/test-page";
import SpyObj = jasmine.SpyObj;
import createSpyObj = jasmine.createSpyObj;

describe('DiscoveredFilesCardComponent', () => {
  let component: DiscoveredFilesCardComponent;
  let fixture: ComponentFixture<DiscoveredFilesCardComponent>;
  let gameFilesClient: SpyObj<GameFilesClient>;
  let notificationService: NotificationService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DiscoveredFilesCardComponent],
      providers: [
        {
          provide: GameFilesClient,
          useValue: jasmine.createSpyObj('GameFilesClient', ['enqueueFileBackup', 'getGameFiles'])
        },
        {
          provide: NotificationService, useValue: createSpyObj('NotificationService', ['showSuccess', 'showFailure'])
        },
        provideRouter([])
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(DiscoveredFilesCardComponent);
    component = fixture.componentInstance;

    gameFilesClient = TestBed.inject(GameFilesClient) as SpyObj<GameFilesClient>;
    notificationService = TestBed.inject(NotificationService);

    const emptyGameFilePage: PageGameFile = {content: []};
    gameFilesClient.getGameFiles.and.returnValue(of(emptyGameFilePage) as any);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should log an error when discovered files cannot be refreshed', async () => {
    const mockError = new Error('Discovery failed');
    gameFilesClient.getGameFiles.and.returnValue(throwError(() => mockError));

    await component.refreshDiscoveredFiles();

    expect(notificationService.showFailure).toHaveBeenCalledWith(
      'Error fetching discovered files', mockError);
  });

  it('should enqueue file', async () => {
    const file: GameFile = TestGameFile.discovered();
    const fakeObservable: Observable<HttpResponse<any>> = of(new HttpResponse()).pipe(catchError(e => {
      file.fileBackup.status = FileBackupStatus.Discovered;
      throw e;
    }));
    gameFilesClient.enqueueFileBackup.and.returnValue(fakeObservable);

    await component.onClickEnqueueFile(file)();

    expect(file.fileBackup?.status).toEqual(FileBackupStatus.Enqueued);
    expect(gameFilesClient.enqueueFileBackup).toHaveBeenCalledWith(file.id);
    expect(notificationService.showSuccess).toHaveBeenCalledWith(`File backup enqueued`);
  });

  it('should dequeue file when enqueueFile throws', async () => {
    const gameFile: GameFile = TestGameFile.enqueued();
    const mockError = new Error("error1");
    const observableMock: any = createSpyObj('Observable', ['subscribe', 'pipe']);
    observableMock.pipe.and.returnValue(observableMock);

    gameFilesClient.enqueueFileBackup.and.returnValue(new Observable(subscriber => {
      expect(gameFile.fileBackup?.status).toEqual(FileBackupStatus.Enqueued);
      subscriber.error(mockError);
    }));

    await component.enqueueFile(gameFile);

    expect(gameFile.fileBackup?.status).toEqual(FileBackupStatus.Discovered);
    expect(notificationService.showFailure).toHaveBeenCalledWith(
      'An error occurred while trying to enqueue a file', gameFile, mockError);
  });

  it('should refresh game files', async () => {
    const expectedGameFilePage: PageGameFile = TestPage.of([TestGameFile.inProgress()]);
    gameFilesClient.getGameFiles.and.returnValue(of(expectedGameFilePage) as any);

    await component.refreshDiscoveredFiles();

    expect(component.filePage).toEqual(expectedGameFilePage);
    expect(component.filesAreLoading).toBeFalse();
  });
});
