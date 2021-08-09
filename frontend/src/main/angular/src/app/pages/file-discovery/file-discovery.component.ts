import {Component, OnDestroy, OnInit} from '@angular/core';
import {DownloadsClient, FileDiscoveryClient, Pageable, PageDiscoveredFile} from "../../../backend";
import {MessagesService} from "../../backend/services/messages.service";
import {StompSubscription} from "@stomp/stompjs/esm6/stomp-subscription";

@Component({
  selector: 'app-file-discovery',
  templateUrl: './file-discovery.component.html',
  styleUrls: ['./file-discovery.component.scss']
})
export class FileDiscoveryComponent implements OnInit, OnDestroy {

  private pageSize = 20;
  discoveredFiles?: PageDiscoveredFile;
  public filesAreLoading: boolean = false;
  public discoveryOngoing: boolean = false;
  private subscriptions: StompSubscription[] = [];

  constructor(private readonly fileDiscoveryClient: FileDiscoveryClient,
              private readonly downloadsClient: DownloadsClient,
              private readonly messageService: MessagesService) {
  }

  ngOnInit(): void {
    this.refresh();

    this.messageService.onConnect(client => this.subscriptions.push(
      client.subscribe('/topic/file-discovery', (payload) => {
        if (this.discoveredFiles?.content) {
          this.discoveredFiles.content.unshift(JSON.parse(payload.body));
          if (this.discoveredFiles.size && this.discoveredFiles.size > 0
            && this.discoveredFiles.content.length > this.pageSize) {
            this.discoveredFiles.content.pop();
          }
        }
      })));
  }

  refresh() {
    this.filesAreLoading = true;
    const pageable: Pageable = {
      size: this.pageSize,
      page: 0,
      sort: ["dateCreated,desc"]
    };
    this.fileDiscoveryClient.getDiscoveredFiles(pageable).subscribe(df => {
      this.discoveredFiles = df;
      this.filesAreLoading = false;
    });
  }

  discoverFiles() {
    this.fileDiscoveryClient.discover().subscribe(() => console.info("Finished discovering"));
  }

  enqueueFile(id?: string) {
    this.downloadsClient.download(id as string).subscribe();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(s => s.unsubscribe());
  }
}
