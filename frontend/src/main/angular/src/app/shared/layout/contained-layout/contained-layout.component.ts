import {ChangeDetectionStrategy, Component} from '@angular/core';
import {RouterOutlet} from '@angular/router';

@Component({
  selector: 'app-contained-layout',
  templateUrl: './contained-layout.component.html',
  styleUrl: './contained-layout.component.scss',
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [RouterOutlet]
})
export class ContainedLayoutComponent {
}
