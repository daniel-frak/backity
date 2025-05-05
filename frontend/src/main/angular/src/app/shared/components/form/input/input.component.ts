import {Component, Input, OnInit} from '@angular/core';
import {NgClass, NgIf} from "@angular/common";
import {
  AbstractControl,
  ControlContainer,
  ControlValueAccessor,
  FormGroup,
  NG_VALUE_ACCESSOR,
  ReactiveFormsModule
} from "@angular/forms";

@Component({
  selector: 'app-input',
  standalone: true,
  imports: [
    NgIf,
    NgClass,
    ReactiveFormsModule
  ],
  templateUrl: './input.component.html',
  styleUrls: ['./input.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      multi: true,
      useExisting: InputComponent
    }
  ]
})
export class InputComponent implements OnInit, ControlValueAccessor {
  formGroup?: FormGroup = undefined;
  @Input() formControlName?: string;
  @Input() id?: string = undefined;
  @Input() type: 'text' | 'email' | 'password' = 'text';
  @Input() disabled: boolean = false;
  @Input() placeholder?: string;
  @Input() testId?: string;

  value: any;

  constructor(private readonly controlContainer: ControlContainer) {
  }

  ngOnInit() {
    this.formGroup = this.controlContainer.control as FormGroup;
  }

  onChange: (value: any) => void = () => {
  };

  onTouched: () => void = () => {
  };

  onInput(event: Event) {
    const input = event.target as HTMLInputElement;
    this.value = input.value;
    this.onChange(this.value);
  }

  writeValue(value: any): void {
    this.value = value;
  }

  registerOnChange(fn: (value: any) => void): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: () => void): void {
    this.onTouched = fn;
  }

  setDisabledState?(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }

  get formControl(): AbstractControl<any, any> {
    if (!this.formControlName) {
      throw new Error('The form control name is not set.');
    }
    const control = this.formGroup?.get(this.formControlName);
    if (!control) {
      throw new Error(`The control "${this.formControlName}" does not exist in the form.`);
    }
    return control;
  }
}
