import {Directive, Input, TemplateRef} from '@angular/core';

@Directive({
  selector: '[app-table-column]',
  standalone: true
})
export class TableColumnDirective {

  constructor(public readonly template: TemplateRef<any>) {
  }

  @Input('app-table-column')
  columnTitle?: string;

  get hideTitleOnMobile(): boolean {
    return !!this._hideTitleOnMobile || this._hideTitleOnMobile === '';
  }

  @Input('hide-title-on-mobile')
  _hideTitleOnMobile?: string | boolean;

  @Input('append-class')
  class?: string;
}
