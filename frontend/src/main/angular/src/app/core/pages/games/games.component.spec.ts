import {ComponentFixture, TestBed} from '@angular/core/testing';

import {GamesComponent} from './games.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {FileBackupStatus, FileDetails, FileDetailsClient, GamesClient, PageGameWithFiles} from "@backend";
import {of, throwError} from "rxjs";
import {PageHeaderStubComponent} from "@app/shared/components/page-header/page-header.component.stub";
import {TableComponent} from "@app/shared/components/table/table.component";
import {LoadedContentStubComponent} from "@app/shared/components/loaded-content/loaded-content.component.stub";
import {TableColumnDirective} from "@app/shared/components/table/column-directive/table-column.directive";

describe('GamesComponent', () => {
  let component: GamesComponent;
  let fixture: ComponentFixture<GamesComponent>;

  let gamesClient: GamesClient;
  let fileDetailsClient: FileDetailsClient;

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
        {provide: FileDetailsClient, useValue: {download: jasmine.createSpy('download')}}
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GamesComponent);
    component = fixture.componentInstance;
    gamesClient = TestBed.inject(GamesClient);
    fileDetailsClient = TestBed.inject(FileDetailsClient);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with loading state', () => {
    expect(component.gamesAreLoading).toBeTrue();
  });

  it('should get games', () => {
    const mockGames: PageGameWithFiles = {
      content: [{
        id: "someGameId",
        title: "someGameTitle",
        files: [{
          id: "someFileId",
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
    const mockFile: FileDetails = {
      id: "someFileId", backupDetails: {
        status: FileBackupStatus.Discovered
      }
    };
    (fileDetailsClient.download as jasmine.Spy).and.returnValue(of(null));

    component.backUp(mockFile);

    expect(mockFile.backupDetails?.status).toBe(FileBackupStatus.Enqueued);
    expect(fileDetailsClient.download).toHaveBeenCalledWith(mockFile.id!);
  });

  it('should set file status to Discovered when backup fails', () => {
    const mockFile: FileDetails = {
      id: "someFileId", backupDetails: {
        status: FileBackupStatus.Discovered
      }
    };
    const mockError = new Error('Backup error');
    (fileDetailsClient.download as jasmine.Spy).and.returnValue(throwError(mockError));

    component.backUp(mockFile);

    expect(mockFile.backupDetails?.status).toBe(FileBackupStatus.Discovered);
    expect(fileDetailsClient.download).toHaveBeenCalledWith(mockFile.id!);
  });

  it('should log an error when canceling backup', () => {
    spyOn(console, 'error');

    component.cancelBackup("someFileId");

    expect(console.error).toHaveBeenCalledWith('Removing from queue not yet implemented');
  });

  it('should log an error when deleting backup', () => {
    spyOn(console, 'error');

    component.deleteBackup("someFileId");

    expect(console.error).toHaveBeenCalledWith('Deleting backups not yet implemented');
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
    const mockFile: FileDetails = {
      id: "someFileId", backupDetails: {
        status: FileBackupStatus.Discovered
      }
    };
    const mockError = new Error('Backup error');
    (fileDetailsClient.download as jasmine.Spy).and.returnValue(throwError(mockError));
    spyOn(console, 'error');

    component.backUp(mockFile);

    expect(mockFile.backupDetails?.status).toBe(FileBackupStatus.Discovered);
    expect(fileDetailsClient.download).toHaveBeenCalledWith(mockFile.id!);
    expect(console.error).toHaveBeenCalledWith(
      `An error occurred while trying to enqueue a file (${mockFile})`,
      mockFile,
      mockError
    );
  });
});
