import {Component, OnInit, input} from '@angular/core';
import { NgbPopover } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'app-page-header',
    templateUrl: './page-header.component.html',
    styleUrls: ['./page-header.component.scss'],
    imports: [NgbPopover]
})
export class PageHeaderComponent implements OnInit {

  readonly title = input<string>("No title");

  constructor() { }

  ngOnInit(): void {
    // Nothing to initialize
  }
}
