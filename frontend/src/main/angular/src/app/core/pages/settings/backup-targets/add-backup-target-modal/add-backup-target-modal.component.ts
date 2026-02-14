import {Component, signal} from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {ButtonComponent} from "@app/shared/components/button/button.component";
import {InputComponent} from "@app/shared/components/form/input/input.component";
import {LoadedContentComponent} from "@app/shared/components/loaded-content/loaded-content.component";
import {AutoLayoutComponent} from "@app/shared/components/auto-layout/auto-layout.component";
import {FormValidatorService} from "@app/shared/services/form-validator-service/form-validator.service";

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
    AutoLayoutComponent
  ],
  templateUrl: './add-backup-target-modal.component.html',
  styleUrl: './add-backup-target-modal.component.scss',
})
export class AddBackupTargetModalComponent {

  isLoading = signal(false);
  addBackupTargetForm: FormGroup<AddBackupTargetForm> = new FormGroup(
    {
      name: new FormControl('', {nonNullable: true, validators: [Validators.required]}),
      storageSolutionId: new FormControl('', {nonNullable: true, validators: [Validators.required]}),
      pathTemplate: new FormControl('', {nonNullable: true, validators: [Validators.required]})
    },
    {
      updateOn: 'submit'
    }
  );

  constructor(public readonly modal: NgbActiveModal,
              private readonly notificationService: NotificationService,
              private readonly formValidatorService: FormValidatorService) {
  }

  submit(): void {
    this.isLoading.set(true);
    if (this.formValidatorService.formIsInvalid(this.addBackupTargetForm)) {
      this.isLoading.set(false);
      return;
    }
    // @TODO Add on backend
    this.notificationService.showFailure("Adding backup targets is not yet implemented.")
    this.modal.close(false);
  }
}
