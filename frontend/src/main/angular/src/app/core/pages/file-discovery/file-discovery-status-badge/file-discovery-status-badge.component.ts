import {Component, Input, OnInit} from '@angular/core';
import {FileDiscoveryStatus} from "@backend";
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-file-discovery-status-badge',
  templateUrl: './file-discovery-status-badge.component.html',
  styleUrls: ['./file-discovery-status-badge.component.scss'],
  host: {'data-testid': 'file-discovery-status-badge'},
  standalone: true,
  imports: [CommonModule]
})
export class FileDiscoveryStatusBadgeComponent implements OnInit {

  @Input()
  public status: FileDiscoveryStatus = {};

  constructor() {
  }

  ngOnInit(): void {
    // Nothing to initialize
  }
}
