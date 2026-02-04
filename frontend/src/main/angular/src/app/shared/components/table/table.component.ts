import {Component, ContentChildren, input, QueryList} from '@angular/core';
import {TableColumnDirective} from "@app/shared/components/table/column-directive/table-column.directive";
import {LoadedContentComponent} from '../loaded-content/loaded-content.component';
import {NgClass, NgTemplateOutlet} from '@angular/common';
import {TableContentGroup} from "@app/shared/components/table/table-content-group";

@Component({
  selector: 'app-table',
  templateUrl: './table.component.html',
  styleUrls: ['./table.component.scss'],
  imports: [LoadedContentComponent, NgClass, NgTemplateOutlet]
})
export class TableComponent {

  @ContentChildren(TableColumnDirective, {descendants: false})
  templateRefs!: QueryList<TableColumnDirective>;

  readonly testId = input<string>();

  readonly content = input<any[]>();

  readonly groupedContent = input<TableContentGroup[]>();

  readonly caption = input<string>();

  readonly isLoading = input<boolean>(false);

  constructor() {
  }

  getColumnTitles(): string[] {
    return this.templateRefs.map(t => t.columnTitle() as string);
  }

  getTdClass(column: TableColumnDirective): string {
    const classes = [];

    if (column.hideTitleOnMobile()) {
      classes.push('hide-title')
    }

    const columnClass = column.appendClass();
    if (columnClass) {
      classes.push(columnClass);
    }

    return classes.join(" ");
  }
}
