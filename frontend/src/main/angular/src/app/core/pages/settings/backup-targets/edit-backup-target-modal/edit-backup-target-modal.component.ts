import {Component, effect, signal} from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {FormValidatorService} from "@app/shared/services/form-validator-service/form-validator.service";
import {BackupTarget} from "@backend";
import {AutoLayoutComponent} from "@app/shared/components/auto-layout/auto-layout.component";
import {ButtonComponent} from "@app/shared/components/button/button.component";
import {InputComponent} from "@app/shared/components/form/input/input.component";
import {LoadedContentComponent} from "@app/shared/components/loaded-content/loaded-content.component";

interface EditBackupTargetForm {
  name: FormControl<string>;
  pathTemplate: FormControl<string>;
}

@Component({
  selector: 'app-edit-backup-target-modal',
  imports: [
    AutoLayoutComponent,
    ButtonComponent,
    InputComponent,
    LoadedContentComponent,
    ReactiveFormsModule
  ],
  templateUrl: './edit-backup-target-modal.component.html',
  styleUrl: './edit-backup-target-modal.component.scss',
})
export class EditBackupTargetModalComponent {

  isLoading = signal(false);
  backupTarget = signal<BackupTarget | undefined>(undefined)

  editBackupTargetForm: FormGroup<EditBackupTargetForm> = new FormGroup(
    {
      name: new FormControl('', {nonNullable: true, validators: [Validators.required]}),
      pathTemplate: new FormControl('', {nonNullable: true, validators: [Validators.required]})
    },
    {
      updateOn: 'submit'
    }
  );

  constructor(public readonly modal: NgbActiveModal,
              private readonly notificationService: NotificationService,
              private readonly formValidatorService: FormValidatorService) {
    effect(() => {
      this.editBackupTargetForm.controls.name.setValue(this.backupTarget()?.name ?? '')
      this.editBackupTargetForm.controls.pathTemplate.setValue(this.backupTarget()?.pathTemplate ?? '')
    });
  }

  submit(): void {
    this.isLoading.set(true);
    if (this.formValidatorService.formIsInvalid(this.editBackupTargetForm)) {
      this.isLoading.set(false);
      return;
    }
    // @TODO Edit on backend
    this.notificationService.showFailure("Editing backup targets is not yet implemented.")
    this.modal.close(false);
  }
}
