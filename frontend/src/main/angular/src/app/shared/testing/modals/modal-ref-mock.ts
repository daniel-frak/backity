import {ModalRef} from "@app/shared/services/modal-service/modal-ref";

/**
 * Mock for {@link ModalRef}.
 */
export class ModalRefMock<T = any> implements ModalRef<T> {
  componentInstance: T;
  result!: Promise<any>;

  private _resolve!: (value?: any) => void;
  private _reject!: (reason?: any) => void;

  private nextOutcome?: { type: 'resolve' | 'reject'; value?: any };

  timesOpened: number = 0;

  constructor(componentInstance: T) {
    this.componentInstance = componentInstance;
    this.resetPromise();
  }

  private resetPromise() {
    this.result = new Promise((resolve, reject) => {
      this._resolve = resolve;
      this._reject = reject;
    });
  }

  resolveWhenOpened(value?: any) {
    this.nextOutcome = {type: 'resolve', value};
  }

  rejectWhenOpened(reason?: any) {
    this.nextOutcome = {type: 'reject', value: reason};
  }

  resolveOrReject() {
    this.timesOpened++;
    this.resetPromise();

    if (this.nextOutcome) {
      const currentOutcome = this.nextOutcome;
      this.nextOutcome = undefined;

      Promise.resolve().then(() => {
        if (currentOutcome?.type === 'resolve') {
          this._resolve(currentOutcome.value);
        } else {
          this._reject(currentOutcome.value);
        }
      });
    }
  }
}
