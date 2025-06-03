import {Component, Input, OnInit} from '@angular/core';
import {GameFileDiscoveredEvent} from "@backend";
import {NgbPopover} from '@ng-bootstrap/ng-bootstrap';
import {NewDiscoveredPopupComponent} from './new-discovered-popup/new-discovered-popup.component';

@Component({
  selector: 'app-new-discovered-game-files-badge',
  templateUrl: './new-discovered-files-badge.component.html',
  styleUrls: ['./new-discovered-files-badge.component.scss'],
  standalone: true,
  imports: [NgbPopover, NewDiscoveredPopupComponent]
})
export class NewDiscoveredFilesBadgeComponent implements OnInit {

  @Input()
  newDiscoveredCount: number = 0;

  @Input()
  newestDiscovered?: GameFileDiscoveredEvent;

  constructor() {
  }

  ngOnInit(): void {
    // Nothing to initialize
  }
}
