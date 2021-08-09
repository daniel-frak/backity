import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {DefaultLayoutComponent} from "./layout/default-layout/default-layout.component";
import {FileDiscoveryComponent} from "./pages/file-discovery/file-discovery.component";
import {DownloadsComponent} from "./pages/downloads/downloads.component";
import {AuthComponent} from "./pages/auth/auth.component";
import {LogsComponent} from "./pages/logs/logs.component";

const routes: Routes = [
  {
    path: '', component: DefaultLayoutComponent, children: [
      {path: '', component: AuthComponent, pathMatch: 'full'},
      {path: 'auth', component: AuthComponent},
      {path: 'file-discovery', component: FileDiscoveryComponent},
      {path: 'downloads', component: DownloadsComponent},
      {path: 'logs', component: LogsComponent}
    ]
  }

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
