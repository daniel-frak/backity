import {Component, Input} from '@angular/core';
import {NgClass, NgIf} from "@angular/common";
import {ButtonStyle} from "@app/shared/components/button/button-style";
import {ButtonSize} from "@app/shared/components/button/button-size";

@Component({
    selector: 'app-button',
    imports: [
        NgClass,
        NgIf
    ],
    templateUrl: './button.component.html',
    styleUrl: './button.component.scss'
})
export class ButtonComponent {

  @Input() isLoading = false;
  @Input() buttonStyle: ButtonStyle = "primary";
  @Input() outline = false;
  @Input() buttonType = "button";
  @Input() buttonSize: ButtonSize = undefined;
  @Input() buttonClass = "";
  @Input() disabled = false;
  @Input() actionAsync?: () => Promise<void>;
  @Input() action?: VoidFunction;
  @Input() title?: string;
  @Input() testId?: string;
  @Input() ngbAutofocus?: boolean;

  private static readonly sizeClassMap = new Map<ButtonSize, string>([
    ['small',  'btn-sm'],
    ['medium', 'btn-md'],
    ['large',  'btn-lg'],
  ]);

  getButtonStyle() {
    if (this.outline) {
      return "btn-outline-" + this.buttonStyle;
    }
    return "btn-" + this.buttonStyle;
  }

  getSizeClass() {
    return ButtonComponent.sizeClassMap.get(this.buttonSize) ?? '';
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
