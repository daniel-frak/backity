import {Component, Input} from '@angular/core';
import {GameContentDiscoveryProgressUpdateEvent} from "@backend";

@Component({
  selector: 'app-gog-auth',
  standalone: true,
  template: ''
})
export class GogAuthComponentStub {

  @Input()
  externalDataIsLoading: boolean = false;

  @Input()
  progress?: GameContentDiscoveryProgressUpdateEvent;
}
