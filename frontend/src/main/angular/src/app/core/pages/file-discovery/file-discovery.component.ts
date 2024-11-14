import {Component, OnDestroy, OnInit} from '@angular/core';
import {
  FileBackupStatus,
  FileDetails,
  FileDetailsClient,
  FileDiscoveredMessage,
  FileDiscoveryClient,
  FileDiscoveryWebSocketTopics,
  FileDiscoveryProgressUpdateMessage,
  FileDiscoveryStatus,
  FileDiscoveryStatusChangedMessage,
  PageFileDetails
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

  discoveredFiles?: PageFileDetails;
  newestDiscovered?: FileDiscoveredMessage;
  newDiscoveredCount: number = 0;
  infoIsLoading: boolean = false;
  filesAreLoading: boolean = false;
  discoveryStatusBySource: Map<string, boolean> = new Map<string, boolean>();
  discoveryProgressBySource: Map<string, FileDiscoveryProgressUpdateMessage>
    = new Map<string, FileDiscoveryProgressUpdateMessage>();
  discoveryStateUnknown: boolean = true;

  private readonly pageSize = 20;
  private readonly stompSubscriptions: StompSubscription[] = [];

  constructor(private readonly fileDiscoveryClient: FileDiscoveryClient,
              private readonly fileDetailsClient: FileDetailsClient,
              private readonly messageService: MessagesService) {
  }

  asFile = (file: FileDetails) => file;

  ngOnInit(): void {
    this.messageService.onConnect(client => this.stompSubscriptions.push(
      client.subscribe(FileDiscoveryWebSocketTopics.FileDiscovered, p => this.onFileDiscovered(p)),
      client.subscribe(FileDiscoveryWebSocketTopics.FileStatusChanged, p => this.onDiscoveryStatusChanged(p)),
      client.subscribe(FileDiscoveryWebSocketTopics.ProgressUpdate, p => this.onProgressUpdated(p))
    ))

    this.refreshInfo();

    this.refreshDiscoveredFiles();
  }

  private onFileDiscovered(payload: IMessage) {
    this.newestDiscovered = JSON.parse(payload.body);
    this.newDiscoveredCount++;
  }

  private onDiscoveryStatusChanged(payload: IMessage) {
    const status: FileDiscoveryStatusChangedMessage = JSON.parse(payload.body);
    this.updateDiscoveryStatus(status);
  }

  private onProgressUpdated(payload: IMessage) {
    const progress: FileDiscoveryProgressUpdateMessage = JSON.parse(payload.body);
    this.discoveryProgressBySource.set(progress.source as string, progress);
  }

  private updateDiscoveryStatus(status: FileDiscoveryStatusChangedMessage) {
    this.discoveryStatusBySource.set(status.source as string, status.isInProgress as boolean);
    this.discoveryStateUnknown = false;
  }

  private refreshInfo() {
    this.infoIsLoading = true;
    this.fileDiscoveryClient.getStatuses()
      .subscribe(ss => {
        ss.forEach((s) => this.updateDiscoveryStatus(s))
        this.infoIsLoading = false;
      });
  }

  refreshDiscoveredFiles() {
    this.filesAreLoading = true;
    const page = 0;
    const size = this.pageSize;
    this.fileDetailsClient.getDiscoveredFiles({
      page: page,
      size: size
    })
      .subscribe(df => this.updateDiscoveredFiles(df));
  }

  private updateDiscoveredFiles(df: PageFileDetails) {
    this.discoveredFiles = df;
    this.newDiscoveredCount = 0;
    this.filesAreLoading = false;
  }

  startDiscovery() {
    this.discoveryStateUnknown = true;
    this.fileDiscoveryClient.discover().subscribe(() => {
    });
  }

  stopDiscovery() {
    this.discoveryStateUnknown = true;
    this.fileDiscoveryClient.stopDiscovery().subscribe(() => {
    });
  }

  enqueueFile(file: FileDetails) {
    file.backupDetails.status = FileBackupStatus.Enqueued;
    console.info("Enqueuing: " + file.id);
    this.fileDetailsClient.download(file.id)
      .pipe(catchError(e => {
        file.backupDetails.status = FileBackupStatus.Discovered;
        return throwError(e);
      }))
      .subscribe(() => {
      }, (err: any) => console.error(
        `An error occurred while trying to enqueue a file (id=${file.id})`, file, err));
  }

  ngOnDestroy(): void {
    this.stompSubscriptions.forEach(s => s.unsubscribe());
  }

  getStatuses(): FileDiscoveryStatus[] {
    if (this.discoveryStatusBySource.size === 0) {
      return [];
    }

    return Array.from(this.discoveryStatusBySource)
      .map(([source, isInProgress]) => {
        return {
          source: source,
          isInProgress: isInProgress
        };
      });
  }

  getProgressList(): FileDiscoveryProgressUpdateMessage[] {
    if (this.discoveryProgressBySource.size === 0) {
      return [];
    }
    return Array.from(this.discoveryProgressBySource)
      .map(([source, progress]) => {
        return progress;
      });
  }

  discoveryOngoing(): boolean {
    if (this.discoveryStatusBySource.size === 0) {
      return false;
    }

    return Array.from(this.discoveryStatusBySource)
      .some(([source, inProgress]) => inProgress);
  }

  discoverFilesFor(source?: string) {
    console.error("Per-source file discovery start not yet implemented");
  }

  isInProgress(source: string): boolean {
    return !!this.discoveryStatusBySource.get(source);
  }
}
