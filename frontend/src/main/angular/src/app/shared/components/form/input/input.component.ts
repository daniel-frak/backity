import {Component, Optional, Self, input, signal} from '@angular/core';
import {NgClass} from "@angular/common";
import {ControlValueAccessor, NgControl, ReactiveFormsModule} from "@angular/forms";

@Component({
  selector: 'app-input',
  imports: [
    NgClass,
    ReactiveFormsModule
  ],
  templateUrl: './input.component.html',
  styleUrls: ['./input.component.scss']
})
export class InputComponent implements ControlValueAccessor {

  readonly id = input<string>();
  readonly type = input<'text' | 'email' | 'password'>('text');
  readonly disabled = signal<boolean>(false);
  readonly placeholder = input<string>();
  readonly testId = input<string>();
  readonly floating = input<boolean | undefined>(true);
  readonly iconClass = input<string>();

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
    this.disabled.set(isDisabled);
  }
}
