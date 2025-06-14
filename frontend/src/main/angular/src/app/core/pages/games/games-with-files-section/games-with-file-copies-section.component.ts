import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {
  BackupTarget,
  BackupTargetsClient,
  EnqueueFileCopyRequest,
  FileBackupMessageTopics,
  FileCopiesClient,
  FileCopyNaturalId,
  FileCopyStatus,
  FileCopyStatusChangedEvent,
  FileCopyWithProgress,
  FileDownloadProgressUpdatedEvent,
  GameFile,
  GameFileWithCopies,
  GamesClient,
  GameWithFileCopies,
  PageGameWithFileCopies,
  StorageSolutionsClient,
  StorageSolutionStatus,
  StorageSolutionStatusesResponse
} from "@backend";
import {catchError} from "rxjs/operators";
import {firstValueFrom, Subscription} from "rxjs";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {ModalService} from "@app/shared/services/modal-service/modal.service";
import {ButtonComponent} from '@app/shared/components/button/button.component';
import {LoadedContentComponent} from '@app/shared/components/loaded-content/loaded-content.component';
import {DatePipe, NgForOf, NgIf} from '@angular/common';
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {Message} from "@stomp/stompjs";
import {PaginationComponent} from "@app/shared/components/pagination/pagination.component";
import {
  FileCopyStatusBadgeComponent
} from "@app/core/components/file-copy-status-badge/file-copy-status-badge.component";
import {
  PotentialFileCopyWithContext
} from "@app/core/pages/games/games-with-files-section/potential-file-copy-with-context";
import {
  PotentialFileCopy,
  PotentialFileCopyFactory
} from "@app/core/pages/games/games-with-files-section/potential-file-copy";
import {SectionComponent} from "@app/shared/components/section/section.component";
import {ProgressBarComponent} from "@app/shared/components/progress-bar/progress-bar.component";
import {IconItemComponent} from "@app/shared/components/icon-item/icon-item.component";
import {
  GameFileVersionBadgeComponent
} from "@app/core/components/game-file-version-badge/game-file-version-badge.component";
import {NamedValueComponent} from "@app/shared/components/named-value/named-value.component";
import {
  StorageSolutionStatusBadgeComponent
} from "@app/core/components/storage-solution-status-badge/storage-solution-status-badge.component";
import {AutoLayoutComponent} from "@app/shared/components/auto-layout/auto-layout.component";
import {
  NamedValueContainerComponent
} from "@app/shared/components/named-value-container/named-value-container.component";

@Component({
  selector: 'app-games-with-file-copies-section',
  standalone: true,
  imports: [
    ButtonComponent,
    FileCopyStatusBadgeComponent,
    LoadedContentComponent,
    PaginationComponent,
    NgIf,
    SectionComponent,
    ProgressBarComponent,
    IconItemComponent,
    NgForOf,
    GameFileVersionBadgeComponent,
    NamedValueComponent,
    DatePipe,
    StorageSolutionStatusBadgeComponent,
    AutoLayoutComponent,
    NamedValueContainerComponent
  ],
  templateUrl: './games-with-file-copies-section.component.html',
  styleUrl: './games-with-file-copies-section.component.scss'
})
export class GamesWithFileCopiesSectionComponent implements OnInit, OnDestroy {

  FileCopyStatus = FileCopyStatus;

  gamesAreLoading: boolean = true;
  storageSolutionStatusesById: Map<string, StorageSolutionStatus> = new Map();
  gameWithFileCopiesPage?: PageGameWithFileCopies;
  potentialFileCopiesWithContextByGameFileId: Map<string, PotentialFileCopyWithContext[]> =
    new Map<string, PotentialFileCopyWithContext[]>();

  backupTargets: Array<BackupTarget> = [];
  pageNumber: number = 1;
  pageSize: number = 3;

  private readonly subscriptions: Subscription[] = [];

  readonly refreshAction = () => this.refresh();

  constructor(private readonly gamesClient: GamesClient,
              private readonly fileCopiesClient: FileCopiesClient,
              private readonly backupTargetsClient: BackupTargetsClient,
              private readonly storageSolutionsClient: StorageSolutionsClient,
              private readonly messageService: MessagesService,
              private readonly notificationService: NotificationService,
              private readonly modalService: ModalService,
              @Inject('Window') private readonly window: Window) {
  }

  ngOnInit(): void {
    this.subscriptions.push(
      this.messageService.watch(FileBackupMessageTopics.StatusChanged)
        .subscribe(p => this.onStatusChanged(p)),
      this.messageService.watch(FileBackupMessageTopics.ProgressUpdate)
        .subscribe(p => this.onReplicationProgressChanged(p))
    );
  }

