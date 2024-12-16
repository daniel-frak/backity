import {Component, EventEmitter, Input, Output} from '@angular/core';
import {NgbPagination} from "@ng-bootstrap/ng-bootstrap";
import {TableContent} from "@app/shared/components/table/table-content";
import {NgForOf, NgIf} from "@angular/common";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-pagination',
  standalone: true,
  imports: [
    NgbPagination,
    NgIf,
    FormsModule,
    NgForOf
  ],
  templateUrl: './pagination.component.html',
  styleUrl: './pagination.component.scss',
})
export class PaginationComponent {

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

  emitPageChange(pageNumber: number) {
    if (this.pageNumber != pageNumber) {
      this.pageNumberChange.emit(pageNumber );
      this.onPageChange.emit();
    }
  }

  onPageSizeChange(rowsPerPage: number) {
    this.pageSizeChange.emit(rowsPerPage);
    this.onPageChange.emit();
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
}
