import {ComponentFixture, TestBed} from '@angular/core/testing';

import {EditBackupTargetModalComponent} from './edit-backup-target-modal.component';
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {By} from "@angular/platform-browser";
import {DebugElement} from "@angular/core";
import {BackupTarget, BackupTargetsClient} from "@backend";
import {TestBackupTarget} from "@app/shared/testing/objects/test-backup-target";
import {of, throwError} from "rxjs";
import {NgbActiveModalMock} from "@app/shared/testing/modals/ngb-active-modal-mock";
import {EditBackupTargetRequest} from "@backend/model/editBackupTargetRequest";
import SpyObj = jasmine.SpyObj;
import createSpyObj = jasmine.createSpyObj;

describe('EditBackupTargetModalComponent', () => {
  let component: EditBackupTargetModalComponent;
  let fixture: ComponentFixture<EditBackupTargetModalComponent>;

  let notificationService: SpyObj<NotificationService>;
  let modal: NgbActiveModalMock;
  let backupTargetsClient: SpyObj<BackupTargetsClient>;

  class Page {

    setInput(input: HTMLInputElement, value: string) {
      input.value = value;
      input.dispatchEvent(new Event('input'));
    }

    get closeButton(): HTMLButtonElement {
      return this.getElementByTestId('close-edit-backup-target-modal-btn') as HTMLButtonElement;
    }

    get formInputs(): HTMLInputElement[] {
      return fixture.debugElement
        .queryAll(By.css('[data-testid="edit-backup-target-form"] input'))
        .map(de => de.nativeElement as HTMLInputElement)
        .filter(el => !!el);
    }

    get nameInput(): HTMLInputElement {
      return this.getElementByTestId('name-input') as HTMLInputElement;
    }

    get submitButton(): HTMLButtonElement {
      return this.getElementByTestId('submit-edit-backup-target-btn') as HTMLButtonElement;
    }

    get form(): HTMLFormElement {
      return this.getElementByTestId('edit-backup-target-form') as HTMLFormElement;
    }

    private getElementByTestId(testId: string): HTMLElement {
      const debugElement: DebugElement =
        fixture.debugElement.query(By.css('[data-testid="' + testId + '"]'));
      return debugElement?.nativeElement;
    }

    submitForm() {
      this.form.dispatchEvent(new Event('submit'));
    }
  }

  let page: Page;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditBackupTargetModalComponent],
      providers: [
        {
          provide: NotificationService,
          useValue: createSpyObj('NotificationService', ['showSuccess', 'showFailure'])
        },
        {provide: NgbActiveModal, useExisting: NgbActiveModalMock},
        NgbActiveModalMock,
        {
          provide: BackupTargetsClient,
          useValue: createSpyObj('BackupTargetsClient', ['editBackupTarget'])
        }
      ]
    })
      .compileComponents();

    notificationService = TestBed.inject(NotificationService) as SpyObj<NotificationService>;
    modal = TestBed.inject(NgbActiveModalMock);
    backupTargetsClient = TestBed.inject(BackupTargetsClient) as SpyObj<BackupTargetsClient>;

    page = new Page();

    fixture = TestBed.createComponent(EditBackupTargetModalComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should close modal on close button click', () => {
    page.closeButton.click();

    expect(modal.timesDismissed).toBe(1);
  })

  it('should enable submit button given not loading', () => {
    component.isLoading.set(false);
    fixture.detectChanges();

    expect(page.submitButton.disabled).toBeFalse();
  });

  it('should disable submit button given loading', () => {
    component.isLoading.set(true);
    fixture.detectChanges();

    expect(page.submitButton.disabled).toBeTrue();
  });

  it('should show form inputs given not loading', () => {
    component.isLoading.set(false);
    fixture.detectChanges();

    expect(page.formInputs).not.toEqual([]);
  });

  it('should hide form inputs given loading', () => {
    component.isLoading.set(true);
    fixture.detectChanges();

    expect(page.formInputs).toEqual([]);
  });

  it('should not edit backup target on submit given form validation fails', async () => {
    await fixture.whenStable();
    page.setInput(page.nameInput, '');

    page.submitForm();

    expect(notificationService.showFailure)
      .not.toHaveBeenCalledWith("Editing backup targets is not yet implemented.");
    expect(modal.timesClosed).toBe(0);
    expect(component.isLoading()).toBeFalse();
  });

  function editingBackupTargetSucceeds() {
    backupTargetsClient.editBackupTarget.and.returnValue(of({}) as any);
  }

  it('should edit backup target on submit given form validation succeeds', async () => {
    await fixture.whenStable();
    const backupTarget: BackupTarget = TestBackupTarget.localFolder();
    component.backupTarget.set(backupTarget);
    const expectedRequest: EditBackupTargetRequest = {
      name: backupTarget.name,
    };
    editingBackupTargetSucceeds();

    page.setInput(page.nameInput, backupTarget.name);

    page.submitForm();

    expect(backupTargetsClient.editBackupTarget)
      .toHaveBeenCalledWith(backupTarget.id, expectedRequest);
    expect(notificationService.showSuccess)
      .toHaveBeenCalledWith("Backup target edited successfully");
    expect(modal.timesClosed).toBe(1);
    expect(component.isLoading()).toBeFalse();
  });

  it('should gracefully handle errors when editing backup target fails', async () => {
    const backupTarget: BackupTarget = TestBackupTarget.localFolder();
    component.backupTarget.set(backupTarget);
    const error = new Error('Test error');
    editingBackupTargetThrows(error);
    await fixture.whenStable();

    page.setInput(page.nameInput, backupTarget.name);

    page.submitForm();

    expect(notificationService.showFailure).toHaveBeenCalledWith(
      "Something went wrong when editing a Backup Target.", error);
    expect(modal.timesClosed).toBe(0);
    expect(component.isLoading()).toBeFalse();
  });

  function editingBackupTargetThrows(error: Error) {
    backupTargetsClient.editBackupTarget.and.returnValue(throwError(() => error));
  }
});
