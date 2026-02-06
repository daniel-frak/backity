import {Component, input} from '@angular/core';


@Component({
  selector: 'app-loaded-content',
  templateUrl: './loaded-content.component.html',
  styleUrl: './loaded-content.component.scss',
  imports: []
})
export class LoadedContentComponent {

  readonly isLoading = input<boolean>(true);
}
