import {Component, Input} from '@angular/core';
import {CommonModule} from '@angular/common';
import {GameContentDiscoveryOutcome} from "@backend";

@Component({
    selector: 'app-game-content-discovery-outcome-badge',
    templateUrl: './game-content-discovery-outcome-badge.component.html',
    styleUrls: ['./game-content-discovery-outcome-badge.component.scss'],
    host: { 'data-testid': 'game-content-discovery-status-badge' },
    imports: [CommonModule]
})
export class GameContentDiscoveryOutcomeBadgeComponent {

  @Input()
  public status: GameContentDiscoveryOutcome | undefined = undefined;
}
