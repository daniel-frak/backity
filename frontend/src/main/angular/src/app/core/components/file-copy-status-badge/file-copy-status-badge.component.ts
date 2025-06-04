import {Component, Input, OnInit} from '@angular/core';
import {FileCopyStatus} from "@backend";
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-file-copy-status-badge',
  templateUrl: './file-copy-status-badge.component.html',
  styleUrls: ['./file-copy-status-badge.component.scss'],
  standalone: true,
  imports: [CommonModule]
})
export class FileCopyStatusBadgeComponent implements OnInit {

  @Input()
  public status: FileCopyStatus | undefined = undefined;

  constructor() {
  }

  ngOnInit(): void {
    // Nothing to initialize
  }

  public readonly FileCopyStatus = FileCopyStatus;

  getBadgeClass() {
    switch (this.status) {
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
