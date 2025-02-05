import {ComponentFixture, TestBed} from '@angular/core/testing';

import {TableComponent} from './table.component';
import {Component, Input, QueryList, ViewChild} from "@angular/core";
import {TableColumnDirective} from "@app/shared/components/table/column-directive/table-column.directive";
import {By} from "@angular/platform-browser";
import {TableContentGroup} from "@app/shared/components/table/table-content-group";

@Component({
  template: `
    <app-table id="standard-table" [testId]="'someTestId1'" [isLoading]="isLoading || false"
               [content]="standardContent" caption="Test table 1">
      <ng-template app-table-column="Test column 1-1" let-item>Col1-1: {{ item }}</ng-template>
      <ng-template app-table-column="Test column 1-2" hide-title-on-mobile let-item>Col1-2: {{ item }}</ng-template>
      <ng-template app-table-column="Test column 1-3" append-class="custom-class" let-item>Col1-3: {{ item }}
      </ng-template>
    </app-table>
    <app-table id="grouped-table" [testId]="'someTestId2'" [isLoading]="isLoading || false"
               [groupedContent]="groupedContent" caption="Test table 2">
      <ng-template app-table-column="Test column 2-1" let-item>Col2-1: {{ item }}</ng-template>
      <ng-template app-table-column="Test column 2-2" hide-title-on-mobile let-item>Col2-2: {{ item }}</ng-template>
      <ng-template app-table-column="Test column 2-3" append-class="custom-class" let-item>Col2-3: {{ item }}
      </ng-template>
    </app-table>
  `,
  imports: [
    TableComponent,
    TableColumnDirective
  ],
  standalone: true
})
class TableComponentWrapper {
  @ViewChild(TableComponent)
  tableComponent: TableComponent = new TableComponent();

  @Input()
  isLoading?: boolean;

  @Input()
  standardContent?: any[];

  @Input()
  groupedContent?: TableContentGroup[];
}

describe('TableComponent', () => {
  let component: TableComponent;
  let fixture: ComponentFixture<TableComponentWrapper>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TableComponent],
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TableComponentWrapper);
    component = fixture.componentInstance.tableComponent;
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should have test id', () => {
    fixture.detectChanges();
    const standardTableElement = fixture.debugElement.query(By.css('[data-testid="someTestId1"]'));
    const groupedTableElement = fixture.debugElement.query(By.css('[data-testid="someTestId2"]'));
    expect(standardTableElement).toBeTruthy();
    expect(groupedTableElement).toBeTruthy();
  });

  it('should show content', () => {
    fixture.componentInstance.standardContent = ["testContent"];
    fixture.componentInstance.groupedContent = [{
      caption: 'Test grouped element',
      items: ["testContent"]
    }];
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelector('#standard-table td').textContent).toContain("Col1-1: testContent");
    expect(fixture.nativeElement.querySelector('#grouped-table td').textContent).toContain("Col2-1: testContent");
    expect(fixture.nativeElement.querySelector('#grouped-table th.group-caption').textContent)
      .toContain("Test grouped element");
  });

  it('should show title', () => {
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain("Test table 1");
    expect(fixture.nativeElement.textContent).toContain("Test table 2");
  });

  it('should not show if loading', () => {
    fixture.componentInstance.standardContent = ["testContent"];
    fixture.componentInstance.groupedContent = [{
      caption: 'Test grouped element',
      items: ["testContent"]
    }];
    fixture.componentInstance.isLoading = true;
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).not.toContain("Test table 1");
    expect(fixture.nativeElement.textContent).not.toContain("Test table 2");
  });

  it('should add class to row if hide-title-on-mobile is used', () => {
    fixture.componentInstance.standardContent = ["testContent"];
    fixture.componentInstance.groupedContent = [{
      caption: 'Test grouped element',
      items: ["testContent"]
    }];
    fixture.detectChanges();

    const columns = fixture.debugElement.queryAll(By.css('td'));
    const secondColumn = columns[1];

    expect(secondColumn.classes['hide-title']).toBeTruthy();
  });

  it('should add custom class to row', () => {
    fixture.componentInstance.standardContent = ["testContent"];
    fixture.componentInstance.groupedContent = [{
      caption: 'Test grouped element',
      items: ["testContent"]
    }];
    fixture.detectChanges();

    const columns = fixture.debugElement.queryAll(By.css('td'));
    const thirdColumn = columns[2];

    expect(thirdColumn.classes['custom-class']).toBeTruthy();
  });

  it('should show title with empty templateRefs', () => {
    component.templateRefs = new QueryList<TableColumnDirective>();
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain("Test table 1");
    expect(fixture.nativeElement.textContent).toContain("Test table 2");
  });
});
