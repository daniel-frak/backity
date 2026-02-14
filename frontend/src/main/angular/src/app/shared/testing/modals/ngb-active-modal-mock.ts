import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";

/**
 * Mock for {@link NgbActiveModal}.
 */
export class NgbActiveModalMock {

  private readonly _closeCalls: any[] = [];
  private readonly _dismissCalls: any[] = [];

  close(result?: any) {
    this._closeCalls.push(result);
  }

  dismiss(reason?: any) {
    this._dismissCalls.push(reason);
  }

  get timesClosed(): number {
    return this._closeCalls.length;
  }

  get timesDismissed(): number {
    return this._dismissCalls.length;
  }
}
