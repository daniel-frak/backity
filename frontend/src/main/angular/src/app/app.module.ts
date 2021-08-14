import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HttpClientModule} from "@angular/common/http";
import {AuthComponent} from './pages/auth/auth.component';
import {DefaultLayoutComponent} from './layout/default-layout/default-layout.component';
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {FileDiscoveryComponent} from './pages/file-discovery/file-discovery.component';
import {DownloadsComponent} from './pages/downloads/downloads.component';
import {FormsModule} from "@angular/forms";
import {LogsComponent} from './pages/logs/logs.component';
import {LoadedContentComponent} from './shared/components/loaded-content/loaded-content.component';
import { NewDiscoveredPopupComponent } from './pages/file-discovery/new-discovered-files-badge/new-discovered-popup/new-discovered-popup.component';
import { NewDiscoveredFilesBadgeComponent } from './pages/file-discovery/new-discovered-files-badge/new-discovered-files-badge.component';
import { FileDiscoveryStatusBadgeComponent } from './pages/file-discovery/file-discovery-status-badge/file-discovery-status-badge.component';

@NgModule({
  declarations: [
    AppComponent,
    AuthComponent,
    DefaultLayoutComponent,
    FileDiscoveryComponent,
    DownloadsComponent,
    LogsComponent,
    LoadedContentComponent,
    NewDiscoveredPopupComponent,
    NewDiscoveredFilesBadgeComponent,
    FileDiscoveryStatusBadgeComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    NgbModule,
    FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
