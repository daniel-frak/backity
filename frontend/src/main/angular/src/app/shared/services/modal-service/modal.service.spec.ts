import {TestBed} from '@angular/core/testing';
import {ModalService} from './modal.service';
import {NgbModal, NgbModalRef} from '@ng-bootstrap/ng-bootstrap';
import {
  ConfirmationModalComponent
} from '@app/shared/components/modals/confirmation-modal/confirmation-modal.component';
import {ModalRef} from "@app/shared/services/modal-service/modal-ref";
import {Mocked} from "vitest";

describe('ModalService', () => {
  let service: ModalService;
  let ngbModal: Mocked<NgbModal>;

  beforeEach(() => {
    const modalServiceMock = {
      open: vi.fn()
    };

    TestBed.configureTestingModule({
      providers: [
        ModalService,
        {provide: NgbModal, useValue: modalServiceMock}
      ]
    });

    service = TestBed.inject(ModalService);
    ngbModal = TestBed.inject(NgbModal) as any;
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('open()', () => {

    it('should return a ModalRef', () => {
      const ngbModalRef: NgbModalRef = {
        result: vi.fn()
      } as unknown as NgbModalRef;
      ngbModal.open.mockReturnValue(ngbModalRef as any);
      const content = {} as any;

      const result: ModalRef = service.open(content);

      expect(result).toEqual(ngbModalRef);
    });
  });

  describe('withConfirmationModal()', () => {

    it('should open the confirmation modal with the correct message', async () => {
      const mockModalRef: NgbModalRef = mockNgbModalConfirmationAfterOpening();
      const callback = vi.fn();

      await service.withConfirmationModal('Test message', callback);

      expect(ngbModal.open).toHaveBeenCalledWith(ConfirmationModalComponent);
      expect((mockModalRef.componentInstance as any).message.set).toHaveBeenCalledWith('Test message');
    });

    function mockNgbModalConfirmationAfterOpening(): NgbModalRef {
      const mockMessage = {
        set: vi.fn()
      };
      const mockModalRef = {
        componentInstance: {
          message: mockMessage
        },
        result: Promise.resolve(true)
      } as any;

      ngbModal.open.mockReturnValue(mockModalRef);

      return mockModalRef;
    }

    it('should execute the callback if modal is confirmed', async () => {
      mockNgbModalConfirmationAfterOpening();
      const callback = vi.fn().mockResolvedValue(undefined);

      await service.withConfirmationModal('Test message', callback);

      expect(callback).toHaveBeenCalled();
    });

    it('should not execute the callback if the modal is dismissed', async () => {
      mockNgbModalCancellationAfterOpening();
      const callback = vi.fn().mockResolvedValue(undefined);

      await service.withConfirmationModal('Test message', callback);

      expect(callback).not.toHaveBeenCalled();
    });

    function mockNgbModalCancellationAfterOpening(): NgbModalRef {
      const mockModalRef = {
        componentInstance: {
          message: {
            set: vi.fn()
          }
        },
        result: Promise.resolve(false)
      } as any;
      ngbModal.open.mockReturnValue(mockModalRef);

      return mockModalRef;
    }

    it('should not call the callback on modal dismissal (rejected promise)', async () => {
      mockNgbModalDismissalAfterOpening();
      const callback = vi.fn().mockResolvedValue(undefined);

      await (service.withConfirmationModal('Test message', callback));

      expect(callback).not.toHaveBeenCalled();
    });

    function mockNgbModalDismissalAfterOpening(): NgbModalRef {
      const mockModalRef = {
        componentInstance: {
          message: {
            set: vi.fn()
          }
        },
        result: Promise.reject('dismiss')
      } as any;
      ngbModal.open.mockReturnValue(mockModalRef);

      return mockModalRef;
    }
  });
});
