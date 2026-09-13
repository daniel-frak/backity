import {ChangeDetectionStrategy, Component} from '@angular/core';

@Component({
  selector: 'app-files',
  templateUrl: './files.component.html',
  styleUrl: './files.component.scss',
  changeDetection: ChangeDetectionStrategy.Eager,
  standalone: true
})
export class FilesComponent {
}
