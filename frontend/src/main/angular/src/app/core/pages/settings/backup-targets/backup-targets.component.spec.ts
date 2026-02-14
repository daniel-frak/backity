import {ComponentFixture, TestBed} from '@angular/core/testing';

import {BackupTargetsComponent} from './backup-targets.component';
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {ModalService} from "@app/shared/services/modal-service/modal.service";
import {BackupTarget, BackupTargetsClient} from "@backend";
import {of, throwError} from "rxjs";
import {TestBackupTarget} from "@app/shared/testing/objects/test-backup-target";
import {DebugElement, signal} from "@angular/core";
import {By} from "@angular/platform-browser";
import {
  AddBackupTargetModalComponent
} from "@app/core/pages/settings/backup-targets/add-backup-target-modal/add-backup-target-modal.component";
import {
  EditBackupTargetModalComponent
} from "@app/core/pages/settings/backup-targets/edit-backup-target-modal/edit-backup-target-modal.component";
import {ModalRefMock} from "@app/shared/testing/modals/modal-ref-mock";
import {ModalServiceStub} from "@app/shared/testing/modals/modal-service-stub";
import SpyObj = jasmine.SpyObj;
import createSpyObj = jasmine.createSpyObj;

describe('BackupTargetsComponent', () => {
  let component: BackupTargetsComponent;
  let fixture: ComponentFixture<BackupTargetsComponent>;

  let backupTargetsClient: SpyObj<BackupTargetsClient>;
  let notificationService: SpyObj<NotificationService>;
  let modalService: ModalServiceStub;

  class Page {

    get addBackupTargetBtn(): HTMLButtonElement {
      return this.getElementByTestId('add-backup-target-btn') as HTMLButtonElement;
    }

    editBackupTargetBtn(backupTargetId: string): HTMLButtonElement {
      return this.getElementByTestId('edit-backup-target-btn-' + backupTargetId) as HTMLButtonElement;
    }

    deleteBackupTargetBtn(backupTargetId: string): HTMLButtonElement {
      return this.getElementByTestId('delete-backup-target-btn-' + backupTargetId) as HTMLButtonElement;
    }

    private getElementByTestId(testId: string): HTMLElement {
      const debugElement: DebugElement = fixture.debugElement.query(By.css('[data-testid="' + testId + '"]'));
      return debugElement.nativeElement;
    }
  }

  let page: Page;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BackupTargetsComponent],
      providers: [
        {
          provide: BackupTargetsClient,
          useValue: createSpyObj('BackupTargetsClient', ['getBackupTargets'])
        },
        {
          provide: NotificationService,
          useValue: createSpyObj('NotificationService', ['showSuccess', 'showFailure'])
        },
        {provide: ModalService, useExisting: ModalServiceStub},
        ModalServiceStub
      ]
    })
      .compileComponents();

    backupTargetsClient = TestBed.inject(BackupTargetsClient) as SpyObj<BackupTargetsClient>;
    notificationService = TestBed.inject(NotificationService) as SpyObj<NotificationService>;
    modalService = TestBed.inject(ModalServiceStub)

    modalService.registerModal(AddBackupTargetModalComponent, anAddBackupTargetModalComponentStub());
    modalService.registerModal(EditBackupTargetModalComponent, anEditBackupTargetModalComponentStub());

    page = new Page();

    fixture = TestBed.createComponent(BackupTargetsComponent);
    component = fixture.componentInstance;
  });

  function anAddBackupTargetModalComponentStub(): AddBackupTargetModalComponent {
    return {} as AddBackupTargetModalComponent;
  }

  function anEditBackupTargetModalComponentStub(): EditBackupTargetModalComponent {
    return {
      backupTarget: signal<BackupTarget | undefined>(undefined)
    } as any;
  }

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('init', () => {

    it('should disable loading on init', async () => {
      await fixture.whenStable();

      expect(component.backupTargetsAreLoading()).toBeFalse();
    });

    it('should retrieve Backup Targets on init', async () => {
      const backupTarget: BackupTarget = TestBackupTarget.localFolder();
      exists(backupTarget);

      await fixture.whenStable();

      expect(component.backupTargets()).toEqual([backupTarget]);
    });
  });

  function exists(backupTarget: BackupTarget) {
    backupTargetsClient.getBackupTargets.and.returnValue(of([backupTarget]) as any);
  }

  describe('refresh()', () => {

    it('should not refresh Backup Targets given already loading', async () => {
      component.backupTargetsAreLoading.set(true);

      await component.refresh();

      expect(backupTargetsClient.getBackupTargets).not.toHaveBeenCalled();
    })

    it('should refresh Backup Targets given not loading', async () => {
      component.backupTargetsAreLoading.set(false);

      await component.refresh();

      expect(backupTargetsClient.getBackupTargets).toHaveBeenCalled();
    })
  })

  describe('Backup Target retrieval', () => {

    it('should show failure notification given Backup Target retrieval fails', async () => {
      const mockError = new Error('test error');
      backupTargetRetrievalThrows(mockError);

      await fixture.whenStable();

      expect(notificationService.showFailure).toHaveBeenCalledWith('Error fetching backup targets', mockError);
    })

    function backupTargetRetrievalThrows(error: Error) {
      backupTargetsClient.getBackupTargets.and.returnValue(throwError(() => error));
    }

    it('should disable loading given Backup Target retrieval fails', async () => {
      const mockError = new Error('test error');
      backupTargetRetrievalThrows(mockError);

      await fixture.whenStable();

      expect(notificationService.showFailure).toHaveBeenCalledWith('Error fetching backup targets', mockError);
      expect(component.backupTargetsAreLoading()).toBeFalse();
    });
  });

  describe('Adding Backup Targets', () => {

    it('should open add backup target modal when user clicks add button', async () => {
      await fixture.whenStable();
      fixture.detectChanges();
      const modalRef: ModalRefMock<AddBackupTargetModalComponent> =
        modalService.getModalRef(AddBackupTargetModalComponent);
      modalRef.rejectWhenOpened();

      page.addBackupTargetBtn.click();

      expect(modalRef.timesOpened).toBe(1);
    });

    it('should not open add backup target modal twice', async () => {
      await fixture.whenStable();
      fixture.detectChanges();
      const modalRef: ModalRefMock<AddBackupTargetModalComponent> =
        modalService.getModalRef(AddBackupTargetModalComponent);

      void component.onClickShowAddModal()();
      void component.onClickShowAddModal()();
      await fixture.whenStable();

      expect(modalRef.timesOpened).toBe(1);
    });

    it('should refresh backup targets when user adds new backup target', async () => {
      await fixture.whenStable();
      fixture.detectChanges();
      modalService.getModalRef(AddBackupTargetModalComponent).resolveWhenOpened(true);
      backupTargetsClient.getBackupTargets.calls.reset();

      page.addBackupTargetBtn.click();
      await fixture.whenStable();

      expect(backupTargetsClient.getBackupTargets).toHaveBeenCalled();
    });
  });

  describe('Editing Backup Targets', () => {

    it('should open edit backup target modal when user clicks edit button', async () => {
      const backupTarget: BackupTarget = TestBackupTarget.localFolder();
      exists(backupTarget);
      await fixture.whenStable();
      fixture.detectChanges();
      const modalRef: ModalRefMock<EditBackupTargetModalComponent> =
        modalService.getModalRef(EditBackupTargetModalComponent);
      modalRef.rejectWhenOpened()

      page.editBackupTargetBtn(backupTarget.id).click();

      expect(modalRef.timesOpened).toBe(1);
      expect(modalRef.componentInstance.backupTarget()).toEqual(backupTarget);
    });

    it('should not open edit backup target modal twice', async () => {
      const backupTarget: BackupTarget = TestBackupTarget.localFolder();
      exists(backupTarget);
      await fixture.whenStable();
      fixture.detectChanges();
      const modalRef: ModalRefMock<EditBackupTargetModalComponent> =
        modalService.getModalRef(EditBackupTargetModalComponent);

      void component.onClickShowEditModal(backupTarget)();
      void component.onClickShowEditModal(backupTarget)();
      await fixture.whenStable();

      expect(modalRef.timesOpened).toBe(1);
    });

    it('should refresh backup targets when user edits a backup target', async () => {
      const backupTarget: BackupTarget = TestBackupTarget.localFolder();
      exists(backupTarget);
      await fixture.whenStable();
      fixture.detectChanges();
      modalService.getModalRef(EditBackupTargetModalComponent).resolveWhenOpened(true);
      backupTargetsClient.getBackupTargets.calls.reset();

      page.editBackupTargetBtn(backupTarget.id).click();
      await fixture.whenStable();

      expect(backupTargetsClient.getBackupTargets).toHaveBeenCalled();
    });
  });

  describe('Deleting Backup Targets', () => {

    it('should fail to delete backup target when user clicks delete button', async () => {
      const backupTarget: BackupTarget = TestBackupTarget.localFolder();
      exists(backupTarget);
      await fixture.whenStable();
      fixture.detectChanges();

      page.deleteBackupTargetBtn(backupTarget.id).click();

      expect(notificationService.showFailure).toHaveBeenCalledWith('Backup target deletion not yet implemented.');
    });

    it('should show notification given backup target deletion fails', async () => {
      const backupTarget: BackupTarget = TestBackupTarget.localFolder();
      exists(backupTarget);
      await fixture.whenStable();
      fixture.detectChanges();
      const error = new Error('Test error');
      notificationService.showFailure.and.stub();
      notificationService.showFailure.withArgs('Backup target deletion not yet implemented.')
        .and.throwError(error);

      page.deleteBackupTargetBtn(backupTarget.id).click();
      await fixture.whenStable();

      expect(notificationService.showFailure).toHaveBeenCalledWith(
        'An error occurred while trying to delete a backup target', backupTarget.id, error);
    });
  });
});
