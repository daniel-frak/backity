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
}
