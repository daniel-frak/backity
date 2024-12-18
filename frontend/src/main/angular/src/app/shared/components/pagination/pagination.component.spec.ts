import {ComponentFixture, TestBed} from '@angular/core/testing';
import {PaginationComponent} from './pagination.component';
import {NgbPagination} from '@ng-bootstrap/ng-bootstrap';
import {FormsModule} from '@angular/forms';
import {By} from '@angular/platform-browser';
import {DebugElement} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {of} from "rxjs";
import SpyObj = jasmine.SpyObj;
import createSpyObj = jasmine.createSpyObj;

describe('PaginationComponent', () => {
  let component: PaginationComponent;
  let fixture: ComponentFixture<PaginationComponent>;
  let router: SpyObj<Router>;
  let activatedRoute: ActivatedRoute;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PaginationComponent, FormsModule, NgbPagination],
      providers: [
        {provide: Router, useValue: createSpyObj(Router, ['navigate'])},
        {
          provide: ActivatedRoute,
          useValue: {
            queryParams: of({}),
          }
        },
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(PaginationComponent);
    component = fixture.componentInstance;

    router = TestBed.inject(Router) as SpyObj<Router>;
    activatedRoute = TestBed.inject(ActivatedRoute);

    component.currentPage = {
      totalElements: 100,
      content: Array(10),
      pageNumber: 1,
    };
    component.pageSize = 10;
    component.pageNumber = 1;
    component.availablePageSizes = [5, 10, 15];
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should show correct numbers', () => {
    fixture.detectChanges();
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

  it('should emit page number change and onPageChange when onPageNumberChange is called' +
    ' with page number different than current', () => {
    spyOn(component.pageNumberChange, 'emit');
    spyOn(component.onPageChange, 'emit');
    const newPageNumber = component.pageNumber + 1;

    component.onPageNumberChange(newPageNumber);

    expect(component.pageNumberChange.emit).toHaveBeenCalledWith(newPageNumber);
    expect(component.onPageChange.emit).toHaveBeenCalled();
  });

  it('should do nothing when onPageNumberChange is called with page number same as current', () => {
    spyOn(component.pageNumberChange, 'emit');
    spyOn(component.onPageChange, 'emit');
    const newPageNumber = component.pageNumber;

    component.onPageNumberChange(newPageNumber);

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
    fixture.detectChanges();
    const options = fixture.debugElement.queryAll(By.css('option'));
    expect(options.length).toBe(3);
  });

  it('should disable NgbPagination when disabled input is true', () => {
    component.disabled = true;
    fixture.detectChanges();

    const pagination: DebugElement = fixture.debugElement.query(By.directive(NgbPagination));
    expect(pagination.componentInstance.disabled).toBeTrue();
  });

  it('should call onPageNumberChange when page is changed using NgbPagination', () => {
    fixture.detectChanges();
    spyOn(component, 'onPageNumberChange');
    const pagination: DebugElement = fixture.debugElement.query(By.directive(NgbPagination));

    pagination.triggerEventHandler('pageChange', 3);
    fixture.detectChanges();

    expect(component.onPageNumberChange).toHaveBeenCalledWith(3);
  });


  it('should read page number and page size from query params on init', async () => {
    activatedRoute.queryParams = of({
      page: '2',
      'page-size': '15',
    });

    fixture.detectChanges();
    await fixture.whenStable();

    expect(component.pageNumber).toBe(2);
    expect(component.pageSize).toBe(15);
  });

  it('should emit pageNumberChange, pageSizeChange, and onPageChange when query params are read',
    async () => {
      spyOn(component.pageNumberChange, 'emit');
      spyOn(component.pageSizeChange, 'emit');
      spyOn(component.onPageChange, 'emit');
      activatedRoute.queryParams = of({
        page: '2',
        'page-size': '15',
      });

      component.ngOnInit();
      await fixture.whenStable();

      expect(component.pageNumberChange.emit).toHaveBeenCalledWith(2);
      expect(component.pageSizeChange.emit).toHaveBeenCalledWith(15);
      expect(component.onPageChange.emit).toHaveBeenCalled();
    });

  it('should update URL query parameters when onPageNumberChange is called', () => {
    component.onPageNumberChange(3);

    expect(router.navigate).toHaveBeenCalledWith([], {
      relativeTo: activatedRoute,
      queryParams: {page: 3},
      queryParamsHandling: 'merge',
    });
  });

  it('should update URL query parameters when onPageSizeChange is called', () => {
    component.onPageSizeChange(20);

    expect(router.navigate).toHaveBeenCalledWith([], {
      relativeTo: activatedRoute,
      queryParams: {'page-size': 20},
      queryParamsHandling: 'merge',
    });
  });

  it('should do nothing given pageNumber is zero when onPageNumberChange is called', () => {
    spyOn(component.pageNumberChange, 'emit');
    spyOn(component.onPageChange, 'emit');

    component.onPageNumberChange(0);

    expect(component.pageNumber).toBe(1);
    expect(component.pageNumberChange.emit).not.toHaveBeenCalled();
    expect(component.onPageChange.emit).not.toHaveBeenCalled();
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should restrict page number input to numeric values only', () => {
    const inputElement = document.createElement('input');
    inputElement.value = '123abc';

    component.restrictToNumbers(inputElement);

    expect(inputElement.value).toBe('123');
  });

  it('should remove leading zeroes from page number input', () => {
    const inputElement = document.createElement('input');
    inputElement.value = '00123';

    component.restrictToNumbers(inputElement);

    expect(inputElement.value).toBe('123');
  });
});
