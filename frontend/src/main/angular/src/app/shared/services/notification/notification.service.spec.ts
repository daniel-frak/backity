import {TestBed} from '@angular/core/testing';
import {NotificationService} from './notification.service';
import {UserNotification} from '@app/shared/services/notification/userNotification';

describe('NotificationService', () => {
  let service: NotificationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(NotificationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should add a normal notification', () => {
    const content = 'Test Notification';

    service.show(content);

    expect(service.notifications.length).toBe(1);
    expect(service.notifications[0].content).toBe(content);
    expect(service.notifications[0].title).toBeUndefined();
    expect(service.notifications[0].shouldShowLightText).toBeFalse();
  });

  it('should add a success notification', () => {
    const content = 'Success Notification';

    service.showSuccess(content);

    expect(service.notifications.length).toBe(1);
    expect(service.notifications[0].content).toBe(content);
    expect(service.notifications[0].title).toBeUndefined();
    expect(service.notifications[0].class).toBe('bg-success text-light');
    expect(service.notifications[0].shouldShowLightText).toBeTrue();
  });

  it('should add a failure notification', () => {
    const content = 'Failure Notification';

    service.showFailure(content);

    expect(service.notifications.length).toBe(1);
    expect(service.notifications[0].content).toBe(content);
    expect(service.notifications[0].title).toBeUndefined();
    expect(service.notifications[0].class).toBe('bg-danger text-light');
    expect(service.notifications[0].shouldShowLightText).toBeTrue();
  });

  it('should remove a notification', () => {
    const content = 'Test Notification';
    const notification: UserNotification = {title: 'Test', content, shouldShowLightText: false};
    service.notifications.push(notification);

    service.remove(notification);

    expect(service.notifications.length).toBe(0);
  });

  it('should not remove a non-existing notification', () => {
    const content = 'Test Notification';
    const notification: UserNotification = {content};
    service.notifications.push(notification);
    const nonExistingNotification: UserNotification = {
      content: 'Nope'
    };

    service.remove(nonExistingNotification);

    expect(service.notifications.length).toBe(1);
    expect(service.notifications[0]).toBe(notification);
  });

  it('should add default notification with a title', () => {
    const content = 'Test Notification';
    const title = 'Test Title';

    service.show(content, title);
    expect(service.notifications[0].title).toBe(title);
  });

  it('should add success notification with a title', () => {
    const content = 'Test Notification';
    const title = 'Test Title';

    service.showSuccess(content, title);

    expect(service.notifications[0].title).toBe(title);
    expect(service.notifications[0].class).toBe('bg-success text-light');
    expect(service.notifications[0].shouldShowLightText).toBeTrue();
  });

  it('should add failure notification with a title', () => {
    const content = 'Test Notification';
    const title = 'Test Title';

    service.showFailure(content, title);

    expect(service.notifications[0].title).toBe(title);
    expect(service.notifications[0].class).toBe('bg-danger text-light');
    expect(service.notifications[0].shouldShowLightText).toBeTrue();
  });
});
