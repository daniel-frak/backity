import {ComponentFixture, TestBed} from '@angular/core/testing';
import {GogAuthComponent} from './gog-auth.component';
import {GOGAuthenticationClient, RefreshTokenResponse} from "@backend";
import {defer, of, throwError} from "rxjs";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {By} from '@angular/platform-browser';
import createSpyObj = jasmine.createSpyObj;
import {DebugElement} from "@angular/core";

describe('GogAuthComponent', () => {
  let component: GogAuthComponent;
  let fixture: ComponentFixture<GogAuthComponent>;

  let gogAuthClientMock: any;
  let notificationService: NotificationService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GogAuthComponent],
      providers: [
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

    gogAuthClientMock = TestBed.inject(GOGAuthenticationClient);
    gogAuthClientMock.checkAuthentication.and.returnValue(of(false));
    notificationService = TestBed.inject(NotificationService);

    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should check authentication status on init', () => {
    gogAuthClientMock.checkAuthentication.and.returnValue(defer(() => {
      expect(component.gogIsLoading).toBeTrue();
      return of(true);
    }));

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
      'Failed to check GOG authentication', error);
  });

  it('should open a new window for authenticating', () => {
    spyOn(window, 'open');

    component.showGogAuthPopup();

    expect(window.open).toHaveBeenCalled();
  });

  it('should authenticate with a valid URL', () => {
    component.gogCodeUrlInput.setValue('https://www.example.com?code=1234');
    gogAuthClientMock.authenticate.and.returnValue({
      subscribe: (callback: (response: RefreshTokenResponse) => any) => {
        expect(component.gogIsLoading).toBeTrue();
        callback({refresh_token: 'someRefreshToken'});
      }
    });

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
    });

    component.authenticateGog();

    expect(component.gogAuthenticated).toBeFalse();
    expect(component.gogIsLoading).toBeFalse();
    expect(notificationService.showFailure).toHaveBeenCalledWith('Something went wrong during GOG authentication');
  });

  it('should not authenticate given GOG code URL is empty', () => {
    component.gogCodeUrlInput.setValue('');

    component.authenticateGog();

    expect(gogAuthClientMock.authenticate).not.toHaveBeenCalled();
    const expectedErrors = {required: true};
    expect(notificationService.showFailure).toHaveBeenCalledWith("Form is invalid", expectedErrors)
  });

  it('should log out given logged in', async () => {
    makeAuthenticated();
    gogAuthClientMock.logOutOfGog.and.returnValue(of(true));
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