  private onStatusChanged(payload: Message) {
    const event: FileCopyStatusChangedEvent = JSON.parse(payload.body);
    const potentialFileCopyWithContext: PotentialFileCopyWithContext | undefined =
      this.findPotentialFileCopyWithContext(event.fileCopyNaturalId);

    if (potentialFileCopyWithContext) {
      potentialFileCopyWithContext.potentialFileCopy.id = event.fileCopyId;
      potentialFileCopyWithContext.potentialFileCopy.status = event.newStatus as FileCopyStatus;
      potentialFileCopyWithContext.progress = undefined;
      console.debug("Updated potential file copy", event, potentialFileCopyWithContext);
    } else {
      console.warn("Could not find potential file copy", event, this.potentialFileCopiesWithContextByGameFileId);
    }
  }

  private findPotentialFileCopyWithContext(fileCopyNaturalId: FileCopyNaturalId):
    PotentialFileCopyWithContext | undefined {
    return Array.from(this.potentialFileCopiesWithContextByGameFileId?.values())
      .flat()
      .find((potentialFileCopyWithContext) => {
        return this.fileCopyNaturalIdsAreEqual(
          potentialFileCopyWithContext.potentialFileCopy.naturalId, fileCopyNaturalId);
      });
  }

  private fileCopyNaturalIdsAreEqual(a: FileCopyNaturalId, b: FileCopyNaturalId): boolean {
    return Object.entries(a)
      .every(([key, value]) => b[key as keyof FileCopyNaturalId] === value);
  }

  private onReplicationProgressChanged(payload: Message) {
    const event: FileDownloadProgressUpdatedEvent = JSON.parse(payload.body);
    const inProgressFileCopyWithContext: PotentialFileCopyWithContext | undefined =
      this.findPotentialFileCopyWithContext(event.fileCopyNaturalId);

    if (inProgressFileCopyWithContext) {
      inProgressFileCopyWithContext.progress = {
        percentage: event.percentage,
        timeLeftSeconds: event.timeLeftSeconds,
      };
    } else {
      console.warn("Could not find potential file copy", event,
        this.potentialFileCopiesWithContextByGameFileId);
    }
  }

  async refresh(): Promise<void> {
    try {
      this.gamesAreLoading = true;
      await this.getBackupTargets();
      await this.getStorageSolutionStatuses();
      await this.getGamesWithFileCopies();
    } catch (error) {
      this.notificationService.showFailure('Error fetching games', error);
    } finally {
      this.gamesAreLoading = false;
    }
  }

  private async getBackupTargets() {
    this.backupTargets = await firstValueFrom(this.backupTargetsClient.getBackupTargets());
  }

  private async getStorageSolutionStatuses() {
    const storageSolutionStatusesResponse: StorageSolutionStatusesResponse = await firstValueFrom(
      this.storageSolutionsClient.getStorageSolutionStatuses());
    this.storageSolutionStatusesById = new Map<string, StorageSolutionStatus>(
      Object.entries(storageSolutionStatusesResponse.statuses));
  }

  private async getGamesWithFileCopies() {
    this.gameWithFileCopiesPage = await firstValueFrom(this.gamesClient.getGames({
      page: this.pageNumber - 1,
      size: this.pageSize
    }));
    this.potentialFileCopiesWithContextByGameFileId =
      this.mapToPotentialFileCopiesWithContext(this.gameWithFileCopiesPage.content);
  }

  private mapToPotentialFileCopiesWithContext(gamesWithFileCopies: GameWithFileCopies[])
    : Map<string, PotentialFileCopyWithContext[]> {
    const resultMap = new Map<string, PotentialFileCopyWithContext[]>();

    for (const {gameFilesWithCopies} of gamesWithFileCopies) {
      for (const {gameFile, fileCopiesWithProgress} of gameFilesWithCopies) {
        const potentialFileCopiesWithContext = resultMap.get(gameFile.id) ?? [];
        resultMap.set(gameFile.id, potentialFileCopiesWithContext);

        for (const backupTarget of this.backupTargets) {
          const potentialCopy = this.createPotentialFileCopyWithContext(
            gameFile,
            fileCopiesWithProgress,
            backupTarget
          );
          potentialFileCopiesWithContext.push(potentialCopy);
        }
      }
    }

    return resultMap;
  }

  onClickEnqueueFileCopy(potentialFileCopy: PotentialFileCopy): () => Promise<void> {
    return async () => this.enqueueFileCopy(potentialFileCopy);
  }

