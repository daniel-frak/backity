import {TableColumnDirective} from './table-column.directive';
import {TemplateRef} from "@angular/core";

describe('TableColumnDirective', () => {
  it('should create an instance', () => {
    const directive = new TableColumnDirective({} as TemplateRef<any>);
    expect(directive).toBeTruthy();
  });
});
