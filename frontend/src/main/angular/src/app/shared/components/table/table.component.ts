import {Component, ContentChildren, Input, OnInit, QueryList} from '@angular/core';
import {TableContent} from "@app/shared/components/table/table-content";
import {TableColumnDirective} from "@app/shared/components/table/column-directive/table-column.directive";

@Component({
  selector: 'app-table',
  templateUrl: './table.component.html',
  styleUrls: ['./table.component.scss']
})
export class TableComponent implements OnInit {

  @ContentChildren(TableColumnDirective, {descendants: false})
  templateRefs!: QueryList<TableColumnDirective>;

  @Input()
  content?: TableContent;

  @Input()
  caption?: string;

  @Input()
  isLoading: boolean = false;

  constructor() {
  }

  ngOnInit(): void {
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
