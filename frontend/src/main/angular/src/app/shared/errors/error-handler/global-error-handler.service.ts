import {ErrorHandler, Injectable, NgZone} from '@angular/core';
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {HttpErrorResponse} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class GlobalErrorHandler implements ErrorHandler {

  constructor(
    private readonly notificationService: NotificationService,
    private readonly zone: NgZone) {
  }

  handleError(error: any): void {
    if (!(error instanceof HttpErrorResponse)) {
      this.handleStandardError(error);
    } else {
      this.handleHttpRequestError(error);
    }
  }

  private handleStandardError(error: any) {
    this.zone.run(() =>
      this.notificationService.showFailure('An unexpected error has occurred', undefined, error));
  }

  private handleHttpRequestError(error: HttpErrorResponse) {
    const message = this.getErrorMessage(error);
    this.zone.run(() => this.notificationService.showFailure(message, undefined, error));
  }

  private getErrorMessage(error: HttpErrorResponse): string {
    if (error?.error?.messageKey) {
      return error.error.message;
    }

    switch (error.status) {
      case 400:
        return "The request couldn't be processed. Please check the information you entered and try again.";
      case 401:
        return "You're not authorized to perform this action.";
      case 404:
        return "The requested resource was not found.";
      case 405:
        return "This action is not supported.";
      case 408:
        return "The request timed out. Please ensure your network is stable and try again.";
      default:
        if (error.status >= 500 && error.status < 600) {
          return "An unexpected server error has occurred.";
        }
        return `An unexpected network error has occurred (status: ${error.status}).`;
    }
  }
}
