import {Component} from '@angular/core';
import {NgbModalConfig} from "@ng-bootstrap/ng-bootstrap";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {

  constructor(private readonly config: NgbModalConfig) {
    config.ariaLabelledBy = 'modal-title';
    config.centered = true;
  }
}
