import {ComponentFixture, TestBed} from '@angular/core/testing';
import {GamesComponent} from './games.component';
import {provideHttpClientTesting} from "@angular/common/http/testing";
import {FileBackupsClient, FileBackupStatus, GameFile, GameFilesClient, GamesClient, PageGameWithFiles} from "@backend";
import {of, throwError} from "rxjs";
import {PageHeaderStubComponent} from "@app/shared/components/page-header/page-header.component.stub";
import {TableComponent} from "@app/shared/components/table/table.component";
import {LoadedContentStubComponent} from "@app/shared/components/loaded-content/loaded-content.component.stub";
import {TableColumnDirective} from "@app/shared/components/table/column-directive/table-column.directive";
import {provideHttpClient, withInterceptorsFromDi} from '@angular/common/http';
import {ButtonComponent} from "@app/shared/components/button/button.component";
import {FileStatusBadgeComponent} from "@app/core/pages/games/file-status-badge/file-status-badge.component";
import createSpyObj = jasmine.createSpyObj;
import Spy = jasmine.Spy;

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
        TableColumnDirective,
        FileStatusBadgeComponent
      ],
      imports: [
        ButtonComponent
      ],
      providers: [
        {provide: GamesClient, useValue: createSpyObj('GamesClient', ['getGames'])},
        {provide: GameFilesClient, useValue: createSpyObj('GameFilesClient', ['enqueueFileBackup'])},
        {provide: FileBackupsClient, useValue: createSpyObj('FileBackupsClient', ['deleteFileBackup'])},
        provideHttpClient(withInterceptorsFromDi()),
        provideHttpClientTesting()
      ]
    }).compileComponents();

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

  it('should get games', async () => {
    const gameFile = {...sampleGameFile, gameProviderFile: {...sampleGameFile.gameProviderFile, fileTitle: 'game.exe'}};

    const mockGames: PageGameWithFiles = {
      content: [{
        id: "someGameId",
        title: "someGameTitle",
        files: [gameFile]
      }]
    };
    (gamesClient.getGames as Spy).and.returnValue(of(mockGames));

    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(gamesClient.getGames).toHaveBeenCalledWith({page: 0, size: 20});
    expect(component.gameWithFilesPage).toEqual(mockGames);
    expect(component.gamesAreLoading).toBeFalse();

    const pageText = fixture.debugElement.nativeElement.textContent;
    expect(pageText).toContain('someGameTitle');
    expect(pageText).toContain('game.exe');
  });

  it('should log an error when games cannot be retrieved', async () => {
    spyOn(console, 'error');
    const mockError = new Error('Discovery failed');

    (gamesClient.getGames as jasmine.Spy).and.returnValue(throwError(() => mockError));

    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(console.error).toHaveBeenCalledWith('Error fetching games:', mockError);
  });

  it('should back up game file and set its status to Enqueued', async () => {
    spyOn(console, 'info');
    const gameFile = {...sampleGameFile, fileBackup: {status: FileBackupStatus.Enqueued}};
    (gameFilesClient.enqueueFileBackup as Spy).and.returnValue(of(null));

    await component.enqueueFileBackup(gameFile)();

    expect(gameFile.fileBackup?.status).toBe(FileBackupStatus.Enqueued);
    expect(gameFilesClient.enqueueFileBackup).toHaveBeenCalledWith(gameFile.id);
    expect(console.info).toHaveBeenCalledWith(`Enqueuing backup: ${gameFile.id}`);
  });

  it('should set file status to Discovered and log error when backup fails', async () => {
    spyOn(console, 'info');
    spyOn(console, 'error');
    const gameFile = {
      ...sampleGameFile,
      fileBackup: {
        status: FileBackupStatus.Discovered
      }
    };
    const mockError = new Error('Backup error');
    (gameFilesClient.enqueueFileBackup as Spy).and.returnValue(throwError(() => mockError));

    await component.enqueueFileBackup(gameFile)();

    expect(gameFile.fileBackup?.status).toBe(FileBackupStatus.Discovered);
    expect(gameFilesClient.enqueueFileBackup).toHaveBeenCalledWith(gameFile.id);
    expect(console.info).toHaveBeenCalledWith(`Enqueuing backup: ${gameFile.id}`);
    expect(console.error).toHaveBeenCalledWith(
      `An error occurred while trying to enqueue a file (id=${gameFile.id})`,
      gameFile,
      mockError
    );
  });

  it('should log an error for various operations', async () => {
    spyOn(console, 'error');

    const operations = [
      {method: () => component.cancelBackup('someGameFileId')(), message: 'Removing from queue not yet implemented'},
      {method: () => component.viewFilePath('someFileId')(), message: 'Viewing file paths not yet implemented'},
      {method: () => component.download('someFileId')(), message: 'Downloading files not yet implemented'},
      {method: () => component.viewError('someFileId')(), message: 'Viewing errors not yet implemented'}
    ];

    for (const op of operations) {
      await op.method();
      expect(console.error).toHaveBeenCalledWith(op.message);
    }
  });

  it('should delete file backup and refresh list', async () => {
    const gameFileId = 'someGameFileId';
    (fileBackupsClient.deleteFileBackup as Spy).and.returnValue(of(null));
    const mockGames: PageGameWithFiles = {
      content: [{
        id: "someGameId",
        title: "someGameTitle",
        files: []
      }]
    };
    (gamesClient.getGames as Spy).and.returnValue(of(mockGames));

    await component.deleteBackup(gameFileId)();

    expect(fileBackupsClient.deleteFileBackup).toHaveBeenCalledWith(gameFileId);
    expect(gamesClient.getGames).toHaveBeenCalled();
  });

  it('should log error when file backup could not be deleted', async () => {
    spyOn(console, 'error');
    const gameFileId = 'someGameFileId';
    const mockError = new Error('Backup error');
    (fileBackupsClient.deleteFileBackup as Spy).and.returnValue(throwError(() => mockError));

    await expectAsync(component.deleteBackup(gameFileId)()).toBeRejected();

    expect(console.error).toHaveBeenCalledWith(
      `An error occurred while trying to delete a file backup (id=${gameFileId})`,
      gameFileId,
      mockError
    );
  });
});
