import {Component, OnDestroy, OnInit} from '@angular/core';
import {
  DiscoveredFile,
  DownloadsClient,
  FileDiscoveryClient,
  FileDiscoveryMessageTopics,
  FileDiscoveryStatus,
  PageDiscoveredFile
} from "@backend";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {StompSubscription} from "@stomp/stompjs/esm6/stomp-subscription";
import {IMessage} from "@stomp/stompjs";
import {catchError} from "rxjs/operators";
import {throwError} from "rxjs";

@Component({
  selector: 'app-file-discovery',
  templateUrl: './file-discovery.component.html',
  styleUrls: ['./file-discovery.component.scss']
})
export class FileDiscoveryComponent implements OnInit, OnDestroy {

  discoveredFiles?: PageDiscoveredFile;
  newestDiscovered?: DiscoveredFile;
  newDiscoveredCount: number = 0;
  infoIsLoading: boolean = false;
  filesAreLoading: boolean = false;
  discoveryStatuses: Map<string, boolean> = new Map<string, boolean>();

  private pageSize = 20;
  private readonly stompSubscriptions: StompSubscription[] = [];

  constructor(private readonly fileDiscoveryClient: FileDiscoveryClient,
              private readonly downloadsClient: DownloadsClient,
              private readonly messageService: MessagesService) {
  }

  asFile = (file: DiscoveredFile) => file;

  ngOnInit(): void {
    this.messageService.onConnect(client => this.stompSubscriptions.push(
      client.subscribe(FileDiscoveryMessageTopics.Discovery, p => this.onDiscoveredFileReceived(p)),
      client.subscribe(FileDiscoveryMessageTopics.DiscoveryStatus, p => this.onStatusChanged(p))
    ))

    this.refreshInfo();

    this.refreshDiscoveredFiles();
  }

  private onDiscoveredFileReceived(payload: IMessage) {
    this.newestDiscovered = JSON.parse(payload.body);
    this.newDiscoveredCount++;
  }

  private onStatusChanged(payload: IMessage) {
    const status: FileDiscoveryStatus = JSON.parse(payload.body);
    this.updateDiscoveryStatus(status);
  }

  private refreshInfo() {
    this.infoIsLoading = true;
    this.fileDiscoveryClient.getStatuses()
      .subscribe(ss => {
        ss.forEach((s) => this.updateDiscoveryStatus(s))
        this.infoIsLoading = false;
      });
  }

  private updateDiscoveryStatus(status: FileDiscoveryStatus) {
    this.discoveryStatuses.set(status.source as string, status.inProgress as boolean);
  }

  refreshDiscoveredFiles() {
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

  enqueueFile(file: DiscoveredFile) {
    file.enqueued = true;
    console.info("Enqueuing: " + file.id);
    this.downloadsClient.download(file.id as string)
      .pipe(catchError(e => {
        file.enqueued = false;
        return throwError(e);
      }))
      .subscribe(() => {}, err => console.error(`An error occurred while trying to enqueue a file (${file})`,
        file, err));
  }

  ngOnDestroy(): void {
    this.stompSubscriptions.forEach(s => s.unsubscribe());
  }

  getStatuses(): FileDiscoveryStatus[] {
    if (this.discoveryStatuses.size === 0) {
      return [];
    }

    return Array.from(this.discoveryStatuses)
      .map(([source, inProgress]) => {
        return {
          source: source,
          inProgress: inProgress
        };
      });
  }

  discoveryOngoing(): boolean {
    if (this.discoveryStatuses.size === 0) {
      return false;
    }

    return Array.from(this.discoveryStatuses)
      .some(([source, inProgress]) => inProgress);
  }

  discoverFilesFor(source?: string) {
    console.error("Per-source file discovery start not yet implemented");
  }
}
