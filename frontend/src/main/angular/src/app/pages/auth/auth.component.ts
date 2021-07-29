import {Component, OnInit} from '@angular/core';
import {GOGAuthenticationService} from "../../../backend";

@Component({
  selector: 'app-auth',
  templateUrl: './auth.component.html',
  styleUrls: ['./auth.component.scss']
})
export class AuthComponent implements OnInit {

  private GOG_AUTH_URL = "https://login.gog.com/auth?client_id=46899977096215655"
    + "&redirect_uri=https%3A%2F%2Fembed.gog.com%2Fon_login_success%3Forigin%3Dclient"
    + "&response_type=code&layout=client2";

  public gogAuthenticated: boolean = false;
  public gogCodeUrl: string = "";
  public gogIsLoading: boolean = true;

  constructor(private readonly gogAuthService: GOGAuthenticationService) {
  }

  ngOnInit(): void {
    this.gogIsLoading = true;
    this.gogAuthService.check().subscribe(isAuthenticated => {
      this.gogAuthenticated = isAuthenticated;
      this.gogIsLoading = false;
    });
  }

  showGogAuthPopup(): void {
    window.open(this.GOG_AUTH_URL, '_blank', 'toolbar=0,location=0,menubar=0');
  }

  authenticateGog() {
    this.gogIsLoading = true;
    const params = (new URL(this.gogCodeUrl)).searchParams;
    const code = params.get("code") as string;
    console.warn(code);
    this.gogAuthService.authenticate(code).subscribe(r => {
      if (r.refresh_token) {
        console.info("Refresh token: " + r.refresh_token);
        this.gogAuthenticated = true;
      } else {
        console.error("Something went wrong when authenticating GOG");
      }
      this.gogIsLoading = false;
    });
  }

  signOutGog() {
    alert('Not yet implemented');
  }
}
