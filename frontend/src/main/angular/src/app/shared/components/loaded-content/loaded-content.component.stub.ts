import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-loaded-content'
} as any)
export class LoadedContentStubComponent implements OnInit {

  @Input()
  isLoading: boolean = true;

  constructor() { }

  ngOnInit(): void {
  }

}
