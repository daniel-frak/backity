import {ComponentFixture, TestBed} from '@angular/core/testing';

import {DownloadsComponent} from './downloads.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {PageHeaderStubComponent} from "@app/shared/components/page-header/page-header.component.stub";
import {TableComponent} from "@app/shared/components/table/table.component";
import {LoadedContentStubComponent} from "@app/shared/components/loaded-content/loaded-content.component.stub";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {Client} from "@stomp/stompjs";
import {ReplaySubject} from "rxjs";
import {messageCallbackType} from "@stomp/stompjs/src/types";
import {StompHeaders} from "@stomp/stompjs/src/stomp-headers";
import {DownloadsClient, FileDownloadMessageTopics, FileDownloadProgress, GameFileVersion} from "@backend";
import {By} from "@angular/platform-browser";
import {TableColumnDirective} from "@app/shared/components/table/column-directive/table-column.directive";

describe('DownloadsComponent', () => {
  let component: DownloadsComponent;
  let fixture: ComponentFixture<DownloadsComponent>;
  let startSubscriptions: Function[];
  let progressSubscriptions: Function[];
  let finishSubscriptions: Function[];
  const downloadsClientMock = {
    currentlyDownloading: undefined,
    processedFiles: undefined,
    queueItems: undefined,
    getCurrentlyDownloading(page?: number, size?: number, sort?: string[]): any {
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
    downloadsClientMock.currentlyDownloading = undefined;
    downloadsClientMock.processedFiles = undefined;
    downloadsClientMock.queueItems = undefined;
    const clientMock: Client = {
      subscribe: (destination: string, callback: messageCallbackType, headers: StompHeaders = {}
      ): any => {
        if (destination == FileDownloadMessageTopics.Started) {
          startSubscriptions.push(callback);
        }
        if (destination == FileDownloadMessageTopics.Progress) {
          progressSubscriptions.push(callback);
        }
        if (destination == FileDownloadMessageTopics.Finished) {
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
        DownloadsComponent,
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
          provide: DownloadsClient,
          useValue: downloadsClientMock
        }
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DownloadsComponent);
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
    const expectedDownload: GameFileVersion = {
      title: 'someDownload'
    };
    startSubscriptions[0]({body: JSON.stringify(expectedDownload)})
    expect(component.currentDownload).toEqual(expectedDownload);
  });

  it('should set download progress', () => {
    const expectedProgress: FileDownloadProgress = {
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
    downloadsClientMock.queueItems = {
      content: [
        {
          gameTitle: "Some queued game",
          name: "queuedGame.exe",
          size: "1 GB"
        }
      ]
    };
    downloadsClientMock.processedFiles = {
      content: [
        {
          gameTitle: "Some processed game",
          name: "processedGame.exe",
          size: "2 GB"
        }
      ]
    };
    downloadsClientMock.currentlyDownloading = {
      gameTitle: "Some current game",
      name: "currentGame.exe",
      size: "3 GB"
    };

    component.refresh();
    fixture.detectChanges();

    expect(component.enqueuedDownloads).toBe(downloadsClientMock.queueItems);
    expect(component.processedFiles).toBe(downloadsClientMock.processedFiles);
    expect(component.currentDownload).toBe(downloadsClientMock.currentlyDownloading);

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
