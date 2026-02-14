export interface ModalRef<T = any> {
  result: Promise<any>;
  componentInstance: T;
}
