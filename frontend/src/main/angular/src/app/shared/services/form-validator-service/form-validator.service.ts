import {Injectable} from '@angular/core';
import {AbstractControl, FormGroup} from "@angular/forms";
import {NotificationService} from "@app/shared/services/notification/notification.service";

@Injectable({
  providedIn: 'root',
})
export class FormValidatorService {

  constructor(private readonly notificationService: NotificationService) {
  }

  public formIsInvalid(form: FormGroup) {
    if (form.invalid) {
      this.handleInvalidForm(form);
      return true;
    }
    return false;
  }

  private handleInvalidForm(form: FormGroup) {
    form.markAllAsTouched();
    this.notificationService.showFailure("Please check the form for errors and try again.",
      this.getFormErrors(form));
  }

  private getFormErrors(form: FormGroup): Record<string, any> {
    return Object.entries(form.controls)
      .filter(([_, control]: [string, AbstractControl]): boolean => control.errors != null)
      .reduce((
        errorMap: Record<string, any>,
        [controlName, control]: [string, AbstractControl]
      ): Record<string, any> => {
        errorMap[controlName] = control.errors;
        return errorMap;
      }, {} as Record<string, any>);
  }
}
