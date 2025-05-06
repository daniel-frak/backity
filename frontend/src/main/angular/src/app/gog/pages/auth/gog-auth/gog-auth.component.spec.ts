import {ComponentFixture, TestBed} from '@angular/core/testing';
import {GogAuthComponent} from './gog-auth.component';
import {GOGAuthenticationClient, GOGConfigurationClient, RefreshTokenResponse} from "@backend";
import {defer, of, throwError} from "rxjs";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {By} from '@angular/platform-browser';
import {DebugElement} from "@angular/core";
import {FormGroup} from "@angular/forms";
import createSpyObj = jasmine.createSpyObj;
import SpyObj = jasmine.SpyObj;

const USER_AUTH_URL = "someGogAuthUrl";

const GOG_CONFIG_RESPONSE = {
  userAuthUrl: USER_AUTH_URL
};
describe('GogAuthComponent', () => {
  let component: GogAuthComponent;
  let fixture: ComponentFixture<GogAuthComponent>;

  let gogConfigClientMock: SpyObj<GOGConfigurationClient>;
  let gogAuthClientMock: SpyObj<GOGAuthenticationClient>;
  let notificationService: NotificationService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GogAuthComponent],
      providers: [
        {
          provide: GOGConfigurationClient,
          useValue: createSpyObj(GOGConfigurationClient, ['getGogConfig'])
        },
        {
          provide: GOGAuthenticationClient,
          useValue: createSpyObj(GOGAuthenticationClient, ['checkAuthentication', 'authenticate', 'logOutOfGog'])
        },
        {
          provide: NotificationService,
          useValue: createSpyObj('NotificationService', ['showSuccess', 'showFailure'])
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(GogAuthComponent);
    component = fixture.componentInstance;

    gogConfigClientMock = TestBed.inject(GOGConfigurationClient) as SpyObj<GOGConfigurationClient>;
    gogAuthClientMock = TestBed.inject(GOGAuthenticationClient) as SpyObj<GOGAuthenticationClient>;
    gogAuthClientMock.checkAuthentication.and.returnValue(of(false) as any);
    notificationService = TestBed.inject(NotificationService);

    gogConfigClientMock.getGogConfig.and.returnValue(of(GOG_CONFIG_RESPONSE) as any);

    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should throw error when gogCodeUrlInput is not found', () => {
    component.gogAuthForm = new FormGroup({});
    expect(() => component.gogCodeUrlInput)
      .toThrow(new Error('The control "gogCodeUrl" does not exist in the form.'));
  });

  it('should check authentication status on init', () => {
    gogAuthClientMock.checkAuthentication.and.returnValue(defer(() => {
      expect(component.gogIsLoading).toBeTrue();
      return of(true);
    }) as any);

    component.ngOnInit();

    expect(component.gogAuthenticated).toBeTrue();
    expect(component.gogIsLoading).toBeFalse();
  });

  it('should notify and disable loading on authentication check failure', () => {
    const error = new Error('Test error');
    gogAuthClientMock.checkAuthentication.and.returnValue(throwError(() => error));

    component.ngOnInit();

    expect(component.gogIsLoading).toBeFalse();
    expect(notificationService.showFailure).toHaveBeenCalledWith(
      'Failed to configure GOG', error);
  });

  it('should notify and disable loading on configuration check failure', () => {
    const error = new Error('Test error');
    gogConfigClientMock.getGogConfig.and.returnValue(throwError(() => error));

    component.ngOnInit();

    expect(component.gogIsLoading).toBeFalse();
    expect(notificationService.showFailure).toHaveBeenCalledWith(
      'Failed to configure GOG', error);
  });

  it('should open a new window for authenticating', () => {
    spyOn(window, 'open');
    component.ngOnInit();

    component.showGogAuthPopup();

    expect(window.open).toHaveBeenCalledWith(GOG_CONFIG_RESPONSE.userAuthUrl,
      '_blank', 'toolbar=0,location=0,menubar=0');
  });

  it('should authenticate with a valid URL', () => {
    component.gogCodeUrlInput.setValue('https://www.example.com?code=1234');
    gogAuthClientMock.authenticate.and.returnValue({
      subscribe: (callback: (response: RefreshTokenResponse) => any) => {
        expect(component.gogIsLoading).toBeTrue();
        callback({refresh_token: 'someRefreshToken'});
      }
    } as any);

    component.authenticateGog();

    expect(component.gogAuthenticated).toBeTrue();
    expect(component.gogIsLoading).toBeFalse();
    expect(notificationService.showSuccess).toHaveBeenCalledWith('GOG authentication successful');
    expect(notificationService.showFailure).toHaveBeenCalledTimes(0);
    expect(component.gogCodeUrlInput.value).toBeNull();
  });

  it('should not authenticate if refresh token is missing', () => {
    component.gogCodeUrlInput.setValue('https://www.example.com?code=1234');
    gogAuthClientMock.authenticate.and.returnValue({
      subscribe: (callback: (response: RefreshTokenResponse) => any) => {
        expect(component.gogIsLoading).toBeTrue();
        callback({
          refresh_token: undefined
        });
      }
    } as any);

    component.authenticateGog();

    expect(component.gogAuthenticated).toBeFalse();
    expect(component.gogIsLoading).toBeFalse();
    expect(notificationService.showFailure).toHaveBeenCalledWith('Something went wrong during GOG authentication');
  });

  it('should not authenticate given GOG code URL is empty', () => {
    component.gogCodeUrlInput.setValue('');

    component.authenticateGog();

    expect(gogAuthClientMock.authenticate).not.toHaveBeenCalled();
    const expectedErrors = {
      gogCodeUrl: {required: true}
    };
    expect(notificationService.showFailure)
      .toHaveBeenCalledWith("Please check the form for errors and try again.", expectedErrors)
  });

  it('should log out given logged in', async () => {
    makeAuthenticated();
    gogAuthClientMock.logOutOfGog.and.returnValue(of(true) as any);
    const logOutButton: DebugElement = getLogOutButton();

    await logOutButton.nativeElement.click();

    expect(component.gogIsLoading).toBeFalsy();
    expect(component.gogAuthenticated).toBeFalsy();
    expect(notificationService.showSuccess).toHaveBeenCalledWith("Logged out of GOG");
  })

  function makeAuthenticated() {
    component.gogAuthenticated = true;
    fixture.detectChanges();
  }

  function getLogOutButton(): DebugElement {
    return fixture.debugElement.query(By.css('[data-testid="log-out-gog-btn"]'));
  }

  it('should handle error during log out', async () => {
    makeAuthenticated();
    const error = throwErrorDuringLogOut();
    const logOutButton: DebugElement = getLogOutButton();

    await logOutButton.nativeElement.click();

    expect(component.gogIsLoading).toBeFalsy();
    expect(component.gogAuthenticated).toBeTruthy();
    expect(notificationService.showFailure).toHaveBeenCalledWith("Could not log out of GOG", error);
  });

  function throwErrorDuringLogOut() {
    const error = new Error('Log out failed');
    gogAuthClientMock.logOutOfGog.and.returnValue(throwError(() => error));
    return error;
  }
});
