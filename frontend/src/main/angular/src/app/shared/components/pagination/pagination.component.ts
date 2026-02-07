import {Component, effect, EventEmitter, input, model, OnInit, Output} from '@angular/core';
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
  readonly pageNumber = model<number>(1);
  readonly pageSize = model<number>(10);
  readonly disabled = input(false);

  @Output()
  pageChanged: EventEmitter<void> = new EventEmitter();

  readonly availablePageSizes = input<number[]>([2, 3, 5, 10, 20]);
  readonly pageNumberQueryParamName = input<string>('page');
  readonly pageSizeQueryParamName = input<string>('page-size');

  protected readonly Number = Number;

  constructor(private readonly activatedRoute: ActivatedRoute,
              private readonly router: Router) {
    effect(() => {
      const pageNumberValue = this.pageNumber();
      const pageSizeValue = this.pageSize();

      this.updateUrlQueryParams({
        [this.pageNumberQueryParamName()]: pageNumberValue,
        [this.pageSizeQueryParamName()]: pageSizeValue,
      });
    });
  }

  restrictToNumbers(input: HTMLInputElement) {
    input.value = input.value.replace(NOT_NUMBERS_REGEX, '')
      .replace(LEADING_ZEROES_REGEX, '');
  }

  ngOnInit(): void {
    this.activatedRoute.queryParams
      .subscribe((params: Params): void => {
        Promise.resolve().then(() => { // Make update async to avoid ExpressionChangedAfterItHasBeenCheckedError
          const pageNumberParam = params[this.pageNumberQueryParamName()];
          if (pageNumberParam) {
            const parsedPageNumber = Number.parseInt(pageNumberParam, 10);
            if (Number.isFinite(parsedPageNumber) && parsedPageNumber > 0) {
              this.pageNumber.set(parsedPageNumber);
            }
          }

          const pageSizeParam = params[this.pageSizeQueryParamName()];
          if (pageSizeParam) {
            const parsedPageSize = Number.parseInt(pageSizeParam, 10);
            if (Number.isFinite(parsedPageSize) && parsedPageSize > 0) {
              this.pageSize.set(parsedPageSize);
            }
          }
          this.pageChanged.emit();
        });
      });
  }

  onPageNumberChange(pageNumber: number) {
    if (!Number.isFinite(pageNumber) || pageNumber <= 0) {
      return;
    }
    if (this.pageNumber() != pageNumber) {
      this.pageNumber.set(pageNumber);
      this.pageChanged.emit();
    }
  }

  onPageSizeChange(pageSize: number) {
    if (!Number.isFinite(pageSize) || pageSize <= 0) {
      return;
    }
    this.pageSize.set(pageSize);
    this.pageChanged.emit();
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

  private updateUrlQueryParams(queryParams: Params) {
    void this.router.navigate([], {
      relativeTo: this.activatedRoute,
      queryParams: queryParams,
      queryParamsHandling: 'merge',
    });
  }
}
