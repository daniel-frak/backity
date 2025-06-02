import {Component} from '@angular/core';
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {AbstractControl, FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {ButtonComponent} from "@app/shared/components/button/button.component";
import {InputComponent} from "@app/shared/components/form/input/input.component";
import {GOGAuthenticationClient, RefreshTokenResponse} from "@backend";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {LoadedContentComponent} from "@app/shared/components/loaded-content/loaded-content.component";
import {finalize} from "rxjs";

@Component({
  selector: 'app-gog-auth-modal',
  standalone: true,
  imports: [
    FormsModule,
    ButtonComponent,
    InputComponent,
    ReactiveFormsModule,
    LoadedContentComponent,
  ],
  templateUrl: './gog-auth-modal.component.html',
  styleUrl: './gog-auth-modal.component.scss'
})
export class GogAuthModalComponent {

  public gogAuthUrl?: string;
  public isLoading: boolean = false;

  constructor(public readonly modal: NgbActiveModal,
              private readonly gogAuthClient: GOGAuthenticationClient,
              private readonly notificationService: NotificationService) {
  }

  public gogAuthForm: FormGroup = new FormGroup(
    {
      gogCodeUrl: new FormControl('', Validators.required)
    },
    {
      updateOn: 'submit'
    }
  );

  get gogCodeUrlInput(): AbstractControl<any, any> {
    const control = this.gogAuthForm.get('gogCodeUrl');
    if (!control) {
      throw new Error('The control "gogCodeUrl" does not exist in the form.');
    }
    return control;
  }

  showGogAuthPopup = () => {
    window.open(this.gogAuthUrl, '_blank', 'toolbar=0,location=0,menubar=0');
  }

  authenticateGog() {
    try {
      this.isLoading = true;
      if (!this.gogAuthForm.valid) {
        this.handleInvalidForm();
        this.isLoading = false;
        return;
      }
      const code: string = this.extractCodeFromUrl();
      this.gogAuthClient.authenticate(code)
        .pipe(finalize(() => this.isLoading = false))
        .subscribe({
          next: response => this.handleAuthenticationResponse(response),
          error: () => this.notificationService.showFailure(
            "Something went wrong during GOG authentication")
        });
    } catch (error) {
      this.isLoading = false;
      this.notificationService.showFailure(
        "Something went wrong during GOG authentication", error)
    }
  }

  private handleAuthenticationResponse(response: RefreshTokenResponse) {
    if (response.refresh_token) {
      this.notificationService.showSuccess("GOG authentication successful");
      this.modal.close(true);
      this.gogCodeUrlInput.reset();
    } else {
      this.notificationService.showFailure("Something went wrong during GOG authentication");
    }
  }

  private handleInvalidForm() {
    this.gogAuthForm.markAllAsTouched();
    this.notificationService.showFailure("Please check the form for errors and try again.",
      this.getFormErrors(this.gogAuthForm));
  }

  private extractCodeFromUrl() {
    const gogCodeUrl = this.gogCodeUrlInput.value;
    const params: URLSearchParams = new URL(gogCodeUrl).searchParams;
    return params.get("code") as string;
  }

  getFormErrors(form: FormGroup): Record<string, any> {
    return Object.entries(form.controls)
      .filter(([_, control]) => control.errors)
      .reduce((errorMap, [controlName, control]) => {
        errorMap[controlName] = control.errors;
        return errorMap;
      }, {} as Record<string, any>);
  }
}
