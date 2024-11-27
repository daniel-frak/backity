import {Component, OnDestroy, OnInit} from '@angular/core';
import {
  FileBackupStatus,
  FileDiscoveredEvent,
  FileDiscoveryClient,
  FileDiscoveryProgressUpdateEvent,
  FileDiscoveryStatus,
  FileDiscoveryStatusChangedEvent,
  FileDiscoveryWebSocketTopics,
  GameFile,
  GameFileProcessingStatus,
  GameFilesClient,
  PageGameFile
} from "@backend";
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {StompSubscription} from "@stomp/stompjs/esm6/stomp-subscription";
import {IMessage} from "@stomp/stompjs";
import {catchError} from "rxjs/operators";
import {firstValueFrom} from "rxjs";

@Component({
  selector: 'app-file-discovery',
  templateUrl: './file-discovery.component.html',
  styleUrls: ['./file-discovery.component.scss']
})
export class FileDiscoveryComponent implements OnInit, OnDestroy {

  discoveredFiles?: PageGameFile;
  newestDiscovered?: FileDiscoveredEvent;
  newDiscoveredCount: number = 0;
  infoIsLoading: boolean = false;
  filesAreLoading: boolean = false;
  discoveryStatusByGameProviderId: Map<string, boolean> = new Map<string, boolean>();
  discoveryProgressByGameProviderId: Map<string, FileDiscoveryProgressUpdateEvent>
    = new Map<string, FileDiscoveryProgressUpdateEvent>();
  discoveryStateUnknown: boolean = true;

  private readonly pageSize = 20;
  private readonly stompSubscriptions: StompSubscription[] = [];

  constructor(private readonly fileDiscoveryClient: FileDiscoveryClient,
              private readonly gameFilesClient: GameFilesClient,
              private readonly messageService: MessagesService) {
  }

  asGameFile = (gameFile: GameFile) => gameFile;

  ngOnInit(): void {
    this.messageService.onConnect(client => this.stompSubscriptions.push(
      client.subscribe(FileDiscoveryWebSocketTopics.FileDiscovered, p => this.onFileDiscovered(p)),
      client.subscribe(FileDiscoveryWebSocketTopics.FileStatusChanged, p => this.onDiscoveryStatusChanged(p)),
      client.subscribe(FileDiscoveryWebSocketTopics.ProgressUpdate, p => this.onProgressUpdated(p))
    ))

    this.refreshInfo();

    this.refreshDiscoveredFiles()().then(() => {
      // Do nothing
    });
  }

  private onFileDiscovered(payload: IMessage) {
    this.newestDiscovered = JSON.parse(payload.body);
    this.newDiscoveredCount++;
  }

  private onDiscoveryStatusChanged(payload: IMessage) {
    const status: FileDiscoveryStatusChangedEvent = JSON.parse(payload.body);
    this.updateDiscoveryStatus(status);
  }

  private onProgressUpdated(payload: IMessage) {
    const progress: FileDiscoveryProgressUpdateEvent = JSON.parse(payload.body);
    this.discoveryProgressByGameProviderId.set(progress.gameProviderId as string, progress);
  }

  private updateDiscoveryStatus(status: FileDiscoveryStatusChangedEvent) {
    this.discoveryStatusByGameProviderId.set(status.gameProviderId as string, status.isInProgress as boolean);
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

  refreshDiscoveredFiles(): () => Promise<void> {
    return async () => {
      this.filesAreLoading = true;
      const page = 0;
      const size = this.pageSize;
      try {
        const gameFilePage = await firstValueFrom(
          this.gameFilesClient.getGameFiles(GameFileProcessingStatus.Discovered, {
            page: page,
            size: size
          }));
        this.updateDiscoveredFiles(gameFilePage);
      } catch (error) {
        console.error('Error fetching discovered files:', error);
      } finally {
        this.filesAreLoading = false;
      }
    }
  }

  private updateDiscoveredFiles(gameFilePage: PageGameFile) {
    this.discoveredFiles = gameFilePage;
    this.newDiscoveredCount = 0;
    this.filesAreLoading = false;
  }

  startDiscovery(): () => Promise<void> {
    return async () => {
      this.discoveryStateUnknown = true;
      try {
        await firstValueFrom(this.fileDiscoveryClient.startDiscovery());
      } catch (error) {
        console.error('Error starting discovery:', error);
      }
    };
  }

  stopDiscovery(): () => Promise<void> {
    return async () => {
      this.discoveryStateUnknown = true;
      try {
        await firstValueFrom(this.fileDiscoveryClient.stopDiscovery());
      } catch (error) {
        console.error('Error stopping discovery:', error);
      }
    };
  }

  enqueueFile(gameFile: GameFile): () => Promise<void> {
    return async () => {
      gameFile.fileBackup.status = FileBackupStatus.Enqueued;
      console.info("Enqueuing: " + gameFile.id);
      try {
        await firstValueFrom(this.gameFilesClient.enqueueFileBackup(gameFile.id).pipe(catchError(e => {
          gameFile.fileBackup.status = FileBackupStatus.Discovered;
          throw e;
        })));
      } catch (err) {
        console.error(`An error occurred while trying to enqueue a file (id=${gameFile.id})`, gameFile, err);
      }
    }
  }

  ngOnDestroy(): void {
    this.stompSubscriptions.forEach(s => s.unsubscribe());
  }

  getStatuses(): FileDiscoveryStatus[] {
    if (this.discoveryStatusByGameProviderId.size === 0) {
      return [];
    }

    return Array.from(this.discoveryStatusByGameProviderId)
      .map(([gameProviderId, isInProgress]) => {
        return {
          gameProviderId: gameProviderId,
          isInProgress: isInProgress
        };
      });
  }

  getProgressList(): FileDiscoveryProgressUpdateEvent[] {
    if (this.discoveryProgressByGameProviderId.size === 0) {
      return [];
    }
    return Array.from(this.discoveryProgressByGameProviderId)
      .map(([gameProviderId, progress]) => {
        return progress;
      });
  }

  discoveryOngoing(): boolean {
    if (this.discoveryStatusByGameProviderId.size === 0) {
      return false;
    }

    return Array.from(this.discoveryStatusByGameProviderId)
      .some(([gameProviderId, inProgress]) => inProgress);
  }

  discoverFilesFor(gameProviderId?: string): () => Promise<void> {
    return async () => {
      console.error("Per-provider file discovery start not yet implemented");
    };
  }

  isInProgress(gameProviderId: string): boolean {
    return !!this.discoveryStatusByGameProviderId.get(gameProviderId);
  }

  protected readonly FileBackupStatus = FileBackupStatus;
}
