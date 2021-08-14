import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {HttpClientModule} from "@angular/common/http";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {FormsModule} from "@angular/forms";
import {RouterModule} from "@angular/router";
import {GogAuthComponent} from './pages/auth/gog-auth/gog-auth.component';
import {SharedModule} from "@app/shared/shared.module";

@NgModule({
  declarations: [
    GogAuthComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    HttpClientModule,
    NgbModule,
    FormsModule,
    RouterModule
  ],
  exports: [
    GogAuthComponent
  ],
  providers: []
})
export class GogModule {
}
