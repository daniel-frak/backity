import {Injectable} from '@angular/core';
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {
  ConfirmationModalComponent
} from "@app/shared/components/modals/confirmation-modal/confirmation-modal.component";

@Injectable({
  providedIn: 'root'
})
export class ModalService {

  constructor(private readonly modalService: NgbModal) {
  }

  public async withConfirmationModal(message: string, callback: () => Promise<void>): Promise<void>{
    let ngbModalRef = this.modalService.open(ConfirmationModalComponent);
    (ngbModalRef.componentInstance as ConfirmationModalComponent).message = message;
    return ngbModalRef.result.then(async (result) => {
      if (result) {
        await callback();
      }
    }, () => {
      // Do nothing
    });
  }
}
