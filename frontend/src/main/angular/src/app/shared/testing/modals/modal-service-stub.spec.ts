import {ModalServiceStub} from './modal-service-stub';
import {ModalRefMock} from '@app/shared/testing/modals/modal-ref-mock';

class DummyModalComponent {}

describe('ModalServiceStub', () => {
  let service: ModalServiceStub;
  let dummyInstance: DummyModalComponent;

  beforeEach(() => {
    service = new ModalServiceStub();
    dummyInstance = new DummyModalComponent();
  });

  it('should register a modal and allow retrieval', () => {
    service.registerModal(DummyModalComponent, dummyInstance);

    const modalRef: ModalRefMock<DummyModalComponent> = service.getModalRef(DummyModalComponent);
    expect(modalRef).toBeInstanceOf(ModalRefMock);
    expect(modalRef.componentInstance).toBe(dummyInstance);
  });

  it('should throw when getting a modal that is not registered', () => {
    expect(() => service.getModalRef(DummyModalComponent)).toThrowError(
      /No ModalRefMock registered/
    );
  });

  it('should open a registered modal and increment timesOpened', () => {
    service.registerModal(DummyModalComponent, dummyInstance);
    const modalRef1: ModalRefMock<DummyModalComponent> = service.getModalRef(DummyModalComponent);
    expect(modalRef1.timesOpened).toBe(0);

    const openedRef: ModalRefMock<DummyModalComponent> = service.open(DummyModalComponent);
    expect(openedRef).toBe(modalRef1);
    expect(openedRef.timesOpened).toBe(1);

    service.open(DummyModalComponent);
    expect(openedRef.timesOpened).toBe(2);
  });

  it('should throw when opening an unregistered modal', () => {
    expect(() => service.open(DummyModalComponent)).toThrowError(
      /No ModalRefMock registered/
    );
  });

  it('withConfirmationModal should run the callback and resolve', async () => {
    let callbackRan = false;
    await service.withConfirmationModal('Confirm?', async () => {
      callbackRan = true;
    });

    expect(callbackRan).toBeTrue();
  });

  it('withConfirmationModal should propagate errors thrown in the callback', async () => {
    const error = new Error('Test error');
    const callback = async () => {
      throw error;
    };

    await expectAsync(service.withConfirmationModal('Confirm?', callback)).toBeRejectedWith(error);
  });
});
