import {Component} from '@angular/core';
import {RouterOutlet} from '@angular/router';

@Component({
  selector: 'app-contained-layout',
  templateUrl: './contained-layout.component.html',
  styleUrl: './contained-layout.component.scss',
  imports: [RouterOutlet]
})
export class ContainedLayoutComponent {

  constructor() {
  }
}
