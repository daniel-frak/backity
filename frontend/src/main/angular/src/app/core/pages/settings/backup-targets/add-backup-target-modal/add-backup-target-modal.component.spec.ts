import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AddBackupTargetModalComponent} from './add-backup-target-modal.component';
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {DebugElement} from "@angular/core";
import {By} from "@angular/platform-browser";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {NgbActiveModalMock} from "@app/shared/testing/modals/ngb-active-modal-mock";
import {BackupTarget, BackupTargetsClient} from "@backend";
import {AddBackupTargetResponse} from "@backend/model/addBackupTargetResponse";
import {AddBackupTargetRequest} from "@backend/model/addBackupTargetRequest";
import {TestBackupTarget} from "@app/shared/testing/objects/test-backup-target";
import {of, throwError} from "rxjs";
import SpyObj = jasmine.SpyObj;
import createSpyObj = jasmine.createSpyObj;

describe('AddBackupTargetModalComponent', () => {
  let component: AddBackupTargetModalComponent;
  let fixture: ComponentFixture<AddBackupTargetModalComponent>;

  let notificationService: SpyObj<NotificationService>;
  let modal: NgbActiveModalMock;
  let backupTargetsClient: SpyObj<BackupTargetsClient>;

  class Page {

    setInput(input: HTMLInputElement, value: string) {
      input.value = value;
      input.dispatchEvent(new Event('input'));
    }

    get closeButton(): HTMLButtonElement {
      return this.getElementByTestId('close-add-backup-target-modal-btn') as HTMLButtonElement;
    }

    get formInputs(): HTMLInputElement[] {
      return fixture.debugElement
        .queryAll(By.css('[data-testid="add-backup-target-form"] input'))
        .map(de => de.nativeElement as HTMLInputElement)
        .filter(el => !!el);
    }

    get nameInput(): HTMLInputElement {
      return this.getElementByTestId('name-input') as HTMLInputElement;
    }

    get storageSolutionIdInput(): HTMLInputElement {
      return this.getElementByTestId('storage-solution-id-input') as HTMLInputElement;
    }

    get pathTemplateInput(): HTMLInputElement {
      return this.getElementByTestId('path-template-input') as HTMLInputElement;
    }

    get submitButton(): HTMLButtonElement {
      return this.getElementByTestId('submit-new-backup-target-btn') as HTMLButtonElement;
    }

    get form(): HTMLFormElement {
      return this.getElementByTestId('add-backup-target-form') as HTMLFormElement;
    }

    private getElementByTestId(testId: string): HTMLElement {
      const addBackupTargetButtonDe: DebugElement = fixture.debugElement.query(By.css('[data-testid="' + testId + '"]'));
      return addBackupTargetButtonDe?.nativeElement;
    }

    submitForm() {
      this.form.dispatchEvent(new Event('submit'));
    }
  }

  let page: Page;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddBackupTargetModalComponent],
      providers: [
        {
          provide: NotificationService,
          useValue: createSpyObj('NotificationService', ['showSuccess', 'showFailure'])
        },
        {provide: NgbActiveModal, useExisting: NgbActiveModalMock},
        NgbActiveModalMock,
        {
          provide: BackupTargetsClient,
          useValue: createSpyObj('BackupTargetsClient', ['addBackupTarget'])
        }
      ]
    })
      .compileComponents();

    notificationService = TestBed.inject(NotificationService) as SpyObj<NotificationService>;
    modal = TestBed.inject(NgbActiveModalMock);
    backupTargetsClient = TestBed.inject(BackupTargetsClient) as SpyObj<BackupTargetsClient>;

    page = new Page();

    fixture = TestBed.createComponent(AddBackupTargetModalComponent);
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

  it('should not add backup target on submit given form validation fails', async () => {
    await fixture.whenStable();
    page.setInput(page.nameInput, '');

    page.submitForm();

    expect(notificationService.showFailure)
      .not.toHaveBeenCalledWith("Adding backup targets is not yet implemented.");
    expect(modal.timesClosed).toBe(0);
    expect(component.isLoading()).toBeFalse();
  });

  it('should add backup target on submit given form validation succeeds', async () => {
    await fixture.whenStable();
    const backupTarget: BackupTarget = TestBackupTarget.localFolder();
    const expectedRequest: AddBackupTargetRequest = {
      name: backupTarget.name,
      storageSolutionId: backupTarget.storageSolutionId,
      pathTemplate: backupTarget.pathTemplate,
    };
    addingBackupTargetSucceeds(backupTarget);

    page.setInput(page.nameInput, backupTarget.name);
    page.setInput(page.storageSolutionIdInput, backupTarget.storageSolutionId);
    page.setInput(page.pathTemplateInput, backupTarget.pathTemplate);

    page.submitForm();

    expect(backupTargetsClient.addBackupTarget)
      .toHaveBeenCalledWith(expectedRequest);
    expect(notificationService.showSuccess)
      .toHaveBeenCalledWith("Backup target added successfully");
    expect(modal.timesClosed).toBe(1);
    expect(component.isLoading()).toBeFalse();
  });

  function addingBackupTargetSucceeds(backupTarget: BackupTarget) {
    backupTargetsClient.addBackupTarget.and.returnValue(of({
      backupTarget: backupTarget,
    } as AddBackupTargetResponse) as any);
  }

  it('should gracefully handle errors when adding backup target fails', async () => {
    await fixture.whenStable();
    const backupTarget: BackupTarget = TestBackupTarget.localFolder();
    const error = new Error('Test error');
    addingBackupTargetThrows(error);

    page.setInput(page.nameInput, backupTarget.name);
    page.setInput(page.storageSolutionIdInput, backupTarget.storageSolutionId);
    page.setInput(page.pathTemplateInput, backupTarget.pathTemplate);

    page.submitForm();

    expect(notificationService.showFailure).toHaveBeenCalledWith(
      "Something went wrong when adding a Backup Target.", error);
    expect(modal.timesClosed).toBe(0);
    expect(component.isLoading()).toBeFalse();
  });

  function addingBackupTargetThrows(error: Error) {
    backupTargetsClient.addBackupTarget.and.returnValue(throwError(() => error));
  }
});
