import {Component, Input, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-game-content-discovery-status-badge',
  templateUrl: './game-content-discovery-status-badge.component.html',
  styleUrls: ['./game-content-discovery-status-badge.component.scss'],
  host: {'data-testid': 'game-content-discovery-status-badge'},
  standalone: true,
  imports: [CommonModule]
})
export class GameContentDiscoveryStatusBadgeComponent implements OnInit {

  @Input()
  public status: 'SUCCESS' | 'UNKNOWN' | 'FAILED' = 'UNKNOWN';

  constructor() {
  }

  ngOnInit(): void {
    // Nothing to initialize
  }
}
