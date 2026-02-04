import {Injectable, signal} from '@angular/core';
import {UserNotification} from "@app/shared/services/notification/userNotification";

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  private readonly _notifications = signal<UserNotification[]>([]);
  readonly notifications = this._notifications.asReadonly();

  private readonly SUCCESS_NOTIFICATION_CLASSES = 'bg-success text-light';
  private readonly FAILURE_NOTIFICATION_CLASSES = 'bg-danger text-light';

  show(content: string, title?: string) {
    this._notifications.update(n => [
      {
        title,
        content,
        shouldShowLightText: false
      },
      ...n
    ]);
  }

  showSuccess(content: string, title?: string) {
    this._notifications.update(existingNotifications => {
      return [
        {
          title,
          content,
          class: this.SUCCESS_NOTIFICATION_CLASSES,
          shouldShowLightText: true
        },
        ...existingNotifications
      ];
    });
  }

  showFailure(content: string, ...optionalParams: any[]) {
    this._notifications.update(existingNotifications => [
      {
        content,
        class: this.FAILURE_NOTIFICATION_CLASSES,
        shouldShowLightText: true
      },
      ...existingNotifications
    ]);
    console.error(content, ...optionalParams);
  }

  remove(notification: UserNotification) {
    this._notifications.update(existingNotifications => existingNotifications
      .filter(t => t != notification));
  }
}
