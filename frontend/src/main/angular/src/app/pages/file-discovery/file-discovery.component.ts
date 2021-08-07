import {Component, OnDestroy, OnInit} from '@angular/core';
import {DiscoveredFileId, FileDiscoveryService, Pageable, PageDiscoveredFile} from "../../../backend";
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

  constructor(private readonly fileDiscoveryService: FileDiscoveryService,
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
      page: 0
    };
    this.fileDiscoveryService.getDiscoveredFiles(pageable).subscribe(df => {
      this.discoveredFiles = df;
      this.filesAreLoading = false;
    });
  }

  discoverFiles() {
    this.fileDiscoveryService.discover().subscribe(() => console.info("Finished discovering"));
  }

  enqueueFile(id?: DiscoveredFileId) {

  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(s => s.unsubscribe());
  }
}
