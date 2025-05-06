import { ComponentFixture, TestBed } from '@angular/core/testing';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import { InputComponent } from './input.component';
import { By } from '@angular/platform-browser';
import {Component, DebugElement} from '@angular/core';

@Component({
  template: `
    <form [formGroup]="form">
      <app-input formControlName="testInput"></app-input>
    </form>
  `
})
export class TestHostComponent {
  form: FormGroup;

  constructor(private fb: FormBuilder) {
    this.form = this.fb.group({
      testInput: ['', Validators.required]
    });
  }
}

describe('InputComponent', () => {
  let fixture: ComponentFixture<TestHostComponent>;
  let hostComponent: TestHostComponent;
  let inputComponent: InputComponent;
  let inputElement: DebugElement;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TestHostComponent],
      imports: [ReactiveFormsModule, InputComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(TestHostComponent);
    hostComponent = fixture.componentInstance;
    fixture.detectChanges();

    inputComponent = fixture.debugElement.query(By.directive(InputComponent)).componentInstance;
    inputElement = fixture.debugElement.query(By.css('input'));
  });

  it('should create', () => {
    expect(inputComponent).toBeTruthy();
  });

  it('should call onTouched on blur event', () => {
    const onTouchedSpy = spyOn(inputComponent, 'onTouched');
    inputElement.nativeElement.dispatchEvent(new Event('blur'));
    fixture.detectChanges();
    expect(onTouchedSpy).toHaveBeenCalled();
  });

  it('should display error message when form control is invalid and touched', () => {
    const control = hostComponent.form.get('testInput');
    control?.markAsTouched();
    control?.markAsDirty();
    control?.updateValueAndValidity();

    fixture.detectChanges();

    const errorMsg = fixture.debugElement.query(By.css('.invalid-feedback'));
    expect(errorMsg).toBeTruthy();
    expect(errorMsg.nativeElement.textContent).toContain('Must not be empty');
  });

  it('should update value and call onChange when onInput is triggered', () => {
    const onChangeSpy = spyOn(inputComponent, 'onChange');
    const inputValue = 'Test value';

    inputElement.nativeElement.value = inputValue;
    inputElement.nativeElement.dispatchEvent(new Event('input'));

    fixture.detectChanges();

    expect(inputComponent.value).toBe(inputValue);
    expect(onChangeSpy).toHaveBeenCalledWith(inputValue);
  });
});
