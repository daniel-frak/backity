import {Component} from '@angular/core';
import {FormsModule} from '@angular/forms';

@Component({
    selector: 'app-theme',
    templateUrl: './theme.component.html',
    styleUrls: ['./theme.component.scss'],
    imports: [FormsModule]
})
export class ThemeComponent {

  constructor() {
  }
}
