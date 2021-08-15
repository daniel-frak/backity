import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {DefaultLayoutComponent} from "./shared/layout/default-layout/default-layout.component";
import {FileDiscoveryComponent} from "./core/pages/file-discovery/file-discovery.component";
import {DownloadsComponent} from "./core/pages/downloads/downloads.component";
import {AuthComponent} from "./core/pages/auth/auth.component";
import {LogsComponent} from "./core/pages/settings/logs/logs.component";
import {SettingsLayoutComponent} from "@app/core/pages/settings/settings-layout/settings-layout.component";
import {ContainedLayoutComponent} from "@app/shared/layout/contained-layout/contained-layout.component";
import {FilesComponent} from "@app/pages/settings/files/files.component";

const routes: Routes = [
  {
    path: '',
    component: DefaultLayoutComponent,
    children: [
      {
        path: '',
        component: ContainedLayoutComponent,
        children: [
          {path: '', component: AuthComponent, pathMatch: 'full'},
          {path: 'auth', component: AuthComponent},
          {path: 'file-discovery', component: FileDiscoveryComponent},
          {path: 'downloads', component: DownloadsComponent},
          {path: 'logs', component: LogsComponent},
        ]
      },
      {
        path: 'settings',
        component: SettingsLayoutComponent,
        children: [
          {path: '', component: FilesComponent},
          {path: 'logs', component: LogsComponent}
        ]
      }
    ]
  }

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
