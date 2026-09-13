import {ChangeDetectionStrategy, Component} from '@angular/core';

@Component({
  selector: 'app-auto-layout',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.Eager,
  template: '<ng-content />'
})
export class AutoLayoutStubComponent {
}
