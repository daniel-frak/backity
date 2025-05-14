import {Routes} from '@angular/router';
import {DefaultLayoutComponent} from "@app/shared/layout/default-layout/default-layout.component";
import {ContainedLayoutComponent} from "@app/shared/layout/contained-layout/contained-layout.component";
import {AuthComponent} from "@app/core/pages/auth/auth.component";
import {GameContentDiscoveryComponent} from "@app/core/pages/file-discovery/game-content-discovery.component";
import {FileBackupComponent} from "@app/core/pages/file-backup/file-backup.component";
import {GamesComponent} from "@app/core/pages/games/games.component";
import {LogsComponent} from "@app/core/pages/settings/logs/logs.component";
import {ThemeComponent} from "@app/core/pages/theme/theme.component";
import {SettingsLayoutComponent} from "@app/core/pages/settings/settings-layout/settings-layout.component";
import {FilesComponent} from "@app/core/pages/settings/files/files.component";

export const routes: Routes = [
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
          {path: 'game-content-discovery', component: GameContentDiscoveryComponent},
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
  },
  {
    // If route not found:
    path: '**',
    redirectTo: ''
  }
];
