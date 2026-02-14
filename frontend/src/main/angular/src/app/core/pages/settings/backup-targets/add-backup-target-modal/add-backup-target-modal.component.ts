import {Component, signal} from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {ButtonComponent} from "@app/shared/components/button/button.component";
import {InputComponent} from "@app/shared/components/form/input/input.component";
import {LoadedContentComponent} from "@app/shared/components/loaded-content/loaded-content.component";
import {AutoLayoutComponent} from "@app/shared/components/auto-layout/auto-layout.component";
import {FormValidatorService} from "@app/shared/services/form-validator-service/form-validator.service";
import {BackupTargetsClient} from "@backend";
import {finalize} from "rxjs";
import {AddBackupTargetResponse} from "@backend/model/addBackupTargetResponse";
import {SelectComponent} from "@app/shared/components/select/select.component";

interface AddBackupTargetForm {
  name: FormControl<string>;
  storageSolutionId: FormControl<string>;
  pathTemplate: FormControl<string>;
}

@Component({
  selector: 'app-add-backup-target-modal',
  imports: [
    ButtonComponent,
    InputComponent,
    LoadedContentComponent,
    ReactiveFormsModule,
    AutoLayoutComponent,
    SelectComponent
  ],
  templateUrl: './add-backup-target-modal.component.html',
  styleUrl: './add-backup-target-modal.component.scss',
})
export class AddBackupTargetModalComponent {

  isLoading = signal(false);
  readonly storageSolutions = signal<string[]>(['LOCAL_FILE_SYSTEM', 'S3']);

  addBackupTargetForm: FormGroup<AddBackupTargetForm> = new FormGroup(
    {
      name: new FormControl('', {nonNullable: true, validators: [Validators.required]}),
      storageSolutionId: new FormControl(this.storageSolutions()[0], {
        nonNullable: true,
        validators: [Validators.required]
      }),
      pathTemplate: new FormControl('games/{GAME_PROVIDER_ID}/{GAME_TITLE}/{FILENAME}', {
        nonNullable: true,
        validators: [Validators.required]
      })
    },
    {
      updateOn: 'submit'
    }
  );

  constructor(public readonly modal: NgbActiveModal,
              private readonly notificationService: NotificationService,
              private readonly formValidatorService: FormValidatorService,
              private readonly backupTargetsClient: BackupTargetsClient) {
  }

  submit(): void {
    this.isLoading.set(true);
    if (this.formValidatorService.formIsInvalid(this.addBackupTargetForm)) {
      this.isLoading.set(false);
      return;
    }
    this.backupTargetsClient.addBackupTarget({
      name: this.addBackupTargetForm.controls.name.value,
      storageSolutionId: this.addBackupTargetForm.controls.storageSolutionId.value,
      pathTemplate: this.addBackupTargetForm.controls.pathTemplate.value
    })
      .pipe(finalize(() => this.isLoading.set(false)))
      .subscribe({
        next: response => this.handleAddBackupTargetResponse(response),
        error: error => this.notificationService.showFailure(
          "Something went wrong when adding a Backup Target.", error)
      });
  }

  private handleAddBackupTargetResponse(_: AddBackupTargetResponse) {
    this.notificationService.showSuccess("Backup target added successfully");
    this.modal.close(true);
  }
}
