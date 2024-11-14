import {Component, Input, OnInit} from '@angular/core';
import {FileDiscoveredMessage} from "@backend";

@Component({
  selector: 'app-new-discovered-popup',
  templateUrl: './new-discovered-popup.component.html',
  styleUrls: ['./new-discovered-popup.component.scss']
})
export class NewDiscoveredPopupComponent implements OnInit {

  @Input()
  newestDiscovered?: FileDiscoveredMessage;

  constructor() { }

  ngOnInit(): void {
    // Nothing to initialize
  }
}
