import {Pagination} from "@backend";

export class Page<T> {
  content: T[] = [];
  totalPages?: number;
  totalElements?: number;
  pagination?: Pagination;
}
