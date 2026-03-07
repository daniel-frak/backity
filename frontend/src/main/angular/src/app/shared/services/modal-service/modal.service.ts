import {Injectable} from '@angular/core';
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {
  ConfirmationModalComponent
} from "@app/shared/components/modals/confirmation-modal/confirmation-modal.component";
import {ModalRef} from "@app/shared/services/modal-service/modal-ref";

@Injectable({
  providedIn: 'root'
})
export class ModalService {

  constructor(private readonly ngbModal: NgbModal) {
  }

  public async withConfirmationModal(message: string, callback: () => Promise<void>): Promise<void>{
    const ngbModalRef = this.ngbModal.open(ConfirmationModalComponent);
    (ngbModalRef.componentInstance as ConfirmationModalComponent).message.set(message);
    return ngbModalRef.result.then(async (result) => {
      if (result) {
        await callback();
      }
    }, () => {
      // Do nothing
    });
  }

  public open(content: any): ModalRef {
    return this.ngbModal.open(content);
  }
}
