import {Directive, TemplateRef, input} from '@angular/core';

@Directive({
  selector: '[app-table-column]',
  standalone: true
})
export class TableColumnDirective {

  constructor(public readonly template: TemplateRef<any>) {
  }

  readonly columnTitle = input<string>(undefined, { alias: "app-table-column" });

  get hideTitleOnMobile(): boolean {
    const _hideTitleOnMobile = this._hideTitleOnMobile();
    return !!_hideTitleOnMobile || _hideTitleOnMobile === '';
  }

  readonly _hideTitleOnMobile = input<string | boolean>(undefined, { alias: "hide-title-on-mobile" });

  readonly class = input<string>(undefined, { alias: "append-class" });
}
