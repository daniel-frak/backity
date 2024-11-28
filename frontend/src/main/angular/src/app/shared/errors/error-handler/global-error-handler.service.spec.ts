import {TestBed} from '@angular/core/testing';
import {GlobalErrorHandler} from './global-error-handler.service';
import {NotificationService} from '@app/shared/services/notification/notification.service';
import {NgZone} from '@angular/core';

class MockNgZone extends NgZone {
  constructor() {
    super({enableLongStackTrace: false});
  }

  run(fn: Function): any {
    return fn();
  }
}

describe('GlobalErrorHandler', () => {
  let errorHandler: GlobalErrorHandler;
  let notificationServiceSpy: jasmine.SpyObj<NotificationService>;
  let mockNgZone: MockNgZone;

  beforeEach(() => {
    notificationServiceSpy = jasmine.createSpyObj('NotificationService', ['showFailure']);
    mockNgZone = new MockNgZone();

    TestBed.configureTestingModule({
      providers: [
        GlobalErrorHandler,
        {provide: NotificationService, useValue: notificationServiceSpy},
        {provide: NgZone, useValue: mockNgZone}
      ]
    });

    errorHandler = TestBed.inject(GlobalErrorHandler);
  });

  it('should be created', () => {
    expect(errorHandler).toBeTruthy();
  });

  it('should call showFailure on notificationService when handleError is invoked', () => {
    const error = new Error('Test error');

    errorHandler.handleError(error);

    expect(notificationServiceSpy.showFailure)
      .toHaveBeenCalledWith('An unexpected error has occurred', undefined, error);
  });
});
