import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {NgbPagination, NgbPaginationPages} from "@ng-bootstrap/ng-bootstrap";
import {TableContent} from "@app/shared/components/table/table-content";
import {NgForOf, NgIf} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {ActivatedRoute, Params, Router} from '@angular/router';

const NOT_NUMBERS_REGEX = /\D/g;
const LEADING_ZEROES_REGEX = /^0+/;

@Component({
  selector: 'app-pagination',
  standalone: true,
  imports: [
    NgbPagination,
    NgIf,
    FormsModule,
    NgForOf,
    NgbPaginationPages
  ],
  templateUrl: './pagination.component.html',
  styleUrl: './pagination.component.scss',
})
export class PaginationComponent implements OnInit {

  @Input()
  currentPage?: TableContent;

  @Input()
  pageNumber: number = 0;

  @Output()
  pageNumberChange: EventEmitter<number> = new EventEmitter();

  @Input()
  pageSize: number = 10;

  @Output()
  pageSizeChange: EventEmitter<number> = new EventEmitter<number>();

  @Input()
  disabled = false;

  @Output()
  onPageChange: EventEmitter<void> = new EventEmitter();

  @Input()
  availablePageSizes: number[] = [2, 3, 5, 10, 20];

  @Input()
  pageNumberQueryParamName: string = 'page';

  @Input()
  pageSizeQueryParamName: string = 'page-size';

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
        if (params[this.pageNumberQueryParamName]) {
          this.pageNumber = Number.parseInt(params[this.pageNumberQueryParamName]);
          this.pageNumberChange.emit(this.pageNumber);
        }
        if (params[this.pageSizeQueryParamName]) {
          this.pageSize = Number.parseInt(params[this.pageSizeQueryParamName]);
          this.pageSizeChange.emit(this.pageSize);
        }
        this.onPageChange.emit();
      });
    });
  }

  onPageNumberChange(pageNumber: number) {
    if (!pageNumber) {
      return;
    }
    if (this.pageNumber != pageNumber) {
      this.pageNumberChange.emit(pageNumber);
      this.onPageChange.emit();
      this.updateUrlQueryParams({
        [this.pageNumberQueryParamName]: pageNumber,
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
    this.pageSizeChange.emit(pageSize);
    this.onPageChange.emit();
    this.updateUrlQueryParams({
      [this.pageSizeQueryParamName]: pageSize,
    });
  }

  getFirstElementNumber(): number {
    return ((this.pageNumber - 1) * this.pageSize) + 1;
  }

  getLastElementNumber(): number {
    return this.getFirstElementNumber() + Math.max(0, (this.currentPage?.content?.length ?? 0) - 1);
  }

  getTotalElements(): number {
    return this.currentPage?.totalElements ?? 0;
  }

  protected readonly Number = Number;
}
