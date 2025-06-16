import {Component, Input} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";

@Component({
    selector: 'app-confirmation-modal',
    imports: [
        FormsModule,
    ],
    templateUrl: './confirmation-modal.component.html',
    styleUrl: './confirmation-modal.component.scss'
})
export class ConfirmationModalComponent {

  @Input() message: string = "Are you sure?";

  constructor(public readonly modal: NgbActiveModal) {
  }
}
