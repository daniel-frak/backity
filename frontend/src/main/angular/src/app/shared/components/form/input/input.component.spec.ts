import {ComponentFixture, TestBed} from '@angular/core/testing';
import {
  ControlContainer,
  FormBuilder,
  FormGroup,
  FormGroupDirective,
  NG_VALUE_ACCESSOR,
  Validators
} from '@angular/forms';
import {InputComponent} from './input.component';
import {By} from '@angular/platform-browser';
import {DebugElement} from '@angular/core';

const TEST_FORM_CONTROL_NAME = 'testInput';

describe('InputComponent', () => {
  let component: InputComponent;
  let fixture: ComponentFixture<InputComponent>;
  let form: FormGroup = new FormBuilder().group({
    testInput: ['', Validators.required]
  });
  let inputElement: DebugElement;

  beforeEach(async () => {
    const formGroupDirective = new FormGroupDirective([], []);
    formGroupDirective.form = form;

    await TestBed.configureTestingModule({
      imports: [InputComponent],
      providers: [
        {
          provide: ControlContainer,
          useValue: formGroupDirective
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(InputComponent);
    component = fixture.componentInstance;
    component.formControlName = TEST_FORM_CONTROL_NAME;
    component.id = TEST_FORM_CONTROL_NAME;
    component.type = 'text';
    component.placeholder = 'Enter text';
    component.testId = 'test-input';
    fixture.debugElement.injector.get(NG_VALUE_ACCESSOR);

    fixture.detectChanges();

    inputElement = fixture.debugElement.query(By.css('input'));
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call onTouched on blur event', () => {
    const onTouchedSpy = spyOn(component, 'onTouched');
    inputElement.nativeElement.dispatchEvent(new Event('blur'));
    fixture.detectChanges();
    expect(onTouchedSpy).toHaveBeenCalled();
  });

  it('should display error message when form control is invalid and touched', () => {
    form.get(TEST_FORM_CONTROL_NAME)?.markAsTouched();
    form.get(TEST_FORM_CONTROL_NAME)?.markAsDirty();
    form.get(TEST_FORM_CONTROL_NAME)?.updateValueAndValidity();
    fixture.detectChanges();
    const errorMsg = fixture.debugElement.query(By.css('.invalid-feedback'));
    expect(errorMsg).toBeTruthy();
    expect(errorMsg.nativeElement.textContent).toContain('Must not be empty');
  });

  it('should update value and call onChange when onInput is triggered', () => {
    const onChangeSpy = spyOn(component, 'onChange');
    const inputValue = 'Test value';
    inputElement.nativeElement.value = inputValue;
    inputElement.nativeElement.dispatchEvent(new Event('input'));

    fixture.detectChanges();

    expect(component.value).toBe(inputValue);
    expect(onChangeSpy).toHaveBeenCalledWith(inputValue);
  });
});
