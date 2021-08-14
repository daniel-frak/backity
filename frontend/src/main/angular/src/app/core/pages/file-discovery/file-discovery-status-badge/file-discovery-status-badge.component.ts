import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-file-discovery-status-badge',
  templateUrl: './file-discovery-status-badge.component.html',
  styleUrls: ['./file-discovery-status-badge.component.scss']
})
export class FileDiscoveryStatusBadgeComponent implements OnInit {

  @Input()
  public discoveryOngoing: boolean = false;

  constructor() { }

  ngOnInit(): void {
  }

}
