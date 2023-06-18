import {ComponentFixture, TestBed} from '@angular/core/testing';

import {GamesComponent} from './games.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {
  FileBackupStatus,
  GameFileDetails,
  GameFileDetailsClient,
  GamesClient,
  PageHttpDtoGameWithFiles
} from "@backend";
import {of, throwError} from "rxjs";
import {PageHeaderStubComponent} from "@app/shared/components/page-header/page-header.component.stub";
import {TableComponent} from "@app/shared/components/table/table.component";
import {LoadedContentStubComponent} from "@app/shared/components/loaded-content/loaded-content.component.stub";
import {TableColumnDirective} from "@app/shared/components/table/column-directive/table-column.directive";

describe('GamesComponent', () => {
  let component: GamesComponent;
  let fixture: ComponentFixture<GamesComponent>;

  let gamesClient: GamesClient;
  let gameFileDetailsClient: GameFileDetailsClient;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        GamesComponent,
        PageHeaderStubComponent,
        LoadedContentStubComponent,
        TableComponent,
        TableColumnDirective
      ],
      imports: [
        HttpClientTestingModule
      ],
      providers: [
        {provide: GamesClient, useValue: {getGames: jasmine.createSpy('getGames')}},
        {provide: GameFileDetailsClient, useValue: {download: jasmine.createSpy('download')}}
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GamesComponent);
    component = fixture.componentInstance;
    gamesClient = TestBed.inject(GamesClient);
    gameFileDetailsClient = TestBed.inject(GameFileDetailsClient);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with loading state', () => {
    expect(component.gamesAreLoading).toBeTrue();
  });

  it('should get games', () => {
    const mockGames: PageHttpDtoGameWithFiles = {
      content: [{
        id: "someGameId",
        title: "someGameTitle",
        gameFiles: [{
          id: "someGameFileId",
          sourceFileDetails: {
            sourceId: "someSource",
            fileTitle: "someFileTitle"
          },
          backupDetails: {
            status: "DISCOVERED"
          }
        }]
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
    expect(pageText).toContain('someFileTitle');
  });

  it('should back up game file and set its status to Enqueued', () => {
    const mockFile: GameFileDetails = {
      id: "someGameFileId", backupDetails: {
        status: FileBackupStatus.Discovered
      }
    };
    (gameFileDetailsClient.download as jasmine.Spy).and.returnValue(of(null));

    component.backUp(mockFile);

    expect(mockFile.backupDetails?.status).toBe(FileBackupStatus.Enqueued);
    expect(gameFileDetailsClient.download).toHaveBeenCalledWith(mockFile.id!);
  });

  it('should set file status to Discovered when backup fails', () => {
    const mockFile: GameFileDetails = {
      id: "someGameFileId", backupDetails: {
        status: FileBackupStatus.Discovered
      }
    };
    const mockError = new Error('Backup error');
    (gameFileDetailsClient.download as jasmine.Spy).and.returnValue(throwError(mockError));

    component.backUp(mockFile);

    expect(mockFile.backupDetails?.status).toBe(FileBackupStatus.Discovered);
    expect(gameFileDetailsClient.download).toHaveBeenCalledWith(mockFile.id!);
  });

  it('should log an error when canceling backup', () => {
    spyOn(console, 'error');

    component.cancelBackup("someGameFileId");

    expect(console.error).toHaveBeenCalledWith('Removing from queue not yet implemented');
  });

  it('should log an error when deleting backup', () => {
    spyOn(console, 'error');

    component.deleteBackup("someGameFileId");

    expect(console.error).toHaveBeenCalledWith('Deleting backups not yet implemented');
  });

  it('should log an error when viewing file path', () => {
    spyOn(console, 'error');

    component.viewFilePath("someGameFileId");

    expect(console.error).toHaveBeenCalledWith('Viewing file paths not yet implemented');
  });

  it('should log an error when downloading file', () => {
    spyOn(console, 'error');

    component.download("someGameFileId");

    expect(console.error).toHaveBeenCalledWith('Downloading files not yet implemented');
  });

  it('should log an error when viewing error', () => {
    spyOn(console, 'error');

    component.viewError("someGameFileId");

    expect(console.error).toHaveBeenCalledWith('Viewing errors not yet implemented');
  });

  it('should set file status to Discovered and log error when backup fails', () => {
    const mockFile: GameFileDetails = {
      id: "someGameFileId", backupDetails: {
        status: FileBackupStatus.Discovered
      }
    };
    const mockError = new Error('Backup error');
    (gameFileDetailsClient.download as jasmine.Spy).and.returnValue(throwError(mockError));
    spyOn(console, 'error');

    component.backUp(mockFile);

    expect(mockFile.backupDetails?.status).toBe(FileBackupStatus.Discovered);
    expect(gameFileDetailsClient.download).toHaveBeenCalledWith(mockFile.id!);
    expect(console.error).toHaveBeenCalledWith(
      `An error occurred while trying to enqueue a file (${mockFile})`,
      mockFile,
      mockError
    );
  });
});
