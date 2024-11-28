import {ErrorHandler, Injectable, NgZone} from '@angular/core';
import {NotificationService} from "@app/shared/services/notification/notification.service";

@Injectable({
  providedIn: 'root'
})
export class GlobalErrorHandler implements ErrorHandler {

  constructor(
    private readonly notificationService: NotificationService,
    private readonly zone: NgZone) {
  }

  handleError(error: any): void {
    this.zone.run(() =>
      this.notificationService.showFailure('An unexpected error has occurred', undefined, error));
  }
}
