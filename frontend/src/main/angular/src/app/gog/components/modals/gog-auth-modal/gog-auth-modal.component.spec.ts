import {ComponentFixture, TestBed} from '@angular/core/testing';

import {GogAuthModalComponent} from './gog-auth-modal.component';
import {GOGAuthenticationClient} from "@backend";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {of, tap, throwError} from "rxjs";
import SpyObj = jasmine.SpyObj;
import createSpyObj = jasmine.createSpyObj;
import any = jasmine.any;

const USER_AUTH_URL = "someGogAuthUrl";

const GOG_CONFIG_RESPONSE = {
  userAuthUrl: USER_AUTH_URL
};

describe('GogAuthModalComponent', () => {
  let component: GogAuthModalComponent;
  let fixture: ComponentFixture<GogAuthModalComponent>;

  let gogAuthClientMock: SpyObj<GOGAuthenticationClient>;
  let notificationService: NotificationService;
  let ngbActiveModalSpy: SpyObj<NgbActiveModal>;

  beforeEach(async () => {
    const modalMock = createSpyObj('NgbActiveModal', ['close', 'dismiss']);

    await TestBed.configureTestingModule({
      imports: [GogAuthModalComponent],
      providers: [
        {provide: NgbActiveModal, useValue: modalMock},
        {
          provide: GOGAuthenticationClient,
          useValue: createSpyObj(GOGAuthenticationClient, ['authenticateGog'])
        },
        {
          provide: NotificationService,
          useValue: createSpyObj('NotificationService', ['showSuccess', 'showFailure'])
        },
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(GogAuthModalComponent);
    component = fixture.componentInstance;

    ngbActiveModalSpy = TestBed.inject(NgbActiveModal) as SpyObj<NgbActiveModal>;
    gogAuthClientMock = TestBed.inject(GOGAuthenticationClient) as SpyObj<GOGAuthenticationClient>;
    notificationService = TestBed.inject(NotificationService);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should open a new window for authenticating', () => {
    spyOn(window, 'open');
    component.gogAuthUrl = USER_AUTH_URL;
    component.showGogAuthPopup();

    expect(window.open).toHaveBeenCalledWith(GOG_CONFIG_RESPONSE.userAuthUrl,
      '_blank', 'toolbar=0,location=0,menubar=0');
  });

  it('should authenticate with a valid URL', () => {
    component.gogCodeUrlInput.setValue('https://www.example.com?code=1234');
    gogAuthClientMock.authenticateGog.and.returnValue(
      of({refresh_token: 'someRefreshToken'}).pipe(
        tap(() => {
          expect(component.isLoading).toBeTrue();
        })
      ) as any
    );

    component.authenticateGog();

    expect(ngbActiveModalSpy.close).toHaveBeenCalledWith(true);
    expect(component.isLoading).toBeFalse();
    expect(notificationService.showSuccess).toHaveBeenCalledWith('GOG authentication successful');
    expect(notificationService.showFailure).toHaveBeenCalledTimes(0);
    expect(component.gogCodeUrlInput.value).toBe('')
  });

  it('should not authenticate if refresh token is missing', () => {
    component.gogCodeUrlInput.setValue('https://www.example.com?code=1234');
    gogAuthClientMock.authenticateGog.and.returnValue(
      of({refresh_token: undefined}).pipe(
        tap(() => {
          expect(component.isLoading).toBeTrue();
        })
      ) as any
    );

    component.authenticateGog();

    expect(ngbActiveModalSpy.close).not.toHaveBeenCalled();
    expect(component.isLoading).toBeFalse();
    expect(notificationService.showFailure)
      .toHaveBeenCalledWith('Something went wrong during GOG authentication');
  });

  it('should not authenticate given GOG code URL is empty', () => {
    component.gogCodeUrlInput.setValue('');

    component.authenticateGog();

    expect(ngbActiveModalSpy.close).not.toHaveBeenCalled();
    expect(gogAuthClientMock.authenticateGog).not.toHaveBeenCalled();
    const expectedErrors = {
      gogCodeUrl: {required: true}
    };
    expect(notificationService.showFailure)
      .toHaveBeenCalledWith("Please check the form for errors and try again.", expectedErrors);
  });

  it('should not authenticate given response returns error', () => {
    component.gogCodeUrlInput.setValue('https://www.example.com?code=1234');
    gogAuthClientMock.authenticateGog.and.returnValue(
      throwError(() => new Error('Authentication failed')));

    component.authenticateGog();

    expect(ngbActiveModalSpy.close).not.toHaveBeenCalled();
    expect(component.isLoading).toBeFalse();
    expect(notificationService.showFailure)
      .toHaveBeenCalledWith("Something went wrong during GOG authentication")
  });

  it('should not authenticate given authentication throws', () => {
    component.gogCodeUrlInput.setValue('invalidUrl'); // Will throw during new URL construction

    component.authenticateGog();

    expect(ngbActiveModalSpy.close).not.toHaveBeenCalled();
    expect(component.isLoading).toBeFalse();
    expect(notificationService.showFailure)
      .toHaveBeenCalledWith("Something went wrong during GOG authentication", any(Error))
  });

  it('authenticateGog should throw given code is missing from URL', () => {
    component.gogCodeUrlInput.setValue('https://www.missingcodeparam.com');

    component.authenticateGog();

    expect(ngbActiveModalSpy.close).not.toHaveBeenCalled();
    expect(component.isLoading).toBeFalse();
    expect(notificationService.showFailure)
      .toHaveBeenCalledWith("Invalid URL: missing 'code' parameter");
  });
});
