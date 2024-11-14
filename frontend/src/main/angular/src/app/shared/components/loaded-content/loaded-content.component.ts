import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-loaded-content',
  templateUrl: './loaded-content.component.html',
  styleUrls: ['./loaded-content.component.scss']
})
export class LoadedContentComponent implements OnInit {

  @Input()
  isLoading: boolean = true;

  constructor() { }

  ngOnInit(): void {
    // Nothing to initialize
  }
}
