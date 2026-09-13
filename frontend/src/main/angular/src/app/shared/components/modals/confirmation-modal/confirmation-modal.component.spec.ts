import {ComponentFixture, TestBed} from '@angular/core/testing';
import {ConfirmationModalComponent} from './confirmation-modal.component';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {By} from '@angular/platform-browser';
import {Mocked} from "vitest";

describe('ConfirmationModalComponent', () => {
    let component: ConfirmationModalComponent;
    let fixture: ComponentFixture<ConfirmationModalComponent>;
    let ngbActiveModalSpy: Mocked<NgbActiveModal>;

    beforeEach(async () => {
        const modalMock = {
            close: vi.fn(),
            dismiss: vi.fn()
        };

        await TestBed.configureTestingModule({
            imports: [ConfirmationModalComponent],
            providers: [
                { provide: NgbActiveModal, useValue: modalMock }
            ]
        }).compileComponents();

        fixture = TestBed.createComponent(ConfirmationModalComponent);
        component = fixture.componentInstance;
        ngbActiveModalSpy = TestBed.inject(NgbActiveModal) as any;
    });

    it('should create the component', () => {
        fixture.detectChanges();
        expect(component).toBeTruthy();
    });

    it('should have the default message "Are you sure?"', () => {
        fixture.detectChanges();

        const messageElement = fixture.debugElement.query(By.css('.modal-body'));
        expect(messageElement).toBeTruthy(); // Ensure the element exists
        expect(messageElement.nativeElement.textContent).toContain('Are you sure?');
    });

    it('should display the input message', () => {
        fixture.componentRef.setInput('message', 'Do you want to proceed?');
        fixture.detectChanges();

        const messageElement = fixture.debugElement.query(By.css('.modal-body'));
        expect(messageElement).toBeTruthy(); // Ensure the element exists
        expect(messageElement.nativeElement.textContent).toContain('Do you want to proceed?');
    });

    it('should call close on the modal when confirmed', () => {
        fixture.detectChanges();

        component.modal.close();
        expect(ngbActiveModalSpy.close).toHaveBeenCalled();
    });

    it('should call dismiss on the modal when canceled', () => {
        fixture.detectChanges();

        component.modal.dismiss();
        expect(ngbActiveModalSpy.dismiss).toHaveBeenCalled();
    });
});
