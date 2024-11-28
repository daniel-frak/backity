import {Component, Input} from '@angular/core';
import {NgClass, NgIf} from "@angular/common";

@Component({
  selector: 'app-button',
  standalone: true,
  imports: [
    NgClass,
    NgIf
  ],
  templateUrl: './button.component.html',
  styleUrl: './button.component.scss'
})
export class ButtonComponent {

  @Input() isLoading = false;
  @Input() buttonStyle = "primary";
  @Input() buttonType = "button";
  @Input() buttonSize = "";
  @Input() buttonClass = "";
  @Input() disabled = false;
  @Input() actionAsync?: () => Promise<void>;
  @Input() action?: VoidFunction;
  @Input() testId?: string;

  constructor() {
  }

  getSizeClass() {
    if (this.buttonSize == 'small') {
      return "btn-sm";
    }
    return "";
  }

  async onClick() {
    if (this.isLoading) {
      return;
    }

    this.isLoading = true;

    try {
      if (this.actionAsync) {
        await this.actionAsync();
      } else if (this.action) {
        this.action();
      }
    } finally {
      this.isLoading = false;
    }
  }
}
