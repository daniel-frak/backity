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

  const sampleFileDetails: FileDetails = {
    id: "someFileId",
    gameId: "someGameId",
    sourceFileDetails: {
      sourceId: "someSourceId",
      originalGameTitle: "Some game",
      originalFileName: "Some original file name",
      version: "Some version",
      url: "some.url",
      size: "3 GB",
      fileTitle: "currentGame.exe"
    },
    backupDetails: {
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
    const fileDetails = {... sampleFileDetails};
    fileDetails.sourceFileDetails.fileTitle = 'game.exe';

    const mockGames: PageGameWithFiles = {
      content: [{
        id: "someGameId",
        title: "someGameTitle",
        files: [fileDetails]
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
    const fileDetails = {... sampleFileDetails};
    fileDetails.backupDetails.status = FileBackupStatus.Enqueued;
    (fileDetailsClient.download as jasmine.Spy).and.returnValue(of(null));

    component.backUp(fileDetails);

    expect(fileDetails.backupDetails?.status).toBe(FileBackupStatus.Enqueued);
    expect(fileDetailsClient.download).toHaveBeenCalledWith(fileDetails.id!);
  });

  it('should set file status to Discovered when backup fails', () => {
    const fileDetails = {... sampleFileDetails};
    fileDetails.backupDetails.status = FileBackupStatus.Discovered;
    const mockError = new Error('Backup error');
    (fileDetailsClient.download as jasmine.Spy).and.returnValue(throwError(mockError));

    component.backUp(fileDetails);

    expect(fileDetails.backupDetails?.status).toBe(FileBackupStatus.Discovered);
    expect(fileDetailsClient.download).toHaveBeenCalledWith(fileDetails.id!);
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
    const fileDetails = {... sampleFileDetails};
    fileDetails.backupDetails.status = FileBackupStatus.Discovered;
    const mockError = new Error('Backup error');
    (fileDetailsClient.download as jasmine.Spy).and.returnValue(throwError(mockError));
    spyOn(console, 'error');

    component.backUp(fileDetails);

    expect(fileDetails.backupDetails?.status).toBe(FileBackupStatus.Discovered);
    expect(fileDetailsClient.download).toHaveBeenCalledWith(fileDetails.id!);
    expect(console.error).toHaveBeenCalledWith(
      `An error occurred while trying to enqueue a file (id=${fileDetails.id})`,
      fileDetails,
      mockError
    );
  });
});
