import {Component, Input} from '@angular/core';
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-section',
  standalone: true,
  imports: [
    NgIf
  ],
  templateUrl: './section.component.html',
  styleUrl: './section.component.scss'
})
export class SectionComponent {

  @Input() sectionTitle?: string;
}
