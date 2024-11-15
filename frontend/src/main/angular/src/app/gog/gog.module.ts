import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import { provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {FormsModule} from "@angular/forms";
import {RouterModule} from "@angular/router";
import {GogAuthComponent} from './pages/auth/gog-auth/gog-auth.component';
import {SharedModule} from "@app/shared/shared.module";

@NgModule({ declarations: [
        GogAuthComponent
    ],
    exports: [
        GogAuthComponent
    ], imports: [SharedModule,
        CommonModule,
        NgbModule,
        FormsModule,
        RouterModule], providers: [provideHttpClient(withInterceptorsFromDi())] })
export class GogModule {
}
