import {Component, Input} from '@angular/core';
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-icon-item',
  standalone: true,
  imports: [
    NgIf
  ],
  templateUrl: './icon-item.component.html',
  styleUrl: './icon-item.component.scss'
})
export class IconItemComponent {

  @Input() iconClass?: string;
}
