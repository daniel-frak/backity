import {ComponentFixture, TestBed} from '@angular/core/testing';

import {GamesComponent} from './games.component';
import { provideHttpClientTesting } from "@angular/common/http/testing";
import {FileBackupsClient, FileBackupStatus, GameFile, GameFilesClient, GamesClient, PageGameWithFiles} from "@backend";
import {of, throwError} from "rxjs";
import {PageHeaderStubComponent} from "@app/shared/components/page-header/page-header.component.stub";
import {TableComponent} from "@app/shared/components/table/table.component";
import {LoadedContentStubComponent} from "@app/shared/components/loaded-content/loaded-content.component.stub";
import {TableColumnDirective} from "@app/shared/components/table/column-directive/table-column.directive";
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';

describe('GamesComponent', () => {
  let component: GamesComponent;
  let fixture: ComponentFixture<GamesComponent>;

  let gamesClient: GamesClient;
  let gameFilesClient: GameFilesClient;
  let fileBackupsClient: FileBackupsClient;

  const sampleGameFile: GameFile = {
    id: "someFileId",
    gameId: "someGameId",
    gameProviderFile: {
      gameProviderId: "someGameProviderId",
      originalGameTitle: "Some game",
      originalFileName: "Some original file name",
      version: "Some version",
      url: "some.url",
      size: "3 GB",
      fileTitle: "currentGame.exe"
    },
    fileBackup: {
      status: "DISCOVERED"
    }
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
    declarations: [
        GamesComponent,
        PageHeaderStubComponent,
        LoadedContentStubComponent,
        TableComponent,
        TableColumnDirective
    ],
    imports: [],
    providers: [
        { provide: GamesClient, useValue: { getGames: jasmine.createSpy('getGames') } },
        { provide: GameFilesClient, useValue: { download: jasmine.createSpy('download') } },
        { provide: FileBackupsClient, useValue: { deleteFileBackup: jasmine.createSpy('deleteFileBackup') } },
        provideHttpClient(withInterceptorsFromDi()),
        provideHttpClientTesting()
    ]
})
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GamesComponent);
    component = fixture.componentInstance;
    gamesClient = TestBed.inject(GamesClient);
    gameFilesClient = TestBed.inject(GameFilesClient);
    fileBackupsClient = TestBed.inject(FileBackupsClient);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with loading state', () => {
    expect(component.gamesAreLoading).toBeTrue();
  });

  it('should get games', () => {
    const gameFile = {... sampleGameFile};
    gameFile.gameProviderFile.fileTitle = 'game.exe';

    const mockGames: PageGameWithFiles = {
      content: [{
        id: "someGameId",
        title: "someGameTitle",
        files: [gameFile]
      }]
    };
    (gamesClient.getGames as jasmine.Spy).and.returnValue(of(mockGames));

    fixture.detectChanges();

    expect(gamesClient.getGames).toHaveBeenCalledWith({
      page: 0,
      size: 20
    });
    expect(component.gameWithFilesPage).toEqual(mockGames);
    expect(component.gamesAreLoading).toBeFalse();

    const pageText = fixture.debugElement.nativeElement.textContent;
    expect(pageText).toContain('someGameTitle');
    expect(pageText).toContain('game.exe');
  });

  it('should back up game file and set its status to Enqueued', () => {
    const gameFile = {... sampleGameFile};
    gameFile.fileBackup.status = FileBackupStatus.Enqueued;
    (gameFilesClient.download as jasmine.Spy).and.returnValue(of(null));

    component.enqueueFileBackup(gameFile);

    expect(gameFile.fileBackup?.status).toBe(FileBackupStatus.Enqueued);
    expect(gameFilesClient.download).toHaveBeenCalledWith(gameFile.id!);
  });

  it('should set file status to Discovered when backup fails', () => {
    const gameFile = {... sampleGameFile};
    gameFile.fileBackup.status = FileBackupStatus.Discovered;
    const mockError = new Error('Backup error');
    (gameFilesClient.download as jasmine.Spy).and.returnValue(throwError(mockError));

    component.enqueueFileBackup(gameFile);

    expect(gameFile.fileBackup?.status).toBe(FileBackupStatus.Discovered);
    expect(gameFilesClient.download).toHaveBeenCalledWith(gameFile.id!);
  });

  it('should log an error when canceling backup', () => {
    spyOn(console, 'error');

    component.cancelBackup("someFileId");

    expect(console.error).toHaveBeenCalledWith('Removing from queue not yet implemented');
  });

  it('should log an error when viewing file path', () => {
    spyOn(console, 'error');

    component.viewFilePath("someFileId");

    expect(console.error).toHaveBeenCalledWith('Viewing file paths not yet implemented');
  });

  it('should log an error when downloading file', () => {
    spyOn(console, 'error');

    component.download("someFileId");

    expect(console.error).toHaveBeenCalledWith('Downloading files not yet implemented');
  });

  it('should log an error when viewing error', () => {
    spyOn(console, 'error');

    component.viewError("someFileId");

    expect(console.error).toHaveBeenCalledWith('Viewing errors not yet implemented');
  });

  it('should set file status to Discovered and log error when backup fails', () => {
    const gameFile = {... sampleGameFile};
    gameFile.fileBackup.status = FileBackupStatus.Discovered;
    const mockError = new Error('Backup error');
    (gameFilesClient.download as jasmine.Spy).and.returnValue(throwError(mockError));
    spyOn(console, 'error');

    component.enqueueFileBackup(gameFile);

    expect(gameFile.fileBackup?.status).toBe(FileBackupStatus.Discovered);
    expect(gameFilesClient.download).toHaveBeenCalledWith(gameFile.id!);
    expect(console.error).toHaveBeenCalledWith(
      `An error occurred while trying to enqueue a file (id=${gameFile.id})`,
      gameFile,
      mockError
    );
  });

  it('should delete file backup and refresh list', () => {
    let gameFileId = 'someGameFileId';
    (fileBackupsClient.deleteFileBackup as jasmine.Spy).and.returnValue(of(null));
    const mockGames: PageGameWithFiles = {
      content: [{
        id: "someGameId",
        title: "someGameTitle",
        files: []
      }]
    };
    (gamesClient.getGames as jasmine.Spy).and.returnValue(of(mockGames));

    component.deleteBackup(gameFileId);

    expect(fileBackupsClient.deleteFileBackup).toHaveBeenCalledWith(gameFileId);
    expect(gamesClient.getGames).toHaveBeenCalled();
  });

  it('should log error when file backup could not be deleted', () => {
    spyOn(console, 'error');
    let gameFileId = 'someGameFileId';
    const mockError = new Error('Backup error');
    (fileBackupsClient.deleteFileBackup as jasmine.Spy).and.returnValue(throwError(mockError));

    component.deleteBackup(gameFileId);

    expect(console.error).toHaveBeenCalledWith(
      `An error occurred while trying to delete a file backup (id=${gameFileId})`,
      gameFileId,
      mockError
    );
  });
});
