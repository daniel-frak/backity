import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-page-header'
} as any)
export class PageHeaderStubComponent implements OnInit {

  @Input()
  title?: string;

  constructor() { }

  ngOnInit(): void {
    // Nothing to initialize
  }
}
