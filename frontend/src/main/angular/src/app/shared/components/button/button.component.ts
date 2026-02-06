import {Component, input, model} from '@angular/core';
import {NgClass} from "@angular/common";
import {ButtonStyle} from "@app/shared/components/button/button-style";
import {ButtonSize} from "@app/shared/components/button/button-size";

@Component({
    selector: 'app-button',
    imports: [
    NgClass
],
    templateUrl: './button.component.html',
    styleUrl: './button.component.scss'
})
export class ButtonComponent {

  readonly isLoading = model(false);
  readonly buttonStyle = input<ButtonStyle>("primary");
  readonly outline = input(false);
  readonly buttonType = input("button");
  readonly buttonSize = input<ButtonSize>();
  readonly buttonClass = input("");
  readonly disabled = input(false);
  readonly actionAsync = input<() => Promise<void>>();
  readonly action = input<VoidFunction>();
  readonly title = input<string>();
  readonly testId = input<string>();
  readonly ngbAutofocus = input<boolean>();

  private static readonly sizeClassMap = new Map<ButtonSize, string>([
    ['small',  'btn-sm'],
    ['medium', 'btn-md'],
    ['large',  'btn-lg'],
  ]);

  getButtonStyle() {
    if (this.outline()) {
      return "btn-outline-" + this.buttonStyle();
    }
    return "btn-" + this.buttonStyle();
  }

  getSizeClass() {
    return ButtonComponent.sizeClassMap.get(this.buttonSize()) ?? '';
  }

  async onClick() {
    if (this.isLoading()) {
      return;
    }

    this.isLoading.set(true);

    try {
      const actionAsync = this.actionAsync();
      const action = this.action();
      if (actionAsync) {
        await actionAsync();
      } else if (action) {
        action();
      }
    } finally {
      this.isLoading.set(false);
    }
  }
}
