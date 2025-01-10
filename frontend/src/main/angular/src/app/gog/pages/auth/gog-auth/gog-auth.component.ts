import {Component, OnInit} from '@angular/core';
import {GOGAuthenticationClient} from "@backend";
import {environment} from "@environment/environment";
import {AbstractControl, FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {finalize} from "rxjs";
import {LoadedContentComponent} from '@app/shared/components/loaded-content/loaded-content.component';
import {CommonModule} from '@angular/common';
import {ButtonComponent} from '@app/shared/components/button/button.component';
import {InputComponent} from "@app/shared/components/form/input/input.component";
import {CardComponent} from "@app/shared/components/card/card.component";

@Component({
  selector: 'app-gog-auth',
  templateUrl: './gog-auth.component.html',
  styleUrls: ['./gog-auth.component.scss'],
  standalone: true,
  imports: [
    LoadedContentComponent,
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    ButtonComponent,
    InputComponent,
    CardComponent
  ]
})
export class GogAuthComponent implements OnInit {

  private readonly GOG_AUTH_URL = environment.gogAuthUrl;

  public gogAuthenticated: boolean = false;
  public gogIsLoading: boolean = true;
  public gogAuthForm: FormGroup = new FormGroup(
    {
      gogCodeUrl: new FormControl('', Validators.required)
    },
    {
      updateOn: 'submit'
    }
  );

  get gogCodeUrlInput(): AbstractControl<any, any> {
    return this.gogAuthForm.get('gogCodeUrl')!;
  }

  constructor(private readonly gogAuthClient: GOGAuthenticationClient,
              private readonly notificationService: NotificationService) {
  }

  ngOnInit(): void {
    this.gogIsLoading = true;
    this.gogAuthClient.checkAuthentication()
      .pipe(finalize(() => this.gogIsLoading = false))
      .subscribe({
        next: isAuthenticated => this.gogAuthenticated = isAuthenticated,
        error: error => this.notificationService.showFailure('Failed to check GOG authentication', error)
      });
  }

  showGogAuthPopup = () => {
    window.open(this.GOG_AUTH_URL, '_blank', 'toolbar=0,location=0,menubar=0');
  }

  authenticateGog() {
    if (!this.gogAuthForm.valid) {
      this.gogAuthForm.markAllAsTouched();
      this.notificationService.showFailure("Form is invalid", this.gogCodeUrlInput.errors);
      return;
    }
    this.gogIsLoading = true;
    const gogCodeUrl = this.gogAuthForm.get('gogCodeUrl')?.value;
    const params: URLSearchParams = new URL(gogCodeUrl).searchParams;
    const code = params.get("code") as string;
    this.gogAuthClient.authenticate(code).subscribe(r => {
      if (r.refresh_token) {
        this.gogAuthenticated = true;
        this.notificationService.showSuccess("GOG authentication successful");
      } else {
        this.notificationService.showFailure("Something went wrong during GOG authentication");
      }
      this.gogIsLoading = false;
    });
  }

  onClickSignOutGog(): () => Promise<void> {
    return async () => this.signOutGog();
  }

  async signOutGog(): Promise<void> {
    this.notificationService.showFailure('Not yet implemented');
  }
}
