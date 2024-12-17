import { TestBed } from '@angular/core/testing';
import { GlobalErrorHandler } from './global-error-handler.service';
import { NotificationService } from '@app/shared/services/notification/notification.service';
import { NgZone } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';

class MockNgZone extends NgZone {
  constructor() {
    super({ enableLongStackTrace: false });
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
        { provide: NotificationService, useValue: notificationServiceSpy },
        { provide: NgZone, useValue: mockNgZone }
      ]
    });

    errorHandler = TestBed.inject(GlobalErrorHandler);
  });

  it('should be created', () => {
    expect(errorHandler).toBeTruthy();
  });

  it('should call showFailure on notificationService for standard errors', () => {
    const error = new Error('Test error');

    errorHandler.handleError(error);

    expect(notificationServiceSpy.showFailure)
      .toHaveBeenCalledWith(
        'An unexpected error has occurred',
        error);
  });

  it('should call showFailure on notificationService for HttpErrorResponse with status 400', () => {
    const error = new HttpErrorResponse({
      status: 400
    });

    errorHandler.handleError(error);

    expect(notificationServiceSpy.showFailure)
      .toHaveBeenCalledWith(
        "The request couldn't be processed. Please check the information you entered and try again.",
        error
      );
  });

  it('should call showFailure on notificationService for HttpErrorResponse with status 401', () => {
    const error = new HttpErrorResponse({
      status: 401
    });

    errorHandler.handleError(error);

    expect(notificationServiceSpy.showFailure)
      .toHaveBeenCalledWith(
        "You're not authorized to perform this action.",
        error
      );
  });

  it('should call showFailure on notificationService for HttpErrorResponse with status 404', () => {
    const error = new HttpErrorResponse({
      status: 404
    });

    errorHandler.handleError(error);

    expect(notificationServiceSpy.showFailure)
      .toHaveBeenCalledWith(
        "The requested resource was not found.",
        error
      );
  });

  it('should call showFailure on notificationService for HttpErrorResponse with status 405', () => {
    const error = new HttpErrorResponse({
      status: 405
    });

    errorHandler.handleError(error);

    expect(notificationServiceSpy.showFailure)
      .toHaveBeenCalledWith(
        "This action is not supported.",
        error
      );
  });

  it('should call showFailure on notificationService for HttpErrorResponse with status 408', () => {
    const error = new HttpErrorResponse({
      status: 408
    });

    errorHandler.handleError(error);

    expect(notificationServiceSpy.showFailure)
      .toHaveBeenCalledWith(
        "The request timed out. Please ensure your network is stable and try again.",
        error
      );
  });

  it('should call showFailure on notificationService for HttpErrorResponse with status 500', () => {
    const error = new HttpErrorResponse({
      status: 500
    });

    errorHandler.handleError(error);

    expect(notificationServiceSpy.showFailure)
      .toHaveBeenCalledWith(
        "An unexpected server error has occurred.",
        error
      );
  });

  it('should use error.error.message if messageKey exists', () => {
    const error = new HttpErrorResponse({
      status: 500,
      error: { messageKey: 'true', message: 'Custom server error message' }
    });

    errorHandler.handleError(error);

    expect(notificationServiceSpy.showFailure)
      .toHaveBeenCalledWith(
        "Custom server error message",
        error
      );
  });

  it('should call showFailure on notificationService for HttpErrorResponse with unrecognized status', () => {
    const error = new HttpErrorResponse({
      status: 418
    });

    errorHandler.handleError(error);

    expect(notificationServiceSpy.showFailure)
      .toHaveBeenCalledWith(
        "An unexpected network error has occurred (status: 418).",
        error
      );
  });
});
