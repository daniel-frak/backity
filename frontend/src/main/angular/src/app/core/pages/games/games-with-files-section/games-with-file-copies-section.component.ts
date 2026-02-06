import {Component, Inject, OnInit, signal} from '@angular/core';
import {
  BackupTarget,
  BackupTargetsClient,
  EnqueueFileCopyRequest,
  FileBackupMessageTopics,
  FileCopiesClient,
  FileCopyNaturalId,
  FileCopyReplicationProgressUpdatedEvent,
  FileCopyStatus,
  FileCopyStatusChangedEvent,
  FileCopyWithProgress,
  GameFile,
  GameFileWithCopies,
  GamesClient,
  GameWithFileCopies,
  StorageSolutionsClient,
  StorageSolutionStatus,
  StorageSolutionStatusesResponse
} from "@backend";
import {catchError} from "rxjs/operators";
import {firstValueFrom} from "rxjs";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {ModalService} from "@app/shared/services/modal-service/modal.service";
import {ButtonComponent} from '@app/shared/components/button/button.component';
import {LoadedContentComponent} from '@app/shared/components/loaded-content/loaded-content.component';
import {DatePipe} from '@angular/common';
import {MessageService} from "@app/shared/backend/services/message.service";
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
import {InputComponent} from "@app/shared/components/form/input/input.component";
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {SelectComponent} from "@app/shared/components/select/select.component";
import {Page} from "@app/shared/components/table/page";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";

interface SearchForm {
  searchBox: FormControl<string>;
  fileCopyStatus: FormControl<FileCopyStatus | undefined>;
}

@Component({
  selector: 'app-games-with-file-copies-section',
  imports: [
    ButtonComponent,
    FileCopyStatusBadgeComponent,
    LoadedContentComponent,
    PaginationComponent,
    SectionComponent,
    ProgressBarComponent,
    IconItemComponent,
    GameFileVersionBadgeComponent,
    NamedValueComponent,
    DatePipe,
    StorageSolutionStatusBadgeComponent,
    AutoLayoutComponent,
    NamedValueContainerComponent,
    InputComponent,
    ReactiveFormsModule,
    SelectComponent,
    FormsModule
  ],
  templateUrl: './games-with-file-copies-section.component.html',
  styleUrl: './games-with-file-copies-section.component.scss'
})
export class GamesWithFileCopiesSectionComponent implements OnInit {

  FileCopyStatus = FileCopyStatus;

  fileCopyStatuses: Array<FileCopyStatus | undefined> = [
    undefined,
    FileCopyStatus.Tracked,
    FileCopyStatus.Enqueued,
    FileCopyStatus.InProgress,
    FileCopyStatus.Failed,
    FileCopyStatus.StoredIntegrityUnknown,
    FileCopyStatus.StoredIntegrityVerified
  ];

  gamesAreLoading = signal(false);
  storageSolutionStatusesById = signal<Map<string, StorageSolutionStatus>>(new Map());
  gameWithFileCopiesPage = signal<Page<GameWithFileCopies> | undefined>(undefined);
  potentialFileCopiesWithContext = signal<Map<string, PotentialFileCopyWithContext>>(
    new Map<string, PotentialFileCopyWithContext>());

  backupTargets = signal<Array<BackupTarget>>([]);
  pageNumber = signal(1);
  pageSize = signal(3);

  public searchForm: FormGroup<SearchForm> = new FormGroup(
    {
      searchBox: new FormControl<string>('', {nonNullable: true}),
      fileCopyStatus: new FormControl<FileCopyStatus | undefined>(undefined, {nonNullable: true}),
    },
    {
      updateOn: 'submit'
    }
  );

  constructor(private readonly gamesClient: GamesClient,
              private readonly fileCopiesClient: FileCopiesClient,
              private readonly backupTargetsClient: BackupTargetsClient,
              private readonly storageSolutionsClient: StorageSolutionsClient,
              private readonly messageService: MessageService,
              private readonly notificationService: NotificationService,
              private readonly modalService: ModalService,
              @Inject('Window') private readonly window: Window) {
    this.messageService.watch<FileCopyStatusChangedEvent>(FileBackupMessageTopics.TopicBackupsStatusChanged)
      .pipe(takeUntilDestroyed())
      .subscribe(event => this.onStatusChanged(event));
    this.messageService.watch<FileCopyReplicationProgressUpdatedEvent>(
      FileBackupMessageTopics.TopicBackupsProgressUpdate)
      .pipe(takeUntilDestroyed())
      .subscribe(event => this.onReplicationProgressChanged(event));
  }

  ngOnInit(): void {
    void this.refresh();
  }

  async refresh(): Promise<void> {
    if (this.gamesAreLoading()) {
      return;
    }
    this.gamesAreLoading.set(true);
    try {
      await Promise.all([
        this.getBackupTargets(),
        this.getStorageSolutionStatuses()
      ]);
      await this.getGamesWithFileCopies();
    } catch (error) {
      this.notificationService.showFailure('Error fetching games', error);
    } finally {
      this.gamesAreLoading.set(false);
    }
  }

  onClickEnqueueFileCopy(potentialFileCopy: PotentialFileCopy): () => Promise<void> {
    return async () => this.enqueueFileCopy(potentialFileCopy);
  }

