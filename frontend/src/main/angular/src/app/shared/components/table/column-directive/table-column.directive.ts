import {booleanAttribute, Directive, input, TemplateRef} from '@angular/core';

@Directive({
  selector: '[app-table-column]',
  standalone: true
})
export class TableColumnDirective {

  readonly columnTitle = input<string>(undefined, {alias: "app-table-column"});
  readonly hideTitleOnMobile = input<boolean, unknown>(false, {
    transform: booleanAttribute
  });
  readonly appendClass = input<string | undefined>(undefined);

  constructor(public readonly template: TemplateRef<any>) {
  }
}
