import {Component, OnInit, input} from '@angular/core';


@Component({
    selector: 'app-loaded-content',
    templateUrl: './loaded-content.component.html',
    styleUrls: ['./loaded-content.component.scss'],
    imports: []
})
export class LoadedContentComponent implements OnInit {

  readonly isLoading = input<boolean>(true);

  constructor() {
  }

  ngOnInit(): void {
    // Nothing to initialize
  }
}
