import {TestBed} from '@angular/core/testing';

import {FormValidatorService} from './form-validator.service';
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {Mocked} from "vitest";

describe('FormValidatorService', () => {
    let service: FormValidatorService;
    let notificationService: Mocked<NotificationService>;


    beforeEach(() => {
        notificationService = {
            showFailure: vi.fn().mockName("NotificationService.showFailure")
        } as unknown as Mocked<NotificationService>;
        TestBed.configureTestingModule({
            providers: [
                { provide: NotificationService, useValue: notificationService }
            ]
        });
        service = TestBed.inject(FormValidatorService);
        notificationService = TestBed.inject(NotificationService) as any;
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    describe('formIsInvalid()', () => {

        it('should return false given form is valid', () => {
            const form = new FormGroup({
                someInput: new FormControl('some value', Validators.required),
            });

            const result: boolean = service.formIsInvalid(form);

            expect(result).toBe(false);
        });

        it('should not show notification given form is valid', () => {
            const form = new FormGroup({
                someInput: new FormControl('some value', Validators.required),
            });

            service.formIsInvalid(form);

            expect(notificationService.showFailure).not.toHaveBeenCalled();
        });

        it('should return true given form is invalid', () => {
            const form = new FormGroup({
                someInput: new FormControl('', Validators.required),
            });

            const result: boolean = service.formIsInvalid(form);

            expect(result).toBe(true);
        });

        it('should mark form as touched given form is invalid', () => {
            const form = new FormGroup({
                someInput: new FormControl('', Validators.required),
            });

            service.formIsInvalid(form);

            expect(form.controls['someInput'].touched).toBe(true);
        });

        it('should show notification given form is invalid', () => {
            const form = new FormGroup({
                someInput: new FormControl('', Validators.required),
            });

            service.formIsInvalid(form);

            expect(notificationService.showFailure).toHaveBeenCalledWith('Please check the form for errors and try again.', {
                someInput: { required: true },
            });
        });
    });
});
