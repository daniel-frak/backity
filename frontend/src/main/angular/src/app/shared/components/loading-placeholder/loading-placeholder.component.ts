import { Component, Input } from '@angular/core';
import {NgForOf, NgStyle} from "@angular/common";

@Component({
  selector: 'app-loading-placeholder',
  standalone: true,
  imports: [
    NgStyle,
    NgForOf
  ],
  templateUrl: './loading-placeholder.component.html',
  styleUrl: './loading-placeholder.component.scss'
})
export class LoadingPlaceholderComponent {

  @Input() widths: string[] = ['6rem'];
}
