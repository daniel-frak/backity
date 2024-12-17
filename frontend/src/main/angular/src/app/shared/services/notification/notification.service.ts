import {Injectable} from '@angular/core';
import {UserNotification} from "@app/shared/services/notification/userNotification";

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  notifications: UserNotification[] = [];

  show(content: string, title?: string) {
    this.notifications.unshift({title, content, shouldShowLightText: false});
  }

  showSuccess(content: string, title?: string) {
    this.notifications.unshift({title, content, class: 'bg-success text-light', shouldShowLightText: true});
  }

  showFailure(content: string, ...optionalParams: any[]) {
    this.notifications.unshift({content, class: 'bg-danger text-light', shouldShowLightText: true});
    console.error(content, ...optionalParams);
  }

  remove(notification: UserNotification) {
    this.notifications = this.notifications.filter(t => t != notification);
  }
}
