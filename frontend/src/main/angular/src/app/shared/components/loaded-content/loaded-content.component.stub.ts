import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-loaded-content',
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
