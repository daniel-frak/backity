import {Routes} from '@angular/router';
import {DefaultLayoutComponent} from "@app/shared/layout/default-layout/default-layout.component";
import {ContainedLayoutComponent} from "@app/shared/layout/contained-layout/contained-layout.component";
import {GameProvidersComponent} from "@app/core/pages/auth/game-providers.component";
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
          {path: '', component: GameProvidersComponent, pathMatch: 'full'},
          {path: 'game-providers', component: GameProvidersComponent},
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
