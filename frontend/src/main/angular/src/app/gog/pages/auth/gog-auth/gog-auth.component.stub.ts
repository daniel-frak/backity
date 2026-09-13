import {ChangeDetectionStrategy, Component, input} from '@angular/core';
import {GameContentDiscoveryOverview} from "@backend";

@Component({
  selector: 'app-gog-auth',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.Eager,
  template: ''
})
export class GogAuthComponentStub {

  readonly externalDataIsLoading = input<boolean>(false);

  readonly overview = input<GameContentDiscoveryOverview>();
}
