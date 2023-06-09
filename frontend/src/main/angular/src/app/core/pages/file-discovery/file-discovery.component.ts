import {Component, OnDestroy, OnInit} from '@angular/core';
import {
  BackupsClient,
  FileBackupStatus,
  FileDiscoveryClient,
  FileDiscoveryMessageTopics,
  FileDiscoveryProgress,
  FileDiscoveryStatus,
  GameFileVersionBackup,
  PageGameFileVersionBackup
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

  discoveredFiles?: PageGameFileVersionBackup;
  newestDiscovered?: GameFileVersionBackup;
  newDiscoveredCount: number = 0;
  infoIsLoading: boolean = false;
  filesAreLoading: boolean = false;
  discoveryStatusBySource: Map<string, boolean> = new Map<string, boolean>();
  discoveryProgressBySource: Map<string, FileDiscoveryProgress> = new Map<string, FileDiscoveryProgress>();
  discoveryStateUnknown: boolean = true;

  private pageSize = 20;
  private readonly stompSubscriptions: StompSubscription[] = [];

  constructor(private readonly fileDiscoveryClient: FileDiscoveryClient,
              private readonly BackupsClient: BackupsClient,
              private readonly messageService: MessagesService) {
  }

  asFile = (file: GameFileVersionBackup) => file;

  ngOnInit(): void {
    this.messageService.onConnect(client => this.stompSubscriptions.push(
      client.subscribe(FileDiscoveryMessageTopics.Discovery, p => this.onGameFileVersionReceived(p)),
      client.subscribe(FileDiscoveryMessageTopics.DiscoveryStatus, p => this.onDiscoveryStatusChanged(p)),
      client.subscribe(FileDiscoveryMessageTopics.DiscoveryProgress, p => this.onProgressUpdated(p))
    ))

    this.refreshInfo();

    this.refreshDiscoveredFiles();
  }

  private onGameFileVersionReceived(payload: IMessage) {
    this.newestDiscovered = JSON.parse(payload.body);
    this.newDiscoveredCount++;
  }

  private onDiscoveryStatusChanged(payload: IMessage) {
    const status: FileDiscoveryStatus = JSON.parse(payload.body);
    this.updateDiscoveryStatus(status);
  }

  private onProgressUpdated(payload: IMessage) {
    const progress: FileDiscoveryProgress = JSON.parse(payload.body);
    this.discoveryProgressBySource.set(progress.source as string, progress);
  }

  private updateDiscoveryStatus(status: FileDiscoveryStatus) {
    this.discoveryStatusBySource.set(status.source as string, status.inProgress as boolean);
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
    const sort = ["dateCreated,desc"];
    this.fileDiscoveryClient.getDiscoveredFiles(page, size, sort)
      .subscribe(df => this.updateDiscoveredFiles(df));
  }

  private updateDiscoveredFiles(df: PageGameFileVersionBackup) {
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

  enqueueFile(file: GameFileVersionBackup) {
    file.status = FileBackupStatus.Enqueued;
    console.info("Enqueuing: " + file.id);
    this.BackupsClient.download(file.id as number)
      .pipe(catchError(e => {
        file.status = FileBackupStatus.Discovered;
        return throwError(e);
      }))
      .subscribe(() => {
      }, err => console.error(`An error occurred while trying to enqueue a file (${file})`,
        file, err));
  }

  ngOnDestroy(): void {
    this.stompSubscriptions.forEach(s => s.unsubscribe());
  }

  getStatuses(): FileDiscoveryStatus[] {
    if (this.discoveryStatusBySource.size === 0) {
      return [];
    }

    return Array.from(this.discoveryStatusBySource)
      .map(([source, inProgress]) => {
        return {
          source: source,
          inProgress: inProgress
        };
      });
  }

  getProgressList(): FileDiscoveryProgress[] {
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
