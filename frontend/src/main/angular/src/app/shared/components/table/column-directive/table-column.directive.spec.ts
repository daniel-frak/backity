import {TableColumnDirective} from './table-column.directive';
import {TestBed} from "@angular/core/testing";
import {Component, viewChild} from "@angular/core";

@Component({
  standalone: true,
  imports: [TableColumnDirective],
  template: `
    <ng-template [app-table-column]="'Title'">Template Content</ng-template>`
})
class TestHostComponent {
  columnDirective = viewChild(TableColumnDirective);
}

describe('TableColumnDirective', () => {
  it('should create an instance', async () => {
    await TestBed.configureTestingModule({
      imports: [TestHostComponent]
    }).compileComponents();

    const fixture = TestBed.createComponent(TestHostComponent);
    fixture.detectChanges();
    expect(fixture.componentInstance.columnDirective()).toBeTruthy();
  });
});
