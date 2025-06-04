import {Component, Input} from '@angular/core';

@Component({
  selector: 'app-game-file-version-badge',
  standalone: true,
  imports: [],
  templateUrl: './game-file-version-badge.component.html',
  styleUrl: './game-file-version-badge.component.scss'
})
export class GameFileVersionBadgeComponent {

  @Input() version: string = '';
}
