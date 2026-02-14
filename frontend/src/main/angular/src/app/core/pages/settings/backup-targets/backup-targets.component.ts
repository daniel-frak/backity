import {Component, OnInit, signal} from '@angular/core';
import {BackupTarget, BackupTargetsClient} from "@backend";
import {finalize, firstValueFrom} from "rxjs";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {AutoLayoutComponent} from "@app/shared/components/auto-layout/auto-layout.component";
import {LoadedContentComponent} from "@app/shared/components/loaded-content/loaded-content.component";
import {SectionComponent} from "@app/shared/components/section/section.component";
import {IconItemComponent} from "@app/shared/components/icon-item/icon-item.component";
import {NamedValueComponent} from "@app/shared/components/named-value/named-value.component";
import {
  NamedValueContainerComponent
} from "@app/shared/components/named-value-container/named-value-container.component";
import {ButtonComponent} from "@app/shared/components/button/button.component";
import {ModalService} from "@app/shared/services/modal-service/modal.service";
import {
  AddBackupTargetModalComponent
} from "@app/core/pages/settings/backup-targets/add-backup-target-modal/add-backup-target-modal.component";
import {
  EditBackupTargetModalComponent
} from "@app/core/pages/settings/backup-targets/edit-backup-target-modal/edit-backup-target-modal.component";
import {ModalRef} from "@app/shared/services/modal-service/modal-ref";

@Component({
  selector: 'app-backup-targets',
  imports: [
    AutoLayoutComponent,
    LoadedContentComponent,
    SectionComponent,
    IconItemComponent,
    NamedValueComponent,
    NamedValueContainerComponent,
    ButtonComponent
  ],
  templateUrl: './backup-targets.component.html',
  styleUrl: './backup-targets.component.scss',
})
export class BackupTargetsComponent implements OnInit {

  backupTargetsAreLoading = signal(false);
  backupTargets = signal<Array<BackupTarget>>([]);
  usedBackupTargetIds = signal<Array<string>>([]);

  private activeModalRef?: ModalRef;

  constructor(private readonly backupTargetsClient: BackupTargetsClient,
              private readonly notificationService: NotificationService,
              private readonly modalService: ModalService) {
  }

  ngOnInit(): void {
    void this.refresh();
  }

  async refresh(): Promise<void> {
    if (this.backupTargetsAreLoading()) {
      return;
    }
    this.backupTargetsAreLoading.set(true);
    try {
      await this.getBackupTargets();
    } catch (error) {
      this.notificationService.showFailure('Error fetching backup targets', error);
    } finally {
      this.backupTargetsAreLoading.set(false);
    }
  }

  private async getBackupTargets() {
    const [backupTargets, usedBackupTargetIds] = await Promise.all([
      firstValueFrom(this.backupTargetsClient.getBackupTargets()),
      firstValueFrom(this.backupTargetsClient.getLockedBackupTargetIds()),
    ]);
    this.backupTargets.set(backupTargets);
    this.usedBackupTargetIds.set(usedBackupTargetIds);
  }

  onClickShowAddModal(): () => Promise<void> {
    return async () => {
      if (this.activeModalRef) {
        console.log("A modal is already open, not opening another one.");
        return this.activeModalRef.result;
      }

      this.activeModalRef = this.modalService.open(AddBackupTargetModalComponent);
      return this.activeModalRef.result.then(async (result) => {
        if (result) {
          void this.refresh();
        }
        this.activeModalRef = undefined;
      }, () => {
        this.activeModalRef = undefined;
      });
    }
  }

  onClickShowEditModal(backupTarget: BackupTarget): () => Promise<void> {
    return async () => {
      if (this.activeModalRef) {
        console.log("A modal is already open, not opening another one.");
        return this.activeModalRef.result;
      }

      this.activeModalRef = this.modalService.open(EditBackupTargetModalComponent);
      this.activeModalRef.componentInstance.backupTarget.set(backupTarget);
      return this.activeModalRef.result.then(async (result) => {
        if (result) {
          void this.refresh();
        }
        this.activeModalRef = undefined;
      }, () => {
        this.activeModalRef = undefined;
      });
    }
  }

  onClickDelete(backupTarget: BackupTarget): () => Promise<void> {
    return async () => {
      try {
        await this.modalService.withConfirmationModal(
          "Are you sure you want to delete the backup target?",
          async () => {
            this.backupTargetsAreLoading.set(true);
            await firstValueFrom(
              this.backupTargetsClient.deleteBackupTarget(backupTarget.id)
                .pipe(finalize(() => this.backupTargetsAreLoading.set(false)))
            );

            this.notificationService.showSuccess("Backup target deleted successfully");
            await this.refresh();
          }
        );
      } catch (error) {
        this.notificationService.showFailure(
          'An error occurred while trying to delete a backup target', backupTarget.id, error
        );
        this.backupTargetsAreLoading.set(false);
      }
    };
  }

  isLocked(backupTarget: BackupTarget): boolean {
    return this.usedBackupTargetIds().includes(backupTarget.id);
  }
}
