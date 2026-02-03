import {Injectable, signal} from '@angular/core';
import {UserNotification} from "@app/shared/services/notification/userNotification";

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private readonly _notifications = signal<UserNotification[]>([]);
  readonly notifications = this._notifications.asReadonly();

  show(content: string, title?: string) {
    this._notifications.update(n => [{title, content, shouldShowLightText: false}, ...n]);
  }

  showSuccess(content: string, title?: string) {
    this._notifications.update(n => [{title, content, class: 'bg-success text-light', shouldShowLightText: true}, ...n]);
  }

  showFailure(content: string, ...optionalParams: any[]) {
    this._notifications.update(n => [{content, class: 'bg-danger text-light', shouldShowLightText: true}, ...n]);
    console.error(content, ...optionalParams);
  }

  remove(notification: UserNotification) {
    this._notifications.update(n => n.filter(t => t != notification));
  }
}
