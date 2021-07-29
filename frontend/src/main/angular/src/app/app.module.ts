import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomeComponent } from './pages/home/home.component';
import {HttpClientModule} from "@angular/common/http";
import { AuthComponent } from './pages/auth/auth.component';
import { DefaultLayoutComponent } from './layout/default-layout/default-layout.component';
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import { FileDiscoveryComponent } from './pages/file-discovery/file-discovery.component';
import { DownloadsComponent } from './pages/downloads/downloads.component';
import {FormsModule} from "@angular/forms";
import { LogsComponent } from './pages/logs/logs.component';
import { LoadedContentComponent } from './shared/components/loaded-content/loaded-content.component';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    AuthComponent,
    DefaultLayoutComponent,
    FileDiscoveryComponent,
    DownloadsComponent,
    LogsComponent,
    LoadedContentComponent
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
export class AppModule { }
