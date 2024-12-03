import {ComponentFixture, TestBed} from '@angular/core/testing';

import {TableComponent} from './table.component';
import {Component, Input, QueryList, ViewChild} from "@angular/core";
import {TableContent} from "@app/shared/components/table/table-content";
import {LoadedContentStubComponent} from "@app/shared/components/loaded-content/loaded-content.component.stub";
import {TableColumnDirective} from "@app/shared/components/table/column-directive/table-column.directive";
import {By} from "@angular/platform-browser";

@Component({
  template: `
    <app-table [testId]="'someTestId'" [isLoading]="isLoading || false" [content]="content" caption="Test table">
      <ng-template app-table-column="Test column 1" let-item>Col1: {{ item }}</ng-template>
      <ng-template app-table-column="Test column 2" hide-title-on-mobile let-item>Col2: {{ item }}</ng-template>
      <ng-template app-table-column="Test column 3" append-class="custom-class" let-item>Col3: {{ item }}</ng-template>
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
  content?: TableContent;
}

describe('TableComponent', () => {
  let component: TableComponent;
  let fixture: ComponentFixture<TableComponentWrapper>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TableComponent, TableComponentWrapper, TableColumnDirective, LoadedContentStubComponent],
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
    const tableElement = fixture.debugElement.query(By.css('[data-testid="someTestId"]'));
    expect(tableElement).toBeTruthy();
  });

  it('should show content', () => {
    fixture.componentInstance.content = {content: ["testContent"]};
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelector('td').textContent).toContain("Col1: testContent");
  });

  it('should show title', () => {
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain("Test table");
  });

  it('should not show if loading', () => {
    fixture.componentInstance.content = {content: ["testContent"]};
    fixture.componentInstance.isLoading = true;
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).not.toContain("Test table");
  });

  it('should add class to row if hide-title-on-mobile is used', () => {
    fixture.componentInstance.content = {content: ["testContent"]};
    fixture.detectChanges();

    const columns = fixture.debugElement.queryAll(By.css('td'));
    const secondColumn = columns[1];

    expect(secondColumn.classes['hide-title']).toBeTruthy();
  });

  it('should add custom class to row', () => {
    fixture.componentInstance.content = {content: ["testContent"]};
    fixture.detectChanges();

    const columns = fixture.debugElement.queryAll(By.css('td'));
    const thirdColumn = columns[2];

    expect(thirdColumn.classes['custom-class']).toBeTruthy();
  });

  it('should show title with empty templateRefs', () => {
    component.templateRefs = new QueryList<TableColumnDirective>();
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain("Test table");
  });
});
