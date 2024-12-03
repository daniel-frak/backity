import {Component, OnInit} from '@angular/core';
import {NotificationContainerComponent} from '@app/notification-container/notification-container.component';
import {RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {NgbCollapse} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-default-layout',
  templateUrl: './default-layout.component.html',
  styleUrls: ['./default-layout.component.scss'],
  standalone: true,
  imports: [NotificationContainerComponent, RouterLink, NgbCollapse, RouterLinkActive, RouterOutlet]
})
export class DefaultLayoutComponent implements OnInit {

  public isCollapsed = true;

  constructor() {
  }

  ngOnInit(): void {
    // Nothing to initialize
  }
}
