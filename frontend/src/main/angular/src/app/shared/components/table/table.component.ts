import { Component, ContentChildren, Input, OnInit, QueryList } from '@angular/core';
import { TableColumnDirective } from "@app/shared/components/table/column-directive/table-column.directive";
import { LoadedContentComponent } from '../loaded-content/loaded-content.component';
import { NgClass, NgTemplateOutlet } from '@angular/common';
import {TableContentGroup} from "@app/shared/components/table/table-content-group";

@Component({
    selector: 'app-table',
    templateUrl: './table.component.html',
    styleUrls: ['./table.component.scss'],
    imports: [LoadedContentComponent, NgClass, NgTemplateOutlet]
})
export class TableComponent implements OnInit {

  @ContentChildren(TableColumnDirective, { descendants: false })
  templateRefs!: QueryList<TableColumnDirective>;

  @Input()
  testId?: string;

  @Input()
  content?: any[];

  @Input()
  groupedContent?: TableContentGroup[];

  @Input()
  caption: string | undefined;

  @Input()
  isLoading: boolean = false;

  constructor() { }

  ngOnInit(): void {
    // Nothing to initialize
  }

  getColumnTitles(): string[] {
    const columns: string[] = [];
    this.templateRefs.forEach(t => columns.push(t.columnTitle as string));

    return columns;
  }

  getTdClass(column: TableColumnDirective): string {
    const classes = [];

    if (column.hideTitleOnMobile) {
      classes.push('hide-title')
    }

    if (column.class) {
      classes.push(column.class);
    }

    return classes.join(" ");
  }
}
