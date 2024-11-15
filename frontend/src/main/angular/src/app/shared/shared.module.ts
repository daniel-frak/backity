import {InjectionToken, NgModule} from "@angular/core";
import {DefaultLayoutComponent} from "@app/shared/layout/default-layout/default-layout.component";
import {LoadedContentComponent} from "@app/shared/components/loaded-content/loaded-content.component";
import { provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {FormsModule} from "@angular/forms";
import {RouterModule} from "@angular/router";
import {CommonModule} from "@angular/common";
import {PageHeaderComponent} from './components/page-header/page-header.component';
import {ContainedLayoutComponent} from './layout/contained-layout/contained-layout.component';
import {TableComponent} from './components/table/table.component';
import {TableColumnDirective} from './components/table/column-directive/table-column.directive';
import SockJS from "sockjs-client";
import {environment} from "@environment/environment";
import {Client} from "@stomp/stompjs";

export const STOMP_CLIENT = new InjectionToken('STOMP_CLIENT');

@NgModule({ declarations: [
        DefaultLayoutComponent,
        LoadedContentComponent,
        PageHeaderComponent,
        ContainedLayoutComponent,
        TableComponent,
        TableColumnDirective,
    ],
    exports: [
        DefaultLayoutComponent,
        LoadedContentComponent,
        PageHeaderComponent,
        TableComponent,
        TableColumnDirective
    ], imports: [CommonModule,
        NgbModule,
        FormsModule,
        RouterModule], providers: [
        {
            provide: STOMP_CLIENT,
            useValue: new Client({
                webSocketFactory: () => new SockJS(environment.apiUrl + '/messages')
            }),
        },
        provideHttpClient(withInterceptorsFromDi())
    ] })
export class SharedModule {
}
