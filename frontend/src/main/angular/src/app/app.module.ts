import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import { provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {FormsModule} from "@angular/forms";
import {CoreModule} from "@app/core/core.module";

@NgModule({ declarations: [
        AppComponent,
    ],
    bootstrap: [AppComponent], imports: [CoreModule,
        BrowserModule,
        AppRoutingModule,
        NgbModule,
        FormsModule], providers: [provideHttpClient(withInterceptorsFromDi())] })
export class AppModule {
}
