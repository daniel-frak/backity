import {Component, EventEmitter, OnInit, Output, model, input} from '@angular/core';
import {NgbPagination, NgbPaginationPages} from "@ng-bootstrap/ng-bootstrap";
import {Page} from "@app/shared/components/table/page";

import {FormsModule} from "@angular/forms";
import {ActivatedRoute, Params, Router} from '@angular/router';
import {SelectComponent} from "@app/shared/components/select/select.component";

const NOT_NUMBERS_REGEX = /\D/g;
const LEADING_ZEROES_REGEX = /^0+/;

@Component({
    selector: 'app-pagination',
  imports: [
    NgbPagination,
    FormsModule,
    NgbPaginationPages,
    SelectComponent
  ],
    templateUrl: './pagination.component.html',
    styleUrl: './pagination.component.scss'
})
export class PaginationComponent<T> implements OnInit {

  readonly currentPage = input<Page<T>>();

  readonly pageNumber = model<number>(0, {alias: 'pageNumber'});

  readonly pageSize = model<number>(10, {alias: 'pageSize'});

  readonly disabled = input(false);

  @Output()
  onPageChange: EventEmitter<void> = new EventEmitter();

  readonly availablePageSizes = input<number[]>([2, 3, 5, 10, 20]);

  readonly pageNumberQueryParamName = input<string>('page');

  readonly pageSizeQueryParamName = input<string>('page-size');

  constructor(private readonly activatedRoute: ActivatedRoute,
              private readonly router: Router) {
  }

  restrictToNumbers(input: HTMLInputElement) {
    input.value = input.value.replace(NOT_NUMBERS_REGEX, '')
      .replace(LEADING_ZEROES_REGEX, '');
  }

  ngOnInit(): void {
    this.activatedRoute.queryParams.subscribe((params: Params): void => {
      Promise.resolve().then(() => { // Make update async to avoid ExpressionChangedAfterItHasBeenCheckedError
        if (params[this.pageNumberQueryParamName()]) {
          this.pageNumber.set(Number.parseInt(params[this.pageNumberQueryParamName()]));
        }
        if (params[this.pageSizeQueryParamName()]) {
          this.pageSize.set(Number.parseInt(params[this.pageSizeQueryParamName()]));
        }
        this.onPageChange.emit();
      });
    });
  }

  onPageNumberChange(pageNumber: number) {
    if (!pageNumber) {
      return;
    }
    if (this.pageNumber() != pageNumber) {
      this.pageNumber.set(pageNumber);
      this.onPageChange.emit();
      this.updateUrlQueryParams({
        [this.pageNumberQueryParamName()]: pageNumber,
      });
    }
  }

  private updateUrlQueryParams(queryParams: any) {
    this.router.navigate([], {
      relativeTo: this.activatedRoute,
      queryParams: queryParams,
      queryParamsHandling: 'merge',
    });
  }

  onPageSizeChange(pageSize: number) {
    this.pageSize.set(pageSize);
    this.onPageChange.emit();
    this.updateUrlQueryParams({
      [this.pageSizeQueryParamName()]: pageSize,
    });
  }

  getFirstElementNumber(): number {
    return ((this.pageNumber() - 1) * this.pageSize()) + 1;
  }

  getLastElementNumber(): number {
    return this.getFirstElementNumber() + Math.max(0, (this.currentPage()?.content?.length ?? 0) - 1);
  }

  getTotalElements(): number {
    return this.currentPage()?.totalElements ?? 0;
  }

  protected readonly Number = Number;
}
