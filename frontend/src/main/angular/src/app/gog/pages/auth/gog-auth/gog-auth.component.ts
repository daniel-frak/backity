import {Component, OnInit} from '@angular/core';
import {GOGAuthenticationClient} from "@backend";
import {environment} from "@environment/environment";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {NotificationService} from "@app/shared/services/notification/notification.service";

@Component({
  selector: 'app-gog-auth',
  templateUrl: './gog-auth.component.html',
  styleUrls: ['./gog-auth.component.scss']
})
export class GogAuthComponent implements OnInit {

  private readonly GOG_AUTH_URL = environment.gogAuthUrl;

  public gogAuthenticated: boolean = false;
  public gogCodeUrl: string = "";
  public gogIsLoading: boolean = true;
  public gogAuthForm: FormGroup = new FormGroup({
    gogCodeUrl: new FormControl('', Validators.required)
  });

  constructor(private readonly gogAuthClient: GOGAuthenticationClient,
              private readonly notificationService: NotificationService) {
  }

  ngOnInit(): void {
    this.gogIsLoading = true;
    this.gogAuthClient.checkAuthentication().subscribe(isAuthenticated => {
      this.gogAuthenticated = isAuthenticated;
      this.gogIsLoading = false;
    });
  }

  showGogAuthPopup = () => {
    window.open(this.GOG_AUTH_URL, '_blank', 'toolbar=0,location=0,menubar=0');
  }

  authenticateGog() {
    this.gogIsLoading = true;
    let gogCodeUrl = this.gogAuthForm.get('gogCodeUrl')?.value;
    if (!gogCodeUrl) {
      this.gogIsLoading = false;
      return;
    }
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

  async signOutGog() {
    this.notificationService.showFailure('Not yet implemented');
  }
}
