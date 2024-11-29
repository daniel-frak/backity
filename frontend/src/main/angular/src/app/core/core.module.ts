import {ErrorHandler, NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {provideHttpClient, withInterceptorsFromDi} from "@angular/common/http";
import {AuthComponent} from './pages/auth/auth.component';
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {FileDiscoveryComponent} from './pages/file-discovery/file-discovery.component';
import {FileBackupComponent} from '@app/core/pages/file-backup/file-backup.component';
import {FormsModule} from "@angular/forms";
import {LogsComponent} from './pages/settings/logs/logs.component';
import {
  NewDiscoveredPopupComponent
} from './pages/file-discovery/new-discovered-files-badge/new-discovered-popup/new-discovered-popup.component';
import {
  NewDiscoveredFilesBadgeComponent
} from './pages/file-discovery/new-discovered-files-badge/new-discovered-files-badge.component';
import {
  FileDiscoveryStatusBadgeComponent
} from './pages/file-discovery/file-discovery-status-badge/file-discovery-status-badge.component';
import {SharedModule} from "@app/shared/shared.module";
import {GogModule} from "@app/gog/gog.module";
import {SettingsLayoutComponent} from './pages/settings/settings-layout/settings-layout.component';
import {RouterModule} from "@angular/router";
import {SettingsSideNavComponent} from './pages/settings/settings-layout/settings-side-nav/settings-side-nav.component';
import {FilesComponent} from "@app/core/pages/settings/files/files.component";
import {ThemeComponent} from './pages/theme/theme.component';
import {GamesComponent} from './pages/games/games.component';
import {FileStatusBadgeComponent} from './pages/games/file-status-badge/file-status-badge.component';
import {ButtonComponent} from "@app/shared/components/button/button.component";
import {GlobalErrorHandler} from "@app/shared/errors/error-handler/global-error-handler.service";

@NgModule({
  declarations: [
    AuthComponent,
    FileDiscoveryComponent,
    FileBackupComponent,
    LogsComponent,
    NewDiscoveredPopupComponent,
    NewDiscoveredFilesBadgeComponent,
    FileDiscoveryStatusBadgeComponent,
    SettingsLayoutComponent,
    SettingsSideNavComponent,
    FilesComponent,
    ThemeComponent,
    GamesComponent,
    FileStatusBadgeComponent
  ], imports: [SharedModule,
    GogModule,
    BrowserModule,
    NgbModule,
    FormsModule,
    RouterModule, ButtonComponent], providers: [
    provideHttpClient(withInterceptorsFromDi()),
    {
      provide: ErrorHandler,
      useClass: GlobalErrorHandler,
    },
  ]
})
export class CoreModule {
}
