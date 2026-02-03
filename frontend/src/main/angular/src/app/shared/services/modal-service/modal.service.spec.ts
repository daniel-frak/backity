import {TestBed} from '@angular/core/testing';
import {ModalService} from './modal.service';
import {NgbModal, NgbModalRef} from '@ng-bootstrap/ng-bootstrap';
import {
  ConfirmationModalComponent
} from '@app/shared/components/modals/confirmation-modal/confirmation-modal.component';
import SpyObj = jasmine.SpyObj;
import createSpyObj = jasmine.createSpyObj;
import createSpy = jasmine.createSpy;

describe('ModalService', () => {
  let service: ModalService;
  let modalService: SpyObj<NgbModal>;

  beforeEach(() => {
    const modalServiceMock = createSpyObj('NgbModal', ['open']);

    TestBed.configureTestingModule({
      providers: [
        ModalService,
        {provide: NgbModal, useValue: modalServiceMock}
      ]
    });

    service = TestBed.inject(ModalService);
    modalService = TestBed.inject(NgbModal) as SpyObj<NgbModal>;
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should open the confirmation modal with the correct message', async () => {
    const mockModalRef: NgbModalRef = mockNgbModalConfirmationAfterOpening();
    const callback = createSpy('callback');

    await service.withConfirmationModal('Test message', callback);

    expect(modalService.open).toHaveBeenCalledWith(ConfirmationModalComponent);
    expect((mockModalRef.componentInstance as any).message.val).toBe('Test message');
  });

  function mockNgbModalConfirmationAfterOpening(): NgbModalRef {
    const mockMessage = {
      set: createSpy('set').and.callFake(function(val: any) {
        mockMessage.val = val;
      }),
      val: 'Are you sure?'
    };
    const mockModalRef = {
      componentInstance: {
        message: mockMessage
      },
      result: Promise.resolve(true)
    } as any;

    modalService.open.and.returnValue(mockModalRef);

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
    modalService.open.and.returnValue(mockModalRef);

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
    modalService.open.and.returnValue(mockModalRef);

    return mockModalRef;
  }
});
