import {Component, input} from '@angular/core';
import {FileCopyStatus} from "@backend";
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-file-copy-status-badge',
  templateUrl: './file-copy-status-badge.component.html',
  styleUrl: './file-copy-status-badge.component.scss',
  imports: [CommonModule]
})
export class FileCopyStatusBadgeComponent {

  public readonly status = input<FileCopyStatus>();
  public readonly FileCopyStatus = FileCopyStatus;

  getBadgeClass() {
    switch (this.status()) {
      case FileCopyStatus.StoredIntegrityUnknown:
        return 'bg-success';
      case FileCopyStatus.InProgress:
        return 'bg-warning';
      case FileCopyStatus.Failed:
        return 'bg-danger';
      case FileCopyStatus.Enqueued:
        return 'bg-secondary';
      default:
        return 'bg-secondary'
    }
  }
}
