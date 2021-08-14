import {NgModule} from "@angular/core";
import {DefaultLayoutComponent} from "@app/shared/layout/default-layout/default-layout.component";
import {LoadedContentComponent} from "@app/shared/components/loaded-content/loaded-content.component";
import {HttpClientModule} from "@angular/common/http";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {FormsModule} from "@angular/forms";
import {RouterModule} from "@angular/router";
import {CommonModule} from "@angular/common";

@NgModule({
  declarations: [
    DefaultLayoutComponent,
    LoadedContentComponent,
  ],
  imports: [
    CommonModule,
    HttpClientModule,
    NgbModule,
    FormsModule,
    RouterModule
  ],
  exports: [
    DefaultLayoutComponent,
    LoadedContentComponent
  ],
  providers: []
})
export class SharedModule {
}
