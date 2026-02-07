import {Component, EventEmitter, input, Optional, Output, Self} from '@angular/core';
import {ControlValueAccessor, FormsModule, NgControl, ReactiveFormsModule} from "@angular/forms";

@Component({
  selector: 'app-select',
  imports: [
    ReactiveFormsModule,
    FormsModule
  ],
  templateUrl: './select.component.html',
  styleUrl: './select.component.scss'
})
export class SelectComponent<T> implements ControlValueAccessor {

  readonly elements = input<T[]>([]);
  readonly label = input('');
  readonly floating = input(true);
  readonly noValueText = input('None');

  value!: T;
  @Output() valueChange = new EventEmitter<T>();

  constructor(@Optional() @Self() public ngControl: NgControl) {
    if (this.ngControl != null) {
      this.ngControl.valueAccessor = this;
    }
  }

  private onChange = (_: T) => {
  };
  private onTouched = () => {
  };

  writeValue(value: T): void {
    this.value = value;
  }

  registerOnChange(fn: (value: T) => void): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: () => void): void {
    this.onTouched = fn;
  }

  handleChange(value: T): void {
    this.value = value;
    this.onTouched();
    this.onChange(value);
    this.valueChange.emit(value);
  }
}
