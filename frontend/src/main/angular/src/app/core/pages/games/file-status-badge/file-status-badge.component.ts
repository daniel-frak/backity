import {Component, Input, OnInit} from '@angular/core';
import {FileCopyStatus} from "@backend";
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-file-status-badge',
  templateUrl: './file-status-badge.component.html',
  styleUrls: ['./file-status-badge.component.scss'],
  standalone: true,
  imports: [CommonModule]
})
export class FileStatusBadgeComponent implements OnInit {

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
        return 'bg-info';
      default:
        return 'bg-secondary'
    }
  }
}
