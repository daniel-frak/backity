import {Component, Input, OnInit} from '@angular/core';
import {GameFileDiscoveredEvent} from "@backend";
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-new-discovered-popup',
  templateUrl: './new-discovered-popup.component.html',
  styleUrls: ['./new-discovered-popup.component.scss'],
  standalone: true,
  imports: [CommonModule]
})
export class NewDiscoveredPopupComponent implements OnInit {

  @Input()
  newestDiscovered?: GameFileDiscoveredEvent;

  constructor() {
  }

  ngOnInit(): void {
    // Nothing to initialize
  }
}
