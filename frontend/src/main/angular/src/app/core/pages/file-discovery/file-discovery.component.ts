import {Component, OnDestroy, OnInit} from '@angular/core';
import {
  DiscoveredFile,
  DownloadsClient,
  FileDiscoveryClient,
  FileDiscoveryMessageTopics,
  PageDiscoveredFile
} from "@backend";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {StompSubscription} from "@stomp/stompjs/esm6/stomp-subscription";
import {IMessage} from "@stomp/stompjs";
import {TableColumn} from "@app/shared/components/table/table-column";

@Component({
  selector: 'app-file-discovery',
  templateUrl: './file-discovery.component.html',
  styleUrls: ['./file-discovery.component.scss']
})
export class FileDiscoveryComponent implements OnInit, OnDestroy {

  discoveredFiles?: PageDiscoveredFile;
  newestDiscovered?: DiscoveredFile;
  newDiscoveredCount: number = 0;
  filesAreLoading: boolean = false;
  discoveryOngoing: boolean = false;

  private pageSize = 20;
  private stompSubscriptions: StompSubscription[] = [];

  columns: TableColumn[] = [
    {
      title: "Game title"
    },
    {
      title: "File name"
    },
    {
      title: "Size"
    },
    {
      title: "Actions",
      hideTitleOnMobile: true,
      class: "text-end"
    },
  ]

  constructor(private readonly fileDiscoveryClient: FileDiscoveryClient,
              private readonly downloadsClient: DownloadsClient,
              private readonly messageService: MessagesService) {
  }

  asFile(file: DiscoveredFile): DiscoveredFile {
    return file
  };

  ngOnInit(): void {
    this.messageService.onConnect(client => this.stompSubscriptions.push(
      client.subscribe(FileDiscoveryMessageTopics.TopicFileDiscovery, p => this.onDiscoveredFileReceived(p))
    ))

    this.refresh();
  }

  private onDiscoveredFileReceived(payload: IMessage) {
    this.newestDiscovered = JSON.parse(payload.body);
    this.newDiscoveredCount++;
  }

  refresh() {
    this.filesAreLoading = true;
    const page = 0;
    const size = this.pageSize;
    const sort = ["dateCreated,desc"];
    this.fileDiscoveryClient.getDiscoveredFiles(page, size, sort)
      .subscribe(df => this.updateDiscoveredFiles(df));
  }

  private updateDiscoveredFiles(df: PageDiscoveredFile) {
    this.discoveredFiles = df;
    this.newDiscoveredCount = 0;
    this.filesAreLoading = false;
  }

  discoverFiles() {
    this.fileDiscoveryClient.discover().subscribe(() => {
    });
  }

  enqueueFile(id?: string) {
    this.downloadsClient.download(id as string).subscribe();
  }

  ngOnDestroy(): void {
    this.stompSubscriptions.forEach(s => s.unsubscribe());
  }
}
