import {ComponentFixture, TestBed} from '@angular/core/testing';
import {ConfirmationModalComponent} from './confirmation-modal.component';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {FormsModule} from '@angular/forms';
import {By} from '@angular/platform-browser';
import SpyObj = jasmine.SpyObj;
import createSpyObj = jasmine.createSpyObj;

describe('ConfirmationModalComponent', () => {
  let component: ConfirmationModalComponent;
  let fixture: ComponentFixture<ConfirmationModalComponent>;
  let ngbActiveModalSpy: SpyObj<NgbActiveModal>;

  beforeEach(async () => {
    const modalMock = createSpyObj('NgbActiveModal', ['close', 'dismiss']);

    await TestBed.configureTestingModule({
      imports: [ConfirmationModalComponent, FormsModule],
      providers: [
        {provide: NgbActiveModal, useValue: modalMock}
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ConfirmationModalComponent);
    component = fixture.componentInstance;
    ngbActiveModalSpy = TestBed.inject(NgbActiveModal) as SpyObj<NgbActiveModal>;

    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should have the default message "Are you sure?"', () => {
    const messageElement = fixture.debugElement.query(By.css('.modal-body'));
    expect(messageElement).toBeTruthy(); // Ensure the element exists
    expect(messageElement.nativeElement.textContent).toContain('Are you sure?');
  });

  it('should display the input message', () => {
    component.message = 'Do you want to proceed?';
    fixture.detectChanges();

    const messageElement = fixture.debugElement.query(By.css('.modal-body'));
    expect(messageElement).toBeTruthy(); // Ensure the element exists
    expect(messageElement.nativeElement.textContent).toContain('Do you want to proceed?');
  });

  it('should call close on the modal when confirmed', () => {
    component.modal.close();
    expect(ngbActiveModalSpy.close).toHaveBeenCalled();
  });

  it('should call dismiss on the modal when cancelled', () => {
    component.modal.dismiss();
    expect(ngbActiveModalSpy.dismiss).toHaveBeenCalled();
  });
});
