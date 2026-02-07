import {ComponentFixture, TestBed} from '@angular/core/testing';
import {PaginationComponent} from './pagination.component';
import {NgbPagination} from '@ng-bootstrap/ng-bootstrap';
import {By} from '@angular/platform-browser';
import {DebugElement} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {of} from "rxjs";
import SpyObj = jasmine.SpyObj;

describe('PaginationComponent', () => {
  let component: PaginationComponent<unknown>;
  let fixture: ComponentFixture<PaginationComponent<unknown>>;
  let router: SpyObj<Router>;
  let activatedRoute: ActivatedRoute;

  function setDefaultInputs() {
    fixture.componentRef.setInput('currentPage', {
      totalElements: 100,
      content: Array(10),
      pagination: {page: 1, size: 1}
    });
    fixture.componentRef.setInput('pageSize', 10);
    fixture.componentRef.setInput('pageNumber', 1);
    fixture.componentRef.setInput('availablePageSizes', [5, 10, 15]);
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PaginationComponent],
      providers: [
        {provide: Router, useValue: jasmine.createSpyObj('Router', ['navigate'])},
        {provide: ActivatedRoute, useValue: {queryParams: of({})}}
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(PaginationComponent);
    component = fixture.componentInstance;

    router = TestBed.inject(Router) as SpyObj<Router>;
    activatedRoute = TestBed.inject(ActivatedRoute);
  });

  describe('component initialization', () => {
    it('should create the component', () => {
      expect(component).toBeTruthy();
    });

    it('should read page number and page size from query params', async () => {
      activatedRoute.queryParams = of({
        page: '2',
        'page-size': '15',
      });

      fixture.detectChanges();
      await fixture.whenStable();

      expect(component.pageNumber()).toBe(2);
      expect(component.pageSize()).toBe(15);
    });

    it('should emit pageChanged event when initializing from query params', async () => {
      spyOn(component.pageChanged, 'emit');
      activatedRoute.queryParams = of({
        page: '2',
        'page-size': '15',
      });

      component.ngOnInit();
      await fixture.whenStable();

      expect(component.pageChanged.emit).toHaveBeenCalled();
    });

    it('should preserve existing values when query params are missing', async () => {
      activatedRoute.queryParams = of({});
      fixture.componentRef.setInput('pageNumber', 5);
      fixture.componentRef.setInput('pageSize', 20);

      fixture.detectChanges();
      await fixture.whenStable();

      expect(component.pageNumber()).toBe(5);
      expect(component.pageSize()).toBe(20);
    });

    it('should use custom query param names when provided', async () => {
      fixture.componentRef.setInput('pageNumberQueryParamName', 'currentPage');
      fixture.componentRef.setInput('pageSizeQueryParamName', 'itemsPerPage');
      activatedRoute.queryParams = of({
        currentPage: '3',
        itemsPerPage: '25',
      });

      component.ngOnInit();
      await fixture.whenStable();

      expect(component.pageNumber()).toBe(3);
      expect(component.pageSize()).toBe(25);
    });
  });

  describe('query param validation', () => {
    it('should ignore invalid values from query params', async () => {
      fixture.componentRef.setInput('pageNumber', 5);
      fixture.componentRef.setInput('pageSize', 20);
      activatedRoute.queryParams = of({
        page: 'invalid',
        'page-size': 'NaN',
      });

      component.ngOnInit();
      await fixture.whenStable();

      expect(component.pageNumber()).toBe(5);
      expect(component.pageSize()).toBe(20);
    });

    it('should ignore zero and negative values from query params', async () => {
      fixture.componentRef.setInput('pageNumber', 5);
      fixture.componentRef.setInput('pageSize', 20);

      activatedRoute.queryParams = of({
        page: '0',
        'page-size': '-10',
      });

      component.ngOnInit();
      await fixture.whenStable();

      expect(component.pageNumber()).toBe(5);
      expect(component.pageSize()).toBe(20);
    });

    it('should parse decimal values as integers from query params', async () => {
      fixture.componentRef.setInput('pageNumber', 5);
      fixture.componentRef.setInput('pageSize', 20);
      activatedRoute.queryParams = of({
        page: '2.7',
        'page-size': '15.9',
      });

      component.ngOnInit();
      await fixture.whenStable();

      // parseInt truncates: '2.7' -> 2, '15.9' -> 15
      expect(component.pageNumber()).toBe(2);
      expect(component.pageSize()).toBe(15);
    });
  });

  describe('page element calculations', () => {
    beforeEach(() => setDefaultInputs());

    describe('getFirstElementNumber', () => {
      it('should return 1 for the first page', () => {
        fixture.componentRef.setInput('pageNumber', 1);
        fixture.componentRef.setInput('pageSize', 10);

        expect(component.getFirstElementNumber()).toBe(1);
      });

      it('should calculate correctly for middle pages', () => {
        fixture.componentRef.setInput('pageNumber', 3);
        fixture.componentRef.setInput('pageSize', 10);

        const expected = (3 - 1) * 10 + 1; // 21
        expect(component.getFirstElementNumber()).toBe(expected);
      });
    });

    describe('getLastElementNumber', () => {
      it('should calculate correctly for a full page', () => {
        fixture.componentRef.setInput('pageNumber', 1);
        fixture.componentRef.setInput('pageSize', 10);
        fixture.componentRef.setInput('currentPage', {
          totalElements: 100,
          pagination: {page: 1, size: 1},
          content: Array(10),
        });

        expect(component.getLastElementNumber()).toBe(10);
      });

      it('should calculate correctly for a partially filled page', () => {
        fixture.componentRef.setInput('pageNumber', 10);
        fixture.componentRef.setInput('pageSize', 10);
        fixture.componentRef.setInput('currentPage', {
          totalElements: 95,
          pagination: {page: 1, size: 1},
          content: Array(5),
        });

        expect(component.getLastElementNumber()).toBe(95);
      });

      it('should handle empty pages correctly', () => {
        fixture.componentRef.setInput('pageNumber', 2);
        fixture.componentRef.setInput('pageSize', 10);
        fixture.componentRef.setInput('currentPage', {
          totalElements: 20,
          pagination: {page: 1, size: 1},
          content: [],
        });

        expect(component.getLastElementNumber()).toBe(11);
      });

      it('should handle undefined currentPage gracefully', () => {
        fixture.componentRef.setInput('pageNumber', 2);
        fixture.componentRef.setInput('pageSize', 10);
        fixture.componentRef.setInput('currentPage', undefined);

        expect(component.getLastElementNumber()).toBe(11);
      });
    });

    describe('getTotalElements', () => {
      it('should return the total elements count when available', () => {
        fixture.componentRef.setInput('currentPage', {
          totalElements: 150,
          pagination: {page: 1, size: 1},
          content: Array(10),
        });

        expect(component.getTotalElements()).toBe(150);
      });

      it('should return 0 when totalElements is undefined', () => {
        fixture.componentRef.setInput('currentPage', {
          totalElements: undefined,
          pagination: {page: 1, size: 1},
          content: Array(10),
        });

        expect(component.getTotalElements()).toBe(0);
      });

      it('should return 0 when currentPage is undefined', () => {
        fixture.componentRef.setInput('currentPage', undefined);

        expect(component.getTotalElements()).toBe(0);
      });
    });
  });

  describe('page number changes', () => {
    beforeEach(() => setDefaultInputs());

    it('should update page number and emit event when value changes', () => {
      spyOn(component.pageChanged, 'emit');
      const newPageNumber: number = component.pageNumber() + 1;

      component.onPageNumberChange(newPageNumber);

      expect(component.pageNumber()).toBe(newPageNumber);
      expect(component.pageChanged.emit).toHaveBeenCalled();
    });

    it('should not emit event when page number remains the same', () => {
      spyOn(component.pageChanged, 'emit');
      const currentPageNumber: number = component.pageNumber();

      component.onPageNumberChange(currentPageNumber);

      expect(component.pageChanged.emit).not.toHaveBeenCalled();
    });

    it('should ignore invalid page numbers', () => {
      fixture.componentRef.setInput('pageNumber', 5);
      spyOn(component.pageChanged, 'emit');

      component.onPageNumberChange(NaN);
      component.onPageNumberChange(0);
      component.onPageNumberChange(-1);

      expect(component.pageNumber()).toBe(5);
      expect(component.pageChanged.emit).not.toHaveBeenCalled();
    });
  });

  describe('page size changes', () => {
    beforeEach(() => setDefaultInputs());

    it('should update page size and emit event', () => {
      spyOn(component.pageChanged, 'emit');
      const newPageSize = 20;

      component.onPageSizeChange(newPageSize);

      expect(component.pageSize()).toBe(newPageSize);
      expect(component.pageChanged.emit).toHaveBeenCalled();
    });

    it('should ignore invalid page sizes', () => {
      fixture.componentRef.setInput('pageSize', 20);
      spyOn(component.pageChanged, 'emit');

      component.onPageSizeChange(NaN);
      component.onPageSizeChange(0);
      component.onPageSizeChange(-1);

      expect(component.pageSize()).toBe(20);
      expect(component.pageChanged.emit).not.toHaveBeenCalled();
    });
  });

  describe('URL synchronization', () => {
    beforeEach(() => setDefaultInputs());

    it('should update URL when page number changes', async () => {
      fixture.detectChanges();
      router.navigate.calls.reset();

      fixture.componentRef.setInput('pageNumber', 3);
      fixture.detectChanges();
      await fixture.whenStable();

      expect(router.navigate).toHaveBeenCalledWith([], {
        relativeTo: activatedRoute,
        queryParams: {
          'page': 3,
          'page-size': 10,
        },
        queryParamsHandling: 'merge',
      });
    });

    it('should update URL when page size changes', async () => {
      fixture.detectChanges();
      router.navigate.calls.reset();

      fixture.componentRef.setInput('pageSize', 20);
      fixture.detectChanges();
      await fixture.whenStable();

      expect(router.navigate).toHaveBeenCalledWith([], {
        relativeTo: activatedRoute,
        queryParams: {
          'page': 1,
          'page-size': 20,
        },
        queryParamsHandling: 'merge',
      });
    });

    it('should use custom query param names in URL updates', async () => {
      fixture.componentRef.setInput('pageNumberQueryParamName', 'currentPage');
      fixture.componentRef.setInput('pageSizeQueryParamName', 'itemsPerPage');
      fixture.detectChanges();
      router.navigate.calls.reset();

      fixture.componentRef.setInput('pageNumber', 2);
      fixture.detectChanges();
      await fixture.whenStable();

      expect(router.navigate).toHaveBeenCalledWith([], {
        relativeTo: activatedRoute,
        queryParams: {
          'currentPage': 2,
          'itemsPerPage': 10,
        },
        queryParamsHandling: 'merge',
      });
    });
  });

  describe('input sanitization', () => {
    it('should remove non-numeric characters from input', () => {
      const inputElement = document.createElement('input');
      inputElement.value = '123abc456';

      component.restrictToNumbers(inputElement);

      expect(inputElement.value).toBe('123456');
    });

    it('should remove leading zeros from input', () => {
      const inputElement = document.createElement('input');
      inputElement.value = '00123';

      component.restrictToNumbers(inputElement);

      expect(inputElement.value).toBe('123');
    });

    it('should handle combination of leading zeros and non-numeric characters', () => {
      const inputElement = document.createElement('input');
      inputElement.value = '00abc123def';

      component.restrictToNumbers(inputElement);

      expect(inputElement.value).toBe('123');
    });
  });

  describe('template rendering', () => {
    beforeEach(() => setDefaultInputs());

    it('should display correct pagination summary text', () => {
      fixture.detectChanges();

      const paginationText = fixture.debugElement
        .query(By.css('.pagination-options'))
        .nativeElement.textContent.trim();

      expect(paginationText).toContain('Showing 1-10 of 100');
    });

    it('should render available page size options', () => {
      fixture.detectChanges();

      const options = fixture.debugElement.queryAll(By.css('option'));
      expect(options.length).toBe(3);
    });

    it('should not render pagination when page is empty', () => {
      fixture.componentRef.setInput('currentPage', {
        totalElements: 0,
        pagination: {page: 1, size: 1},
        content: [],
      });
      fixture.detectChanges();

      const paginationContainer = fixture.debugElement.query(By.css('.pagination-container'));
      expect(paginationContainer).toBeNull();
    });

    it('should not render pagination when currentPage is undefined', () => {
      fixture.componentRef.setInput('currentPage', undefined);
      fixture.detectChanges();

      const paginationContainer = fixture.debugElement.query(By.css('.pagination-container'));
      expect(paginationContainer).toBeNull();
    });
  });

  describe('NgbPagination integration', () => {
    beforeEach(() => setDefaultInputs());

    it('should disable pagination when disabled input is true', () => {
      fixture.componentRef.setInput('disabled', true);
      fixture.detectChanges();

      const pagination: DebugElement = fixture.debugElement.query(By.directive(NgbPagination));
      expect(pagination.componentInstance.disabled).toBeTrue();
    });

    it('should call onPageNumberChange when page changes via NgbPagination', () => {
      fixture.detectChanges();
      spyOn(component, 'onPageNumberChange');
      const pagination: DebugElement = fixture.debugElement.query(By.directive(NgbPagination));

      pagination.triggerEventHandler('pageChange', 3);
      fixture.detectChanges();

      expect(component.onPageNumberChange).toHaveBeenCalledWith(3);
    });

    it('should pass correct properties to NgbPagination', () => {
      fixture.detectChanges();
      const pagination: DebugElement = fixture.debugElement.query(By.directive(NgbPagination));

      expect(pagination.componentInstance.collectionSize).toBe(100);
      expect(pagination.componentInstance.page).toBe(1);
      expect(pagination.componentInstance.pageSize).toBe(10);
      expect(pagination.componentInstance.boundaryLinks).toBeTrue();
    });
  });
});
