import {ChangeDetectionStrategy, Component, input} from '@angular/core';
import {NgbPopover} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-page-header',
  templateUrl: './page-header.component.html',
  styleUrl: './page-header.component.scss',
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [NgbPopover]
})
export class PageHeaderComponent {

  readonly title = input<string>("No title");
}
