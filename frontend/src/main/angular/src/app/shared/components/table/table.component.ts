import {AfterContentInit, Component, computed, ContentChildren, input, QueryList, signal} from '@angular/core';
import {TableColumnDirective} from "@app/shared/components/table/column-directive/table-column.directive";
import {LoadedContentComponent} from '../loaded-content/loaded-content.component';
import {NgClass, NgTemplateOutlet} from '@angular/common';
import {TableContentGroup} from "@app/shared/components/table/table-content-group";

/**
 * @example
 * // Component TypeScript
 * items = [
 *   { id: 1, name: 'Item 1', description: 'Description 1' },
 *   { id: 2, name: 'Item 2', description: 'Description 2' }
 * ];
 *
 * edit(item: any) {
 *   console.log('Editing', item);
 * }
 *
 * <!-- Component Template -->
 * <app-table [content]="items" caption="My Items">
 *   <ng-template app-table-column="Name" let-item>
 *     {{ item.name }}
 *   </ng-template>
 *   <ng-template app-table-column="Actions" hideTitleOnMobile let-item>
 *     <button (click)="edit(item)">Edit</button>
 *   </ng-template>
 * </app-table>
 *
 * @example
 * // Component TypeScript
 * groups: TableContentGroup[] = [
 *   {
 *     caption: 'Group A',
 *     items: [
 *       { id: 1, description: 'A1' },
 *       { id: 2, description: 'A2' }
 *     ]
 *   },
 *   {
 *     caption: 'Group B',
 *     items: [
 *       { id: 3, description: 'B1' }
 *     ]
 *   }
 * ];
 *
 * <!-- Component Template -->
 * <app-table [groupedContent]="groups">
 *   <ng-template app-table-column="ID" let-item>
 *     {{ item.id }}
 *   </ng-template>
 *   <ng-template app-table-column="Description" let-item>
 *     {{ item.description }}
 *   </ng-template>
 * </app-table>
 */
@Component({
  selector: 'app-table',
  templateUrl: './table.component.html',
  styleUrl: './table.component.scss',
  imports: [LoadedContentComponent, NgClass, NgTemplateOutlet]
})
export class TableComponent implements AfterContentInit {

  @ContentChildren(TableColumnDirective, {descendants: false})
  templateRefs!: QueryList<TableColumnDirective>;

  readonly testId = input<string>();

  readonly content = input<any[]>();

  readonly groupedContent = input<TableContentGroup[]>();

  readonly caption = input<string>();

  readonly isLoading = input<boolean>(false);

  private readonly columns = signal<TableColumnDirective[]>([]);

  readonly columnTitles = computed(() =>
    this.columns()
      .map(col => col.columnTitle())
      .filter((t): t is string => !!t)
  );

  constructor() {
  }

  ngAfterContentInit(): void {
    this.columns.set(this.templateRefs.toArray());

    this.templateRefs.changes.subscribe(() => {
      this.columns.set(this.templateRefs.toArray());
    });
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
