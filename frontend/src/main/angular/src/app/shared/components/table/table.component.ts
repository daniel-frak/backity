import {Component, ContentChildren, Input, OnInit, QueryList, TemplateRef} from '@angular/core';
import {TableColumn} from "@app/shared/components/table/table-column";
import {TableContent} from "@app/shared/components/table/table-content";

@Component({
  selector: 'app-table',
  templateUrl: './table.component.html',
  styleUrls: ['./table.component.scss']
})
export class TableComponent implements OnInit {

  @ContentChildren(TemplateRef)
  templateRefs?: QueryList<TemplateRef<any>>;

  @Input()
  columns?: TableColumn[];

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

  getTdClass(index: number): string {
    let classes = [];

    const column = (this.columns as TableColumn[])[index];
    if (column.hideTitleOnMobile) {
      classes.push('hide-title')
    }

    if (column.class) {
      classes.push(column.class);
    }

    return classes.join(" ");
  }
}
