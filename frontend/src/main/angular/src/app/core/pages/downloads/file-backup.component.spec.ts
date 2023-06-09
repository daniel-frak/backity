import {ComponentFixture, TestBed} from '@angular/core/testing';

import {FileBackupComponent} from './file-backup.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {PageHeaderStubComponent} from "@app/shared/components/page-header/page-header.component.stub";
import {TableComponent} from "@app/shared/components/table/table.component";
import {LoadedContentStubComponent} from "@app/shared/components/loaded-content/loaded-content.component.stub";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {Client} from "@stomp/stompjs";
import {ReplaySubject} from "rxjs";
import {messageCallbackType} from "@stomp/stompjs/src/types";
import {StompHeaders} from "@stomp/stompjs/src/stomp-headers";
import {BackupsClient, FileBackupMessageTopics, FileBackupProgress, GameFileVersionBackup} from "@backend";
import {By} from "@angular/platform-browser";
import {TableColumnDirective} from "@app/shared/components/table/column-directive/table-column.directive";

describe('DownloadsComponent', () => {
  let component: FileBackupComponent;
  let fixture: ComponentFixture<FileBackupComponent>;
  let startSubscriptions: Function[];
  let progressSubscriptions: Function[];
  let finishSubscriptions: Function[];
  const backupsClientMock = {
    currentlyDownloading: undefined,
    processedFiles: undefined,
    queueItems: undefined,
    getCurrentlyProcessing(page?: number, size?: number, sort?: string[]): any {
      return {subscribe: (s: (f: any) => any) => s(this.currentlyDownloading)};
    },
    getProcessedFiles(page?: number, size?: number, sort?: string[]): any {
      return {subscribe: (s: (f: any) => any) => s(this.processedFiles)};
    },
    getQueueItems(page?: number, size?: number, sort?: string[]): any {
      return {subscribe: (s: (f: any) => any) => s(this.queueItems)};
    }
  } as any;

  beforeEach(async () => {
    startSubscriptions = [];
    progressSubscriptions = [];
    finishSubscriptions = [];
    backupsClientMock.currentlyDownloading = undefined;
    backupsClientMock.processedFiles = undefined;
    backupsClientMock.queueItems = undefined;
    const clientMock: Client = {
      subscribe: (destination: string, callback: messageCallbackType, headers: StompHeaders = {}
      ): any => {
        if (destination == FileBackupMessageTopics.Started) {
          startSubscriptions.push(callback);
        }
        if (destination == FileBackupMessageTopics.Progress) {
          progressSubscriptions.push(callback);
        }
        if (destination == FileBackupMessageTopics.Finished) {
          finishSubscriptions.push(callback);
        }
        return {
          id: "",
          unsubscribe(headers: StompHeaders | undefined): void {
          }
        };
      }
    } as any;
    const messagesServiceMock: MessagesService = {
      subscriptions: new ReplaySubject<(client: Client) => any>(),
      onConnect(func: (client: Client) => any): void {
        func(clientMock);
      }
    } as any;

    await TestBed.configureTestingModule({
      declarations: [
        FileBackupComponent,
        PageHeaderStubComponent,
        LoadedContentStubComponent,
        TableComponent,
        TableColumnDirective
      ],
      imports: [
        HttpClientTestingModule
      ],
      providers: [
        {
          provide: MessagesService,
          useValue: messagesServiceMock
        },
        {
          provide: BackupsClient,
          useValue: backupsClientMock
        }
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FileBackupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should subscribe to download start', () => {
    expect(startSubscriptions.length).toBe(1);
  });

  it('should subscribe to download progress', () => {
    expect(progressSubscriptions.length).toBe(1);
  });

  it('should subscribe to download finish', () => {
    expect(finishSubscriptions.length).toBe(1);
  });

  it('should set current download on start', () => {
    const expectedDownload: GameFileVersionBackup = {
      title: 'someDownload'
    };
    startSubscriptions[0]({body: JSON.stringify(expectedDownload)})
    expect(component.currentDownload).toEqual(expectedDownload);
  });

  it('should set download progress', () => {
    const expectedProgress: FileBackupProgress = {
      percentage: 25,
      timeLeftSeconds: 1234
    };
    progressSubscriptions[0]({body: JSON.stringify(expectedProgress)})
    expect(component.downloadProgress).toEqual(expectedProgress);
  });

  it('should unset current download on finish', () => {
    component.currentDownload = {
      title: 'someDownload'
    };
    finishSubscriptions[0]();
    expect(component.currentDownload).toBeUndefined();
  });

  it('should refresh', () => {
    backupsClientMock.queueItems = {
      content: [
        {
          gameTitle: "Some queued game",
          title: "queuedGame.exe",
          size: "1 GB"
        }
      ]
    };
    backupsClientMock.processedFiles = {
      content: [
        {
          gameTitle: "Some processed game",
          title: "processedGame.exe",
          size: "2 GB"
        }
      ]
    };
    backupsClientMock.currentlyDownloading = {
      gameTitle: "Some current game",
      title: "currentGame.exe",
      size: "3 GB"
    };

    component.refresh();
    fixture.detectChanges();

    expect(component.enqueuedDownloads).toBe(backupsClientMock.queueItems);
    expect(component.processedFiles).toBe(backupsClientMock.processedFiles);
    expect(component.currentDownload).toBe(backupsClientMock.currentlyDownloading);

    const currentlyDownloadingTable = fixture.debugElement.query(By.css('#currently-downloading'));
    expect(currentlyDownloadingTable.nativeElement.textContent).toContain("Some current game");

    const queueTable = fixture.debugElement.query(By.css('#download-queue'));
    expect(queueTable.nativeElement.textContent).toContain("Some queued game");

    const processedTable = fixture.debugElement.query(By.css('#processed-files'));
    expect(processedTable.nativeElement.textContent).toContain("Some processed game");
  });

  it('should log a warn when removeFromQueue is called', () => {
    spyOn(console, 'error');
    component.removeFromQueue(123);
    expect(console.error).toHaveBeenCalled();
  })
});
