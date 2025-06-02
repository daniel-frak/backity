import {Component, Input, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-loaded-content',
  templateUrl: './loaded-content.component.html',
  styleUrls: ['./loaded-content.component.scss'],
  standalone: true,
  imports: [CommonModule]
})
export class LoadedContentComponent implements OnInit {

  @Input()
  isLoading: boolean = true;

  @Input()
  hideIcon: boolean = false;

  constructor() {
  }

  ngOnInit(): void {
    // Nothing to initialize
  }
}