  async enqueueFileCopy(potentialFileCopy: PotentialFileCopy): Promise<void> {
    try {
      const request: EnqueueFileCopyRequest = {
        fileCopyNaturalId: potentialFileCopy.naturalId
      };
      await firstValueFrom(this.fileCopiesClient.enqueueFileCopy(request)
        .pipe(catchError(e => {
          throw e;
        })));
      potentialFileCopy.status = FileCopyStatus.Enqueued;
      this.notificationService.showSuccess("File copy enqueued");
    } catch (error) {
      this.notificationService.showFailure(
        'An error occurred while trying to enqueue a file', potentialFileCopy, error);
    }
  }

  onClickCancelBackup(potentialFileCopy: PotentialFileCopy): () => Promise<void> {
    return async () => this.cancelBackup(potentialFileCopy);
  }

  async cancelBackup(potentialFileCopy: PotentialFileCopy): Promise<void> {
    try {
      await firstValueFrom(this.fileCopiesClient.cancelFileCopy(potentialFileCopy.id!)
        .pipe(catchError(e => {
          throw e;
        })));
      potentialFileCopy.status = FileCopyStatus.Tracked;

      const potentialFileCopyWithContext: PotentialFileCopyWithContext | undefined =
        this.findPotentialFileCopyWithContext(potentialFileCopy.naturalId);

      if (potentialFileCopyWithContext) {
        potentialFileCopyWithContext.progress = undefined;
      }
      this.notificationService.showSuccess("Backup cancelled");
    } catch (error) {
      this.notificationService.showFailure(
        'An error occurred while trying to cancel the backup', potentialFileCopy, error);
    }
  }

  onClickDeleteFileCopy(fileCopyId: string): () => Promise<void> {
    return async () => this.deleteFileCopy(fileCopyId);
  }

  async deleteFileCopy(fileCopyId: string): Promise<void> {
    try {
      await this.modalService.withConfirmationModal("Are you sure you want to delete the file copy?",
        async () => {
          await firstValueFrom(this.fileCopiesClient.deleteFileCopy(fileCopyId));
          this.notificationService.showSuccess('Deleted file copy');
          return this.refresh();
        });
    } catch (error) {
      this.notificationService.showFailure(
        'An error occurred while trying to delete a file copy', fileCopyId, error);
    }
  }

  onClickViewFilePath(fileCopyId: string): () => Promise<void> {
    return async () => this.viewFilePath(fileCopyId);
  }

  async viewFilePath(gameFileId: string): Promise<void> {
    this.notificationService.showFailure('Viewing file paths not yet implemented');
  }

  onClickDownload(fileCopyId: string): () => Promise<void> {
    return async () => this.download(fileCopyId);
  }

  async download(fileCopyId: string): Promise<void> {
    /*
    The file interaction here is hardcoded because there seems to be no easy way to use the auto-generated HttpClient
    code to show the download dialog before first downloading the entire file into memory.
     */
    const configuration = this.fileCopiesClient.configuration;
    this.window.location.href = `${configuration.basePath}/api/file-copies/${configuration.encodeParam({
      name: "fileCopyId", value: fileCopyId, in: "path", style: "simple", explode: false, dataType: "string",
      dataFormat: undefined
    })}`;
  }

  onClickViewError(gameFileId: string): () => Promise<void> {
    return async () => this.viewError(gameFileId);
  }

  async viewError(gameFileId: string): Promise<void> {
    this.notificationService.showFailure('Viewing errors not yet implemented');
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(s => s.unsubscribe());
  }

  private createPotentialFileCopyWithContext(
    gameFile: GameFile, fileCopiesWithProgress: FileCopyWithProgress[], backupTarget: BackupTarget
  ): PotentialFileCopyWithContext {
    const match = fileCopiesWithProgress.find(
      (fcwp) => fcwp.fileCopy.naturalId.backupTargetId === backupTarget.id);

    const potentialFileCopy: PotentialFileCopy = (match?.fileCopy as PotentialFileCopy) ??
      PotentialFileCopyFactory.missing(gameFile.id, backupTarget.id);
    return {
      gameFile,
      potentialFileCopy: potentialFileCopy,
      progress: match?.progress,
      backupTarget: backupTarget,
      storageSolutionStatus: this.storageSolutionStatusesById.get(backupTarget.storageSolutionId)
    };
  }

  trackByGameId(index: number, gameWithFileCopies: GameWithFileCopies): string {
    return gameWithFileCopies.id;
  }

  trackByGameFileId(index: number, gameFileWithCopies: GameFileWithCopies): string {
    return gameFileWithCopies.gameFile.id;
  }

  trackByFileCopyNaturalId(index: number, potentialFileCopyWithContext: PotentialFileCopyWithContext)
    : FileCopyNaturalId {
    return potentialFileCopyWithContext.potentialFileCopy.naturalId;
  }
}
