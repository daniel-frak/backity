import {TestBed} from '@angular/core/testing';

import {FormValidatorService} from './form-validator.service';
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import SpyObj = jasmine.SpyObj;

describe('FormValidatorService', () => {
  let service: FormValidatorService;
  let notificationService: SpyObj<NotificationService>;


  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {provide: NotificationService, useValue: jasmine.createSpyObj('NotificationService', ['showFailure'])}
      ]
    });
    service = TestBed.inject(FormValidatorService);
    notificationService = TestBed.inject(NotificationService) as SpyObj<NotificationService>;
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

      expect(result).toBeFalse();
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

      expect(result).toBeTrue();
    });

    it('should mark form as touched given form is invalid', () => {
      const form = new FormGroup({
        someInput: new FormControl('', Validators.required),
      });

      service.formIsInvalid(form);

      expect(form.controls['someInput'].touched).toBeTrue();
    });

    it('should show notification given form is invalid', () => {
      const form = new FormGroup({
        someInput: new FormControl('', Validators.required),
      });

      service.formIsInvalid(form);

      expect(notificationService.showFailure).toHaveBeenCalledWith(
        'Please check the form for errors and try again.',
        {
          someInput: {required: true},
        }
      );
    });
  })
});
