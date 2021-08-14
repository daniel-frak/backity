import {Component, OnDestroy, OnInit} from '@angular/core';
import {DiscoveredFile, DownloadsClient, FileDiscoveryClient, MessageTopics, PageDiscoveredFile} from "@backend";
import {MessagesService} from "@app/backend/services/messages.service";
import {StompSubscription} from "@stomp/stompjs/esm6/stomp-subscription";

@Component({
  selector: 'app-file-discovery',
  templateUrl: './file-discovery.component.html',
  styleUrls: ['./file-discovery.component.scss']
})
export class FileDiscoveryComponent implements OnInit, OnDestroy {

  private pageSize = 20;
  discoveredFiles?: PageDiscoveredFile;
  newestDiscovered?: DiscoveredFile;
  newDiscoveredCount: number = 0;
  public filesAreLoading: boolean = false;
  public discoveryOngoing: boolean = false;
  private stompSubscriptions: StompSubscription[] = [];

  constructor(private readonly fileDiscoveryClient: FileDiscoveryClient,
              private readonly downloadsClient: DownloadsClient,
              private readonly messageService: MessagesService) {
  }

  ngOnInit(): void {
    this.refresh();

    this.messageService.onConnect(client => this.stompSubscriptions.push(
      client.subscribe(MessageTopics.FileDiscovery, (payload) => {
        this.newestDiscovered = JSON.parse(payload.body);
        this.newDiscoveredCount++;
      })));
  }

  refresh() {
    this.filesAreLoading = true;
    const page = 0;
    const size = this.pageSize;
    const sort = ["dateCreated,desc"];
    this.fileDiscoveryClient.getDiscoveredFiles(page, size, sort).subscribe(df => {
      this.discoveredFiles = df;
      this.newDiscoveredCount = 0;
      this.filesAreLoading = false;
    });
  }

  discoverFiles() {
    this.fileDiscoveryClient.discover().subscribe(() => {});
  }

  enqueueFile(id?: string) {
    this.downloadsClient.download(id as string).subscribe();
  }

  ngOnDestroy(): void {
    this.stompSubscriptions.forEach(s => s.unsubscribe());
  }
}
