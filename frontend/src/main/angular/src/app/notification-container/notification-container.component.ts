import { Component } from '@angular/core';
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {NgbToast} from "@ng-bootstrap/ng-bootstrap";
import {NgClass, NgIf} from "@angular/common";

@Component({
  selector: 'app-notification-container',
  standalone: true,
  imports: [
    NgbToast,
    NgIf,
    NgClass
  ],
  templateUrl: './notification-container.component.html',
  styleUrl: './notification-container.component.scss'
})
export class NotificationContainerComponent {

  constructor(public notificationService: NotificationService) {
  }
}
