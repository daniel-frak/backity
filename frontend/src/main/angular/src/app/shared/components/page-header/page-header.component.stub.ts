import {Component, Input, OnInit} from '@angular/core';

// noinspection AngularMissingOrInvalidDeclarationInModule
@Component({
  selector: 'app-page-header',
  standalone: true,
} as any)
export class PageHeaderStubComponent implements OnInit {

  @Input()
  title?: string;

  constructor() { }

  ngOnInit(): void {
    // Nothing to initialize
  }
}
