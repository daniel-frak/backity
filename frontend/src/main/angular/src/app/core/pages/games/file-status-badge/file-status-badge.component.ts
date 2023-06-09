import {Component, Input, OnInit} from '@angular/core';
import {FileBackupStatus} from "@backend";

@Component({
  selector: 'app-file-status-badge',
  templateUrl: './file-status-badge.component.html',
  styleUrls: ['./file-status-badge.component.scss']
})
export class FileStatusBadgeComponent implements OnInit {

  @Input()
  public status: FileBackupStatus | undefined = FileBackupStatus.Discovered;

  constructor() {
  }

  ngOnInit(): void {
  }

  public readonly FileBackupStatus = FileBackupStatus;

  getBadgeClass() {
    switch (this.status) {
      case FileBackupStatus.Success:
        return 'bg-success';
      case FileBackupStatus.InProgress:
        return 'bg-info';
      case FileBackupStatus.Failed:
        return 'bg-danger';
      case FileBackupStatus.Enqueued:
        return 'bg-warning';
      default:
        return 'bg-secondary'
    }
  }
}
