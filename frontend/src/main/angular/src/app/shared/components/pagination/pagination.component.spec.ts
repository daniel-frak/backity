import {ComponentFixture, TestBed} from '@angular/core/testing';
import {PaginationComponent} from './pagination.component';
import {NgbPagination} from '@ng-bootstrap/ng-bootstrap';
import {By} from '@angular/platform-browser';
import {DebugElement} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {of} from "rxjs";
import SpyObj = jasmine.SpyObj;
import createSpyObj = jasmine.createSpyObj;

describe('PaginationComponent', () => {
  let component: PaginationComponent<unknown>;
  let fixture: ComponentFixture<PaginationComponent<unknown>>;
  let router: SpyObj<Router>;
  let activatedRoute: ActivatedRoute;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PaginationComponent],
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

    fixture.componentRef.setInput('currentPage', {
      totalElements: 100,
      content: Array(10),
      pagination: {
        page: 1,
        size: 1
      },
    });
    fixture.componentRef.setInput('pageSize', 10);
    fixture.componentRef.setInput('pageNumber', 1);
    fixture.componentRef.setInput('availablePageSizes', [5, 10, 15]);
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
    fixture.componentRef.setInput('pageNumber', 1);
    fixture.componentRef.setInput('pageSize', 10);

    expect(component.getFirstElementNumber()).toBe(1);
  });

  it('should return the correct first element number for a middle page', () => {
    fixture.componentRef.setInput('pageNumber', 3);
    fixture.componentRef.setInput('pageSize', 10);

    expect(component.getFirstElementNumber()).toBe(21);
  });

  it('should return the correct last element number for a full page', () => {
    fixture.componentRef.setInput('pageNumber', 1);
    fixture.componentRef.setInput('pageSize', 10);
    fixture.componentRef.setInput('currentPage', {
      totalElements: 100,
      pagination: {
        page: 1,
        size: 1
      },
      content: Array(10),
    });

    expect(component.getLastElementNumber()).toBe(10);
  });

  it('should return the correct last element number for a partially filled page', () => {
    fixture.componentRef.setInput('pageNumber', 10);
    fixture.componentRef.setInput('pageSize', 10);
    fixture.componentRef.setInput('currentPage', {
      totalElements: 95,
      pagination: {
        page: 1,
        size: 1
      },
      content: Array(5),
    });

    expect(component.getLastElementNumber()).toBe(95);
  });

  it('should return the first element number as the last element number for an empty page', () => {
    fixture.componentRef.setInput('pageNumber', 2);
    fixture.componentRef.setInput('pageSize', 10);
    fixture.componentRef.setInput('currentPage', {
      totalElements: 20,
      pagination: {
        page: 1,
        size: 1
      },
      content: [],
    });

    expect(component.getLastElementNumber()).toBe(11);
  });

  it('should return the first element number as the last element number if the page is undefined', () => {
    fixture.componentRef.setInput('pageNumber', 2);
    fixture.componentRef.setInput('pageSize', 10);
    fixture.componentRef.setInput('currentPage', undefined);

    expect(component.getLastElementNumber()).toBe(11);
  });

  it('should return the correct total elements count when available', () => {
    fixture.componentRef.setInput('pageNumber', 2);
    fixture.componentRef.setInput('pageSize', 10);
    fixture.componentRef.setInput('currentPage', {
      totalElements: 150,
      pagination: {
        page: 1,
        size: 1
      },
      content: Array(10),
    });
    expect(component.getTotalElements()).toBe(150);
  });

  it('should return 0 as the total elements count if totalElements is undefined', () => {
    fixture.componentRef.setInput('currentPage', {
      totalElements: undefined,
      pagination: {
        page: 1,
        size: 1
      },
      content: Array(10),
    });

    expect(component.getTotalElements()).toBe(0);
  });

  it('should emit page number change and onPageChange when onPageNumberChange is called' +
    ' with page number different than current', () => {
    const pageNumberChangeSpy = jasmine.createSpy('pageNumberChange');
    fixture.componentRef.instance.pageNumber.subscribe(pageNumberChangeSpy);
    spyOn(component.pageChanged, 'emit');
    const newPageNumber = component.pageNumber() + 1;

    component.onPageNumberChange(newPageNumber);

    expect(pageNumberChangeSpy).toHaveBeenCalledWith(newPageNumber);
    expect(component.pageChanged.emit).toHaveBeenCalled();
  });

  it('should do nothing when onPageNumberChange is called with page number same as current', () => {
    const pageNumberChangeSpy = jasmine.createSpy('pageNumberChange');
    fixture.componentRef.instance.pageNumber.subscribe(pageNumberChangeSpy);
    spyOn(component.pageChanged, 'emit');
    const newPageNumber = component.pageNumber();

    component.onPageNumberChange(newPageNumber);

    expect(pageNumberChangeSpy).not.toHaveBeenCalled();
    expect(component.pageChanged.emit).not.toHaveBeenCalled();
  });

  it('should call onPageSizeChange and onPageChange when page size is changed', () => {
    const pageSizeChangeSpy = jasmine.createSpy('pageSizeChange');
    fixture.componentRef.instance.pageSize.subscribe(pageSizeChangeSpy);
    spyOn(component.pageChanged, 'emit');
    const newPageSize = 20;

    component.onPageSizeChange(newPageSize);

    expect(pageSizeChangeSpy).toHaveBeenCalledWith(newPageSize);
    expect(component.pageChanged.emit).toHaveBeenCalled();
  });

  it('should render pagination options', () => {
    fixture.detectChanges();
    const options = fixture.debugElement.queryAll(By.css('option'));
    expect(options.length).toBe(3);
  });

  it('should disable NgbPagination when disabled input is true', () => {
    fixture.componentRef.setInput('disabled', true);
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

    expect(component.pageNumber()).toBe(2);
    expect(component.pageSize()).toBe(15);
  });

  it('should emit pageNumberChange, pageSizeChange, and onPageChange when query params are read',
    async () => {
      const pageNumberChangeSpy = jasmine.createSpy('pageNumberChange');
      fixture.componentRef.instance.pageNumber.subscribe(pageNumberChangeSpy);
      const pageSizeChangeSpy = jasmine.createSpy('pageSizeChange');
      fixture.componentRef.instance.pageSize.subscribe(pageSizeChangeSpy);
      spyOn(component.pageChanged, 'emit');
      activatedRoute.queryParams = of({
        page: '2',
        'page-size': '15',
      });

      component.ngOnInit();
      await fixture.whenStable();

      expect(pageNumberChangeSpy).toHaveBeenCalledWith(2);
      expect(pageSizeChangeSpy).toHaveBeenCalledWith(15);
      expect(component.pageChanged.emit).toHaveBeenCalled();
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
    const pageNumberChangeSpy = jasmine.createSpy('pageNumberChange');
    fixture.componentRef.instance.pageNumber.subscribe(pageNumberChangeSpy);
    spyOn(component.pageChanged, 'emit');

    component.onPageNumberChange(0);

    expect(component.pageNumber()).toBe(1);
    expect(pageNumberChangeSpy).not.toHaveBeenCalled();
    expect(component.pageChanged.emit).not.toHaveBeenCalled();
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

  it('should not update pageNumber or pageSize when query params are missing', async () => {
    activatedRoute.queryParams = of({});
    fixture.componentRef.setInput('pageNumber', 5);
    fixture.componentRef.setInput('pageSize', 20);

    fixture.detectChanges();
    await fixture.whenStable();

    expect(component.pageNumber()).toBe(5);
    expect(component.pageSize()).toBe(20);
  });
});