  getPotentialFileCopyWithContext(gameFileId: string, backupTargetId: string):
    PotentialFileCopyWithContext | undefined {
    const naturalIdKey: string = this.getNaturalIdKey({ gameFileId, backupTargetId });
    return this.potentialFileCopiesWithContext().get(naturalIdKey);
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
      this.updatePotentialFileCopyWithContext(potentialFileCopy.naturalId, context => ({
        ...context,
        potentialFileCopy: {
          ...context.potentialFileCopy,
          status: FileCopyStatus.Enqueued
        }
      }));
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
    if (!potentialFileCopy.id) {
      return;
    }
    try {
      await firstValueFrom(this.fileCopiesClient.cancelFileCopy(potentialFileCopy.id)
        .pipe(catchError(e => {
          throw e;
        })));

      this.updatePotentialFileCopyWithContext(potentialFileCopy.naturalId, context => ({
        ...context,
        potentialFileCopy: {
          ...context.potentialFileCopy,
          status: FileCopyStatus.Tracked
        },
        progress: undefined
      }));

      this.notificationService.showSuccess("Backup canceled");
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

  async viewFilePath(fileCopyId: string): Promise<void> {
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
    this.window.location.href = `${this.fileCopiesClient.configuration.basePath}/api/file-copies/${this.fileCopiesClient
      .configuration.encodeParam({
        name: "fileCopyId", value: fileCopyId, in: "path", style: "simple", explode: false, dataType: "string",
        dataFormat: undefined
      })}`;
  }

  onClickViewError(fileCopyId: string): () => Promise<void> {
    return async () => this.viewError(fileCopyId);
  }

  async viewError(fileCopyId: string): Promise<void> {
    this.notificationService.showFailure('Viewing errors not yet implemented');
  }

  trackByGameId(index: number, gameWithFileCopies: GameWithFileCopies): string {
    return gameWithFileCopies.id;
  }

  trackByGameFileId(index: number, gameFileWithCopies: GameFileWithCopies): string {
    return gameFileWithCopies.gameFile.id;
  }

  private updatePotentialFileCopyWithContext(
    fileCopyNaturalId: FileCopyNaturalId,
    updateFn: (context: PotentialFileCopyWithContext) => PotentialFileCopyWithContext
  ): void {
    this.potentialFileCopiesWithContext.update(map => {
      const key = this.getNaturalIdKey(fileCopyNaturalId);
      const context = map.get(key);
      if (!context) {
        return map;
      }
      return new Map(map)
        .set(key, updateFn(context));
    });
  }

  private getNaturalIdKey(fileCopyNaturalId: FileCopyNaturalId): string {
    return `${fileCopyNaturalId.gameFileId}-${fileCopyNaturalId.backupTargetId}`;
  }

  private onStatusChanged(event: FileCopyStatusChangedEvent) {
    this.updatePotentialFileCopyWithContext(event.fileCopyNaturalId, context => ({
      ...context,
      potentialFileCopy: {
        ...context.potentialFileCopy,
        id: event.fileCopyId,
        status: event.newStatus as FileCopyStatus
      },
      progress: undefined
    }));
  }

  private onReplicationProgressChanged(event: FileCopyReplicationProgressUpdatedEvent) {
    this.updatePotentialFileCopyWithContext(event.fileCopyNaturalId, context => ({
      ...context,
      progress: {
        percentage: event.percentage,
        timeLeftSeconds: event.timeLeftSeconds,
      }
    }));
  }

  private async getBackupTargets() {
    this.backupTargets.set(await firstValueFrom(this.backupTargetsClient.getBackupTargets()));
  }

  private async getStorageSolutionStatuses() {
    const storageSolutionStatusesResponse: StorageSolutionStatusesResponse = await firstValueFrom(
      this.storageSolutionsClient.getStorageSolutionStatuses());
    this.storageSolutionStatusesById.set(new Map<string, StorageSolutionStatus>(
      Object.entries(storageSolutionStatusesResponse.statuses)));
  }

  private async getGamesWithFileCopies() {
    const searchQuery: string = this.searchForm.controls.searchBox?.value;
    const fileCopyStatusFilter: FileCopyStatus | undefined = this.searchForm.controls.fileCopyStatus?.value;
    const page = await firstValueFrom(this.gamesClient.getGames({
      page: this.pageNumber() - 1,
      size: this.pageSize(),
    }, searchQuery, fileCopyStatusFilter));
    this.gameWithFileCopiesPage.set(page);
    this.potentialFileCopiesWithContext.set(
      this.mapToPotentialFileCopiesWithContext(page.content));
  }

  private mapToPotentialFileCopiesWithContext(gamesWithFileCopies: GameWithFileCopies[])
    : Map<string, PotentialFileCopyWithContext> {
    const resultMap = new Map<string, PotentialFileCopyWithContext>();

    for (const {gameFilesWithCopies} of gamesWithFileCopies) {
      for (const {gameFile, fileCopiesWithProgress} of gameFilesWithCopies) {
        for (const backupTarget of this.backupTargets()) {
          const potentialCopy = this.createPotentialFileCopyWithContext(
            gameFile,
            fileCopiesWithProgress,
            backupTarget
          );
          resultMap.set(this.getNaturalIdKey(potentialCopy.potentialFileCopy.naturalId), potentialCopy);
        }
      }
    }

    return resultMap;
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
      storageSolutionStatus: this.storageSolutionStatusesById().get(backupTarget.storageSolutionId)
    };
  }
}
