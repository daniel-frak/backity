import {Component, Input} from '@angular/core';
import {GameContentDiscoveryOverview} from "@backend";

@Component({
  selector: 'app-gog-auth',
  standalone: true,
  template: ''
})
export class GogAuthComponentStub {

  @Input()
  externalDataIsLoading: boolean = false;

  @Input()
  overview?: GameContentDiscoveryOverview;
}
