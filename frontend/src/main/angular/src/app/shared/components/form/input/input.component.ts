import {Component, Input, Optional, Self} from '@angular/core';
import {NgClass, NgIf} from "@angular/common";
import {ControlValueAccessor, NgControl, ReactiveFormsModule} from "@angular/forms";

@Component({
    selector: 'app-input',
    imports: [
        NgIf,
        NgClass,
        ReactiveFormsModule
    ],
    templateUrl: './input.component.html',
    styleUrls: ['./input.component.scss']
})
export class InputComponent implements ControlValueAccessor {

  @Input() formControlName?: string;
  @Input() id?: string = undefined;
  @Input() type: 'text' | 'email' | 'password' = 'text';
  @Input() disabled: boolean = false;
  @Input() placeholder?: string;
  @Input() testId?: string;

  value: any;

  constructor(@Optional() @Self() public ngControl: NgControl) {
    if (this.ngControl != null) {
      this.ngControl.valueAccessor = this;
    }
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
}
