import {ApplicationConfig, ErrorHandler, provideZoneChangeDetection} from '@angular/core';
import {provideHttpClient, withInterceptorsFromDi} from "@angular/common/http";
import {provideRouter} from '@angular/router';
import {routes} from "@app/app.routes";
import {GlobalErrorHandler} from "@app/shared/errors/error-handler/global-error-handler.service";
import {rxStompServiceFactory} from "@app/shared/backend/services/rx-stomp/rx-stomp-service.factory";
import {RxStompService} from "@app/shared/backend/services/rx-stomp/rx-stomp.service";

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({eventCoalescing: true}),
    provideRouter(routes),
    provideHttpClient(withInterceptorsFromDi()),
    {
      provide: RxStompService,
      useFactory: rxStompServiceFactory,
    },
    {
      provide: ErrorHandler,
      useClass: GlobalErrorHandler,
    },
    {
      provide: 'Window',
      useValue: window
    }
  ]
};
