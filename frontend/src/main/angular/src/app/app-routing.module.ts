import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {DefaultLayoutComponent} from "./shared/layout/default-layout/default-layout.component";
import {FileDiscoveryComponent} from "./core/pages/file-discovery/file-discovery.component";
import {FileBackupComponent} from "@app/core/pages/downloads/file-backup.component";
import {AuthComponent} from "./core/pages/auth/auth.component";
import {LogsComponent} from "./core/pages/settings/logs/logs.component";
import {SettingsLayoutComponent} from "@app/core/pages/settings/settings-layout/settings-layout.component";
import {ContainedLayoutComponent} from "@app/shared/layout/contained-layout/contained-layout.component";
import {FilesComponent} from "@app/core/pages/settings/files/files.component";
import {ThemeComponent} from "@app/core/pages/theme/theme.component";
import {GamesComponent} from "@app/core/pages/games/games.component";

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
          {path: 'file-backup', component: FileBackupComponent},
          {path: 'games', component: GamesComponent},
          {path: 'logs', component: LogsComponent},
          {
            path: 'theme',
            component: ThemeComponent
          }
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
