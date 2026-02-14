import {ModalRefMock} from "@app/shared/testing/modals/modal-ref-mock";
import {ModalService} from "@app/shared/services/modal-service/modal.service";

/**
 * Stub for {@link ModalService}.
 * <p>
 * Simplifies testing of modals.
 */
export class ModalServiceStub {

  private readonly modalRefStubsByComponentType = new Map<any, ModalRefMock>();

  registerModal<T>(componentType: new (...args: any[]) => T, componentInstance: T) {
    this.modalRefStubsByComponentType.set(
      componentType,
      new ModalRefMock(componentInstance)
    );
  }

  open<T>(componentType: new (...args: any[]) => T): ModalRefMock<T> {
    const modalRef = this.modalRefStubsByComponentType.get(componentType);
    if (!modalRef) {
      throw new Error(`No ModalRefMock registered for ${componentType.name}`);
    }

    modalRef.resolveOrReject();

    return modalRef as ModalRefMock<T>;
  }

  getModalRef<T>(componentType: new (...args: any[]) => T): ModalRefMock<T> {
    const modalRef = this.modalRefStubsByComponentType.get(componentType);
    if (!modalRef) {
      throw new Error(`No ModalRefMock registered for ${componentType.name}`);
    }
    return modalRef;
  }

  async withConfirmationModal(message: string, callback: () => Promise<void>): Promise<void> {
    // Auto-confirm modal:
    return callback();
  }
}
