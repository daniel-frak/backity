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
  PageGameWithFileCopies
} from "@backend";
import {catchError} from "rxjs/operators";
import {firstValueFrom, Subscription} from "rxjs";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {ModalService} from "@app/shared/services/modal-service/modal.service";
import {ButtonComponent} from '@app/shared/components/button/button.component';
import {LoadedContentComponent} from '@app/shared/components/loaded-content/loaded-content.component';
import {NgIf, NgSwitch, NgSwitchCase} from '@angular/common';
import {TableComponent} from '@app/shared/components/table/table.component';
import {TableColumnDirective} from '@app/shared/components/table/column-directive/table-column.directive';
import {MessagesService} from "@app/shared/backend/services/messages.service";
import {Message} from "@stomp/stompjs";
import {PaginationComponent} from "@app/shared/components/pagination/pagination.component";
import {
  FileCopyStatusBadgeComponent
} from "@app/core/components/file-copy-status-badge/file-copy-status-badge.component";
import {TableContentGroup} from "@app/shared/components/table/table-content-group";
import {
  PotentialFileCopyWithContext
} from "@app/core/pages/games/games-with-files-card/potential-file-copy-with-context";
import {
  PotentialFileCopy,
  PotentialFileCopyFactory
} from "@app/core/pages/games/games-with-files-card/potential-file-copy";
import {SectionComponent} from "@app/shared/components/section/section.component";
import {ProgressBarComponent} from "@app/shared/components/progress-bar/progress-bar.component";

@Component({
  selector: 'app-games-with-file-copies-card',
  standalone: true,
  imports: [
    ButtonComponent,
    FileCopyStatusBadgeComponent,
    LoadedContentComponent,
    NgSwitchCase,
    PaginationComponent,
    TableColumnDirective,
    TableComponent,
    NgSwitch,
    NgIf,
    SectionComponent,
    ProgressBarComponent
  ],
  templateUrl: './games-with-file-copies-card.component.html',
  styleUrl: './games-with-file-copies-card.component.scss'
})
export class GamesWithFileCopiesCardComponent implements OnInit, OnDestroy {

  asPotentialFileCopyWithContext =
    (potentialFileCopyWithContext: PotentialFileCopyWithContext) =>
      potentialFileCopyWithContext;
  FileCopyStatus = FileCopyStatus;

  gamesAreLoading: boolean = true;
  gameWithFileCopiesPage?: PageGameWithFileCopies; // Kept for pagination only
  potentialFileCopiesWithContextByGameTitle: Map<string, PotentialFileCopyWithContext[]> =
    new Map<string, PotentialFileCopyWithContext[]>();
  backupTargets: Array<BackupTarget> = [];
  pageNumber: number = 1;
  pageSize: number = 3;

  private readonly subscriptions: Subscription[] = [];

  constructor(private readonly gamesClient: GamesClient,
              private readonly fileCopiesClient: FileCopiesClient,
              private readonly backupTargetsClient: BackupTargetsClient,
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
      console.warn("Could not find potential file copy", event, this.potentialFileCopiesWithContextByGameTitle);
    }
  }

  private findPotentialFileCopyWithContext(fileCopyNaturalId: FileCopyNaturalId):
    PotentialFileCopyWithContext | undefined {
    return Array.from(this.potentialFileCopiesWithContextByGameTitle?.values())
      .flat()
      .find((potentialFileCopyWithContext) => {
        return this.fileCopyNaturalIdsAreEqual(potentialFileCopyWithContext.potentialFileCopy.naturalId, fileCopyNaturalId);
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
      console.warn("Could not find potential file copy", event, this.potentialFileCopiesWithContextByGameTitle);
    }
  }

  onClickRefresh(): () => Promise<void> {
    return async () => this.refresh();
  }

  async refresh(): Promise<void> {
    try {
      this.gamesAreLoading = true;
      this.backupTargets = await firstValueFrom(this.backupTargetsClient.getBackupTargets());
      this.gameWithFileCopiesPage = await firstValueFrom(this.gamesClient.getGames({
        page: this.pageNumber - 1,
        size: this.pageSize
      }));
      this.potentialFileCopiesWithContextByGameTitle = new Map(
        this.gameWithFileCopiesPage.content?.map(({title, gameFilesWithCopies}) =>
          [title, this.mapToPotentialFileCopiesWithContext(gameFilesWithCopies)]
        )
      );
      this.gamesAreLoading = false;
    } catch (error) {
      this.notificationService.showFailure('Error fetching games', error);
    } finally {
      this.gamesAreLoading = false;
    }
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
      potentialFileCopy.status = FileCopyStatus.Tracked;
    }
  }

  onClickCancelBackup(fileCopyId: string): () => Promise<void> {
    return async () => this.cancelBackup(fileCopyId);
  }

  async cancelBackup(fileCopyId: string): Promise<void> {
    this.notificationService.showFailure('Removing from queue not yet implemented');
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

  group(content: Map<string, PotentialFileCopyWithContext[]>): TableContentGroup[] {
    return Array.from(content.entries()).map(([title, gameFilesWithCopies]) => ({
      caption: title,
      items: gameFilesWithCopies,
    }));
  }

  private mapToPotentialFileCopiesWithContext(gameFilesWithCopies: GameFileWithCopies[]): PotentialFileCopyWithContext[] {
    return gameFilesWithCopies.flatMap(({gameFile, fileCopiesWithProgress}) =>
      this.backupTargets
        .map(backupTarget => this.createPotentialFileCopyWithContext(
          gameFile, fileCopiesWithProgress, backupTarget))
    );
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
    };
  }

  getBackupTargetName(backupTargetId: string): string {
    return this.backupTargets.find(backupTarget => backupTarget.id == backupTargetId)?.name ?? "";
  }
}
