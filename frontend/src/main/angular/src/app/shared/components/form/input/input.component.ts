import {Component, input, model, Optional, Self} from '@angular/core';
import {NgClass} from "@angular/common";
import {ControlValueAccessor, NgControl, ReactiveFormsModule} from "@angular/forms";

@Component({
  selector: 'app-input',
  imports: [
    NgClass,
    ReactiveFormsModule
  ],
  templateUrl: './input.component.html',
  styleUrl: './input.component.scss'
})
export class InputComponent implements ControlValueAccessor {

  readonly id = input<string>(this.generateId());
  readonly type = input<'text' | 'email' | 'password'>('text');
  readonly disabled = model<boolean>(false);
  readonly placeholder = input<string>('');
  readonly testId = input<string>();
  readonly floating = input<boolean>(true);
  readonly iconClass = input<string>();

  value: any = '';

  constructor(@Optional() @Self() public ngControl: NgControl | null) {
    if (this.ngControl != null) {
      this.ngControl.valueAccessor = this;
    }
  }

  private generateId(): string {
    return `input-${Math.random().toString(36).substring(2, 9)}`; // NOSONAR
  }

  onChange: (value: any) => void = () => {
  };

  onTouched: () => void = () => {
  };

  onInput(event: Event) {
    const target = event.target as HTMLInputElement;
    this.value = target.value;
    this.onChange(this.value);
  }

  writeValue(value: any): void {
    this.value = value ?? '';
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
