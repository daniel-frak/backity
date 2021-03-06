import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {HttpClientModule} from "@angular/common/http";
import {AuthComponent} from './pages/auth/auth.component';
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {FileDiscoveryComponent} from './pages/file-discovery/file-discovery.component';
import {DownloadsComponent} from './pages/downloads/downloads.component';
import {FormsModule} from "@angular/forms";
import {LogsComponent} from './pages/settings/logs/logs.component';
import {NewDiscoveredPopupComponent} from './pages/file-discovery/new-discovered-files-badge/new-discovered-popup/new-discovered-popup.component';
import {NewDiscoveredFilesBadgeComponent} from './pages/file-discovery/new-discovered-files-badge/new-discovered-files-badge.component';
import {FileDiscoveryStatusBadgeComponent} from './pages/file-discovery/file-discovery-status-badge/file-discovery-status-badge.component';
import {SharedModule} from "@app/shared/shared.module";
import {GogModule} from "@app/gog/gog.module";
import { SettingsLayoutComponent } from './pages/settings/settings-layout/settings-layout.component';
import {RouterModule} from "@angular/router";
import { SettingsSideNavComponent } from './pages/settings/settings-layout/settings-side-nav/settings-side-nav.component';
import {FilesComponent} from "@app/core/pages/settings/files/files.component";
import { ThemeComponent } from './pages/theme/theme.component';

@NgModule({
  declarations: [
    AuthComponent,
    FileDiscoveryComponent,
    DownloadsComponent,
    LogsComponent,
    NewDiscoveredPopupComponent,
    NewDiscoveredFilesBadgeComponent,
    FileDiscoveryStatusBadgeComponent,
    SettingsLayoutComponent,
    SettingsSideNavComponent,
    FilesComponent,
    ThemeComponent
  ],
  imports: [
    SharedModule,
    GogModule,
    BrowserModule,
    HttpClientModule,
    NgbModule,
    FormsModule,
    RouterModule
  ],
  providers: [],
})
export class CoreModule {
}
