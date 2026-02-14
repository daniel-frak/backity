import {Component} from '@angular/core';
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {AbstractControl, FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {ButtonComponent} from "@app/shared/components/button/button.component";
import {InputComponent} from "@app/shared/components/form/input/input.component";
import {GOGAuthenticationClient, RefreshTokenResponse} from "@backend";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {LoadedContentComponent} from "@app/shared/components/loaded-content/loaded-content.component";
import {finalize} from "rxjs";
import {FormValidatorService} from "@app/shared/services/form-validator-service/form-validator.service";

const SOMETHING_WENT_WRONG_TEXT = "Something went wrong during GOG authentication";

interface GogAuthForm {
  gogCodeUrl: FormControl<string>;
}

@Component({
  selector: 'app-gog-auth-modal',
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
  public gogAuthForm: FormGroup<GogAuthForm> = new FormGroup(
    {
      gogCodeUrl: new FormControl('', {nonNullable: true, validators: [Validators.required]})
    },
    {
      updateOn: 'submit'
    }
  );

  constructor(public readonly modal: NgbActiveModal,
              private readonly gogAuthClient: GOGAuthenticationClient,
              private readonly notificationService: NotificationService,
              private readonly formValidatorService: FormValidatorService) {
  }

  get gogCodeUrlInput(): AbstractControl<string, string> {
    return this.gogAuthForm.controls.gogCodeUrl;
  }

  showGogAuthPopup = () => {
    window.open(this.gogAuthUrl, '_blank', 'toolbar=0,location=0,menubar=0');
  }

  authenticateGog() {
    try {
      this.isLoading = true;
      if (this.formValidatorService.formIsInvalid(this.gogAuthForm)) {
        this.isLoading = false;
        return;
      }

      const code: string | null = this.extractCodeFromUrl();
      if (!code) {
        this.notificationService.showFailure("Invalid URL: missing 'code' parameter");
        this.isLoading = false;
        return;
      }
      this.gogAuthClient.authenticateGog({
        code: code
      })
        .pipe(finalize(() => this.isLoading = false))
        .subscribe({
          next: response => this.handleAuthenticationResponse(response),
          error: () => this.notificationService.showFailure(SOMETHING_WENT_WRONG_TEXT)
        });
    } catch (error) {
      this.isLoading = false;
      this.notificationService.showFailure(SOMETHING_WENT_WRONG_TEXT, error)
    }
  }

  private handleAuthenticationResponse(response: RefreshTokenResponse) {
    if (response.refresh_token) {
      this.notificationService.showSuccess("GOG authentication successful");
      this.modal.close(true);
      this.gogCodeUrlInput.reset();
    } else {
      this.notificationService.showFailure(SOMETHING_WENT_WRONG_TEXT);
    }
  }

  private extractCodeFromUrl(): string | null {
    const gogCodeUrl = this.gogCodeUrlInput.value;
    const params: URLSearchParams = new URL(gogCodeUrl).searchParams;
    return params.get("code");
  }
}
