import {Routes} from '@angular/router';
import {DefaultLayoutComponent} from "@app/shared/layout/default-layout/default-layout.component";
import {ContainedLayoutComponent} from "@app/shared/layout/contained-layout/contained-layout.component";
import {GameProvidersComponent} from "@app/core/pages/game-providers/game-providers.component";
import {GamesComponent} from "@app/core/pages/games/games.component";
import {ThemeComponent} from "@app/core/pages/theme/theme.component";
import {SettingsLayoutComponent} from "@app/core/pages/settings/settings-layout/settings-layout.component";
import {QueueComponent} from "@app/core/pages/queue/queue.component";
import {BackupTargetsComponent} from "@app/core/pages/settings/backup-targets/backup-targets.component";
import {StorageSolutionsComponent} from "@app/core/pages/settings/storage-solutions/storage-solutions.component";

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
          {path: 'queue', component: QueueComponent},
          {path: 'games', component: GamesComponent},
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
          {path: '', component: BackupTargetsComponent},
          {path: 'storage-solutions', component: StorageSolutionsComponent},
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
