import {Component, input} from '@angular/core';
import {NgStyle} from "@angular/common";

@Component({
  selector: 'app-progress-bar',
  imports: [
    NgStyle
  ],
  templateUrl: './progress-bar.component.html',
  styleUrl: './progress-bar.component.scss'
})
export class ProgressBarComponent {

  readonly percentage = input<number>(0);
}
