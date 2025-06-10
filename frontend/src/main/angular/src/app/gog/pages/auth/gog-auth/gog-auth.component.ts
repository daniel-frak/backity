import {Component, Input, OnInit} from '@angular/core';
import {GameContentDiscoveryOverview, GOGAuthenticationClient, GogConfig, GOGConfigurationClient} from "@backend";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {finalize, firstValueFrom, forkJoin, Observable} from "rxjs";
import {LoadedContentComponent} from '@app/shared/components/loaded-content/loaded-content.component';
import {CommonModule} from '@angular/common';
import {ButtonComponent} from '@app/shared/components/button/button.component';
import {IconItemComponent} from "@app/shared/components/icon-item/icon-item.component";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {GogAuthModalComponent} from "@app/gog/components/modals/gog-auth-modal/gog-auth-modal.component";
import {NgbModalRef} from "@ng-bootstrap/ng-bootstrap/modal/modal-ref";
import {LoadingPlaceholderComponent} from "@app/shared/components/loading-placeholder/loading-placeholder.component";
import {NamedValueComponent} from "@app/shared/components/named-value/named-value.component";
import {ProgressBarComponent} from "@app/shared/components/progress-bar/progress-bar.component";
import {
  GameContentDiscoveryOutcomeBadgeComponent
} from "@app/core/pages/game-providers/game-content-discovery-status-badge/game-content-discovery-outcome-badge.component";

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
    IconItemComponent,
    LoadingPlaceholderComponent,
    NamedValueComponent,
    ProgressBarComponent,
    GameContentDiscoveryOutcomeBadgeComponent
  ]
})
export class GogAuthComponent implements OnInit {

  private gogAuthUrl?: string;
  private activeModalRef?: NgbModalRef;

  public gogAuthenticated: boolean = false;
  public gogIsLoading: boolean = true;

  @Input()
  externalDataIsLoading: boolean = false;

  @Input()
  overview?: GameContentDiscoveryOverview;

  public openGogModal = () => this.showGogAuthModal();

  constructor(private readonly gogConfigClient: GOGConfigurationClient,
              private readonly gogAuthClient: GOGAuthenticationClient,
              private readonly notificationService: NotificationService,
              private readonly modalService: NgbModal) {
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

  isLoading(): boolean {
    return this.gogIsLoading || this.externalDataIsLoading;
  }

  showGogAuthModal(): Promise<void> {
    if (this.activeModalRef) {
      console.log("A modal is already open, not opening another one.");
      return this.activeModalRef.result;
    }

    this.activeModalRef = this.modalService.open(GogAuthModalComponent);
    (this.activeModalRef.componentInstance as GogAuthModalComponent).gogAuthUrl = this.gogAuthUrl;
    return this.activeModalRef.result.then(async (result) => {
      if (result) {
        this.gogAuthenticated = true;
      }
      this.activeModalRef = undefined;
    }, () => {
      this.activeModalRef = undefined;
    });
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
