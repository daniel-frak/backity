import {Component, Input, OnInit} from '@angular/core';

// noinspection AngularMissingOrInvalidDeclarationInModule
@Component({
  selector: 'app-loaded-content',
  standalone: true,
  template: '<div *ngIf="!isLoading"><ng-content></ng-content></div>'
} as any)
export class LoadedContentStubComponent implements OnInit {

  @Input()
  isLoading: boolean = true;

  constructor() { }

  ngOnInit(): void {
    // Nothing to initialize
  }
}
