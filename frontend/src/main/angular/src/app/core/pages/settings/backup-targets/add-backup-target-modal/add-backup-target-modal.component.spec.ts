import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AddBackupTargetModalComponent} from './add-backup-target-modal.component';
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {DebugElement} from "@angular/core";
import {By} from "@angular/platform-browser";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {NgbActiveModalMock} from "@app/shared/testing/modals/ngb-active-modal-mock";
import SpyObj = jasmine.SpyObj;
import createSpyObj = jasmine.createSpyObj;

describe('AddBackupTargetModalComponent', () => {
  let component: AddBackupTargetModalComponent;
  let fixture: ComponentFixture<AddBackupTargetModalComponent>;

  let notificationService: SpyObj<NotificationService>;
  let modal: NgbActiveModalMock;

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
        NgbActiveModalMock
      ]
    })
      .compileComponents();

    notificationService = TestBed.inject(NotificationService) as SpyObj<NotificationService>;
    modal = TestBed.inject(NgbActiveModalMock);

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
  });

  it('should fail to add backup target on submit given form validation succeeds', async () => {
    await fixture.whenStable();
    page.setInput(page.nameInput, 'Backup target name');
    page.setInput(page.storageSolutionIdInput, 'someStorageSolutionId');
    page.setInput(page.pathTemplateInput, 'somePathTemplate');

    page.submitForm();

    expect(notificationService.showFailure)
      .toHaveBeenCalledWith("Adding backup targets is not yet implemented.");
    expect(modal.timesClosed).toBe(1);
  });
});
