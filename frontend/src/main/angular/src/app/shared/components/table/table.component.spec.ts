import {ComponentFixture, TestBed} from '@angular/core/testing';

import {TableComponent} from './table.component';
import {Component, Input, ViewChild} from "@angular/core";
import {TableContent} from "@app/shared/components/table/table-content";
import {LoadedContentStubComponent} from "@app/shared/components/loaded-content/loaded-content.component.stub";
import {TableColumnDirective} from "@app/shared/components/table/column-directive/table-column.directive";

@Component({
  template: `
    <app-table [isLoading]="isLoading" [content]="content" caption="Test table">
      <ng-template app-table-column="Test column" let-item>Item: {{item}}</ng-template>
    </app-table>
  `
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
      declarations: [TableComponentWrapper, TableComponent, LoadedContentStubComponent, TableColumnDirective],
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

  it('should show content', () => {
    fixture.componentInstance.content = {content: ["testContent"]};
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelector('td').textContent).toContain("Item: testContent");
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
});
