import {Component, Input, OnInit} from '@angular/core';
import { NgbPopover } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'app-page-header',
    templateUrl: './page-header.component.html',
    styleUrls: ['./page-header.component.scss'],
    imports: [NgbPopover]
})
export class PageHeaderComponent implements OnInit {

  @Input()
  title: string = "No title";

  constructor() { }

  ngOnInit(): void {
    // Nothing to initialize
  }
}
