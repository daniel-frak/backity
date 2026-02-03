import {Component, input} from '@angular/core';
import {StorageSolutionStatus} from "@backend";
import {NgClass} from "@angular/common";

@Component({
    selector: 'app-storage-solution-status-badge',
    imports: [
        NgClass
    ],
    templateUrl: './storage-solution-status-badge.component.html',
    styleUrl: './storage-solution-status-badge.component.scss'
})
export class StorageSolutionStatusBadgeComponent {

  readonly status = input<StorageSolutionStatus>();

  getBadgeClass(): string {
    switch (this.status()) {
      case StorageSolutionStatus.Connected:
        return 'bg-secondary';
      case StorageSolutionStatus.NotConnected:
        return 'bg-danger';
      default:
        return 'bg-secondary';
    }
  }
}
