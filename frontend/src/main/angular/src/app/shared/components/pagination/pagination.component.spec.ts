import {ComponentFixture, TestBed} from '@angular/core/testing';
import {PaginationComponent} from './pagination.component';
import {NgbPagination} from '@ng-bootstrap/ng-bootstrap';
import {FormsModule} from '@angular/forms';
import {By} from '@angular/platform-browser';
import {DebugElement} from '@angular/core';

describe('PaginationComponent', () => {
  let component: PaginationComponent;
  let fixture: ComponentFixture<PaginationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PaginationComponent, FormsModule, NgbPagination],
    }).compileComponents();

    fixture = TestBed.createComponent(PaginationComponent);
    component = fixture.componentInstance;

    component.currentPage = {
      totalElements: 100,
      content: Array(10),
      pageNumber: 1,
    };
    component.pageSize = 10;
    component.pageNumber = 1;
    component.availablePageSizes = [5, 10, 15];

    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should show correct numbers', () => {
    const expectedText = 'Showing 1-10 of 100';
    const paginationText = fixture.debugElement.query(By.css('.pagination-options'))
      .nativeElement.textContent.trim();

    expect(paginationText).toContain(expectedText);
  });

  it('should return 1 as the first element number on the first page', () => {
    component.pageNumber = 1;
    component.pageSize = 10;

    expect(component.getFirstElementNumber()).toBe(1);
  });

  it('should return the correct first element number for a middle page', () => {
    component.pageNumber = 3;
    component.pageSize = 10;

    expect(component.getFirstElementNumber()).toBe(21);
  });

  it('should return the correct last element number for a full page', () => {
    component.pageNumber = 1;
    component.pageSize = 10;
    component.currentPage = {
      totalElements: 100,
      pageNumber: 1,
      content: Array(10),
    };

    expect(component.getLastElementNumber()).toBe(10);
  });

  it('should return the correct last element number for a partially filled page', () => {
    component.pageNumber = 10;
    component.pageSize = 10;
    component.currentPage = {
      totalElements: 95,
      pageNumber: 10,
      content: Array(5),
    };

    expect(component.getLastElementNumber()).toBe(95);
  });

  it('should return the first element number as the last element number for an empty page', () => {
    component.pageNumber = 2;
    component.pageSize = 10;
    component.currentPage = {
      totalElements: 20,
      pageNumber: 2,
      content: [],
    };

    expect(component.getLastElementNumber()).toBe(11);
  });

  it('should return the first element number as the last element number if the page is undefined', () => {
    component.pageNumber = 2;
    component.pageSize = 10;
    component.currentPage = undefined;

    expect(component.getLastElementNumber()).toBe(11);
  });

  it('should return the correct total elements count when available', () => {
    component.pageNumber = 2;
    component.pageSize = 10;
    component.currentPage = {
      totalElements: 150,
      pageNumber: 1,
      content: Array(10),
    };
    expect(component.getTotalElements()).toBe(150);
  });

  it('should return 0 as the total elements count if totalElements is undefined', () => {
    component.currentPage = {
      totalElements: undefined,
      pageNumber: 1,
      content: Array(10),
    };

    expect(component.getTotalElements()).toBe(0);
  });

  it('should emit page number change and onPageChange when emitPageChange is called' +
    ' with page number different than current', () => {
    spyOn(component.pageNumberChange, 'emit');
    spyOn(component.onPageChange, 'emit');
    const newPageNumber = component.pageNumber + 1;

    component.emitPageChange(newPageNumber);

    expect(component.pageNumberChange.emit).toHaveBeenCalledWith(newPageNumber);
    expect(component.onPageChange.emit).toHaveBeenCalled();
  });

  it('should do nothing when emitPageChange is called with page number same as current', () => {
    spyOn(component.pageNumberChange, 'emit');
    spyOn(component.onPageChange, 'emit');
    const newPageNumber = component.pageNumber;

    component.emitPageChange(newPageNumber);

    expect(component.pageNumberChange.emit).not.toHaveBeenCalled();
    expect(component.onPageChange.emit).not.toHaveBeenCalled();
  });

  it('should call onPageSizeChange and onPageChange when page size is changed', () => {
    spyOn(component.pageSizeChange, 'emit');
    spyOn(component.onPageChange, 'emit');
    const newPageSize = 20;

    component.onPageSizeChange(newPageSize);

    expect(component.pageSizeChange.emit).toHaveBeenCalledWith(newPageSize);
    expect(component.onPageChange.emit).toHaveBeenCalled();
  });

  it('should render pagination options', () => {
    const options = fixture.debugElement.queryAll(By.css('option'));
    expect(options.length).toBe(3);
  });

  it('should disable NgbPagination when disabled input is true', () => {
    component.disabled = true;
    fixture.detectChanges();

    const pagination: DebugElement = fixture.debugElement.query(By.directive(NgbPagination));
    expect(pagination.componentInstance.disabled).toBeTrue();
  });

  it('should call emitPageChange when page is changed using NgbPagination', () => {
    spyOn(component, 'emitPageChange');
    const pagination: DebugElement = fixture.debugElement.query(By.directive(NgbPagination));

    pagination.triggerEventHandler('pageChange', 3);
    fixture.detectChanges();

    expect(component.emitPageChange).toHaveBeenCalledWith(3);
  });
});
