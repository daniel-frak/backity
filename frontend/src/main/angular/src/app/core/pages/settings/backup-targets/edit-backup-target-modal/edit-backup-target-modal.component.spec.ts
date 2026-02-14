import {ComponentFixture, TestBed} from '@angular/core/testing';

import {EditBackupTargetModalComponent} from './edit-backup-target-modal.component';
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {By} from "@angular/platform-browser";
import {DebugElement} from "@angular/core";
import SpyObj = jasmine.SpyObj;
import createSpyObj = jasmine.createSpyObj;

describe('EditBackupTargetModalComponent', () => {
  let component: EditBackupTargetModalComponent;
  let fixture: ComponentFixture<EditBackupTargetModalComponent>;

  let notificationService: SpyObj<NotificationService>;
  let modal: SpyObj<NgbActiveModal>;

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

    get pathTemplateInput(): HTMLInputElement {
      return this.getElementByTestId('path-template-input') as HTMLInputElement;
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
    const modalMock = createSpyObj('NgbActiveModal', ['close', 'dismiss']);

    await TestBed.configureTestingModule({
      imports: [EditBackupTargetModalComponent],
      providers: [
        {
          provide: NotificationService,
          useValue: createSpyObj('NotificationService', ['showSuccess', 'showFailure'])
        },
        {provide: NgbActiveModal, useValue: modalMock},
      ]
    })
    .compileComponents();

    notificationService = TestBed.inject(NotificationService) as SpyObj<NotificationService>;
    modal = TestBed.inject(NgbActiveModal) as SpyObj<NgbActiveModal>;

    page = new Page();

    fixture = TestBed.createComponent(EditBackupTargetModalComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should close modal on close button click', () => {
    page.closeButton.click();

    expect(modal.dismiss).toHaveBeenCalled();
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
    expect(modal.close).not.toHaveBeenCalled();
  });

  it('should fail to edit backup target on submit given form validation succeeds', async () => {
    await fixture.whenStable();
    page.setInput(page.nameInput, 'Backup target name');
    page.setInput(page.pathTemplateInput, 'somePathTemplate');

    page.submitForm();

    expect(notificationService.showFailure)
      .toHaveBeenCalledWith("Editing backup targets is not yet implemented.");
    expect(modal.close).toHaveBeenCalled();
  });
});
