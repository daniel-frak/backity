import {Component, OnInit} from '@angular/core';
import {GOGAuthenticationClient, GogConfig, GOGConfigurationClient} from "@backend";
import {AbstractControl, FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {finalize, firstValueFrom, forkJoin, Observable} from "rxjs";
import {LoadedContentComponent} from '@app/shared/components/loaded-content/loaded-content.component';
import {CommonModule} from '@angular/common';
import {ButtonComponent} from '@app/shared/components/button/button.component';
import {InputComponent} from "@app/shared/components/form/input/input.component";
import {SectionComponent} from "@app/shared/components/section/section.component";
import {IconItemComponent} from "@app/shared/components/icon-item/icon-item.component";

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
    SectionComponent,
    IconItemComponent
  ]
})
export class GogAuthComponent implements OnInit {

  private gogAuthUrl?: string;

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
    const control = this.gogAuthForm.get('gogCodeUrl');
    if (!control) {
      throw new Error('The control "gogCodeUrl" does not exist in the form.');
    }
    return control;
  }

  constructor(private readonly gogConfigClient: GOGConfigurationClient,
              private readonly gogAuthClient: GOGAuthenticationClient,
              private readonly notificationService: NotificationService) {
  }

  ngOnInit() {
    this.gogIsLoading = true;
    const auth$: Observable<boolean> = this.gogAuthClient.checkAuthentication();
    const config$: Observable<GogConfig> = this.gogConfigClient.getGogConfig();

    forkJoin([auth$, config$])
      .pipe(finalize(() => this.gogIsLoading = false))
      .subscribe({
        next: ([isAuthenticated, gogConfig]) => {
          this.gogAuthenticated = isAuthenticated;
          this.gogAuthUrl = gogConfig.userAuthUrl;
        },
        error: error => this.notificationService.showFailure('Failed to configure GOG', error)
      });
  }

  showGogAuthPopup = () => {
    window.open(this.gogAuthUrl, '_blank', 'toolbar=0,location=0,menubar=0');
  }

  authenticateGog() {
    if (!this.gogAuthForm.valid) {
      this.gogAuthForm.markAllAsTouched();
      this.notificationService.showFailure("Please check the form for errors and try again.",
        this.getFormErrors(this.gogAuthForm));
      return;
    }
    this.gogIsLoading = true;
    const gogCodeUrl = this.gogCodeUrlInput.value;
    const params: URLSearchParams = new URL(gogCodeUrl).searchParams;
    const code = params.get("code") as string;
    this.gogAuthClient.authenticate(code).subscribe(r => {
      if (r.refresh_token) {
        this.gogAuthenticated = true;
        this.notificationService.showSuccess("GOG authentication successful");
        this.gogCodeUrlInput.reset();
      } else {
        this.notificationService.showFailure("Something went wrong during GOG authentication");
      }
      this.gogIsLoading = false;
    });
  }

  getFormErrors(form: FormGroup): Record<string, any> {
    return Object.entries(form.controls)
      .filter(([_, control]) => control.errors)
      .reduce((errorMap, [controlName, control]) => ({
        ...errorMap,
        [controlName]: control.errors,
      }), {});
  }

  onClickSignOutGog(): () => Promise<void> {
    return async () => this.signOutGog();
  }

  async signOutGog(): Promise<void> {
    this.gogIsLoading = true;
    try {
      await firstValueFrom(this.gogAuthClient.logOutOfGog());
      this.gogAuthenticated = false;
      this.notificationService.showSuccess("Logged out of GOG");
    } catch (error) {
      this.notificationService.showFailure('Could not log out of GOG', error);
    } finally {
      this.gogIsLoading = false;
    }
  }
}
