import {ComponentFixture, TestBed} from '@angular/core/testing';
import {NotificationContainerComponent} from './notification-container.component';
import {NgbToastModule} from '@ng-bootstrap/ng-bootstrap';
import {NotificationService} from '@app/shared/services/notification/notification.service';
import {By} from '@angular/platform-browser';
import {DebugElement} from '@angular/core';
import {CommonModule} from '@angular/common';

describe('NotificationContainerComponent', () => {
  let component: NotificationContainerComponent;
  let fixture: ComponentFixture<NotificationContainerComponent>;
  let notificationService: NotificationService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        NotificationContainerComponent,
        NgbToastModule,
        CommonModule
      ],
      declarations: [],
      providers: [NotificationService]
    }).compileComponents();

    fixture = TestBed.createComponent(NotificationContainerComponent);
    component = fixture.componentInstance;
    notificationService = TestBed.inject(NotificationService);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display notifications', () => {
    notificationService.notifications = [
      {
        title: 'Test Notification 1',
        content: 'Content 1',
      }
    ];

    fixture.detectChanges();

    const toastElements: DebugElement[] = fixture.debugElement.queryAll(By.css('ngb-toast'));
    expect(toastElements.length).toBe(1);

    const contentElement: DebugElement = fixture.debugElement.query(By.css('.toast-body div'));
    expect(contentElement.nativeElement.textContent.trim()).toBe('Content 1');
  });

  it('should remove notification when close button is clicked', () => {
    notificationService.notifications = [
      {
        title: '',
        content: 'Some content'
      }
    ];

    fixture.detectChanges();

    const closeButton: DebugElement = fixture.debugElement.query(By.css('.btn-close'));
    expect(closeButton).toBeTruthy();

    closeButton.triggerEventHandler('click', null);
    fixture.detectChanges();

    expect(notificationService.notifications.length).toBe(0);
  });

  it('should apply the correct classes to the close button', () => {
    notificationService.notifications = [
      {
        title: '',
        content: 'Light Text',
        shouldShowLightText: true
      },
      {
        title: '',
        content: 'Normal Text',
        shouldShowLightText: false
      }
    ];

    fixture.detectChanges();

    const closeButtonElements: DebugElement[] = fixture.debugElement.queryAll(By.css('.btn-close'));
    expect(closeButtonElements.length).toBe(2);
    expect(closeButtonElements[0].nativeElement.classList).toContain('btn-close-white');
    expect(closeButtonElements[1].nativeElement.classList).not.toContain('btn-close-white');
  });

  it('should remove notification when hidden event is triggered', () => {
    notificationService.notifications = [
      {
        content: 'This will hide'
      }
    ];

    fixture.detectChanges();

    const toastElement: DebugElement = fixture.debugElement.query(By.css('ngb-toast'));
    expect(toastElement).toBeTruthy();

    toastElement.triggerEventHandler('hidden', null);
    fixture.detectChanges();

    expect(notificationService.notifications.length).toBe(0);
  });
});
