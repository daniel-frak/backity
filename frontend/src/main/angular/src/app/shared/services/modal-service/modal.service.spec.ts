import {TestBed} from '@angular/core/testing';
import {ModalService} from './modal.service';
import {NgbModal, NgbModalRef} from '@ng-bootstrap/ng-bootstrap';
import {
  ConfirmationModalComponent
} from '@app/shared/components/modals/confirmation-modal/confirmation-modal.component';
import {ModalRef} from "@app/shared/services/modal-service/modal-ref";
import SpyObj = jasmine.SpyObj;
import createSpyObj = jasmine.createSpyObj;
import createSpy = jasmine.createSpy;

describe('ModalService', () => {
  let service: ModalService;
  let ngbModal: SpyObj<NgbModal>;

  beforeEach(() => {
    const modalServiceMock = createSpyObj('NgbModal', ['open']);

    TestBed.configureTestingModule({
      providers: [
        ModalService,
        {provide: NgbModal, useValue: modalServiceMock}
      ]
    });

    service = TestBed.inject(ModalService);
    ngbModal = TestBed.inject(NgbModal) as SpyObj<NgbModal>;
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('open()', () => {

    it('should return a ModalRef', () => {
      const ngbModalRef: NgbModalRef = createSpyObj('NgbModalRef', ['result']);
      ngbModal.open.and.returnValue(ngbModalRef as any);
      const content = {} as any;

      const result: ModalRef = service.open(content);

      expect(result).toEqual(ngbModalRef);
    })
  });

  describe('withConfirmationModal()', () => {

    it('should open the confirmation modal with the correct message', async () => {
      const mockModalRef: NgbModalRef = mockNgbModalConfirmationAfterOpening();
      const callback = createSpy('callback');

      await service.withConfirmationModal('Test message', callback);

      expect(ngbModal.open).toHaveBeenCalledWith(ConfirmationModalComponent);
      expect((mockModalRef.componentInstance as any).message.set).toHaveBeenCalledWith('Test message');
    });

    function mockNgbModalConfirmationAfterOpening(): NgbModalRef {
      const mockMessage = {
        set: createSpy('set')
      };
      const mockModalRef = {
        componentInstance: {
          message: mockMessage
        },
        result: Promise.resolve(true)
      } as any;

      ngbModal.open.and.returnValue(mockModalRef);

      return mockModalRef;
    }

    it('should execute the callback if modal is confirmed', async () => {
      mockNgbModalConfirmationAfterOpening();
      const callback = createSpy('callback').and.returnValue(Promise.resolve());

      await service.withConfirmationModal('Test message', callback);

      expect(callback).toHaveBeenCalled();
    });

    it('should not execute the callback if the modal is dismissed', async () => {
      mockNgbModalCancellationAfterOpening();
      const callback = createSpy('callback').and.returnValue(Promise.resolve());

      await service.withConfirmationModal('Test message', callback);

      expect(callback).not.toHaveBeenCalled();
    });

    function mockNgbModalCancellationAfterOpening(): NgbModalRef {
      const mockModalRef = {
        componentInstance: {
          message: {
            set: createSpy('set')
          }
        },
        result: Promise.resolve(false)
      } as any;
      ngbModal.open.and.returnValue(mockModalRef);

      return mockModalRef;
    }

    it('should not call the callback on modal dismissal (rejected promise)', async () => {
      mockNgbModalDismissalAfterOpening();
      const callback = createSpy('callback').and.returnValue(Promise.resolve());

      await (service.withConfirmationModal('Test message', callback));

      expect(callback).not.toHaveBeenCalled();
    });

    function mockNgbModalDismissalAfterOpening(): NgbModalRef {
      const mockModalRef = {
        componentInstance: {
          message: {
            set: createSpy('set')
          }
        },
        result: Promise.reject('dismiss')
      } as any;
      ngbModal.open.and.returnValue(mockModalRef);

      return mockModalRef;
    }
  });
});
