import {Component, Input, OnInit} from '@angular/core';
import {GameFileDetails} from "@backend";

@Component({
  selector: 'app-new-discovered-files-badge',
  templateUrl: './new-discovered-files-badge.component.html',
  styleUrls: ['./new-discovered-files-badge.component.scss']
})
export class NewDiscoveredFilesBadgeComponent implements OnInit {

  @Input()
  newDiscoveredCount: number = 0;

  @Input()
  newestDiscovered?: GameFileDetails;

  constructor() { }

  ngOnInit(): void {
  }

}
