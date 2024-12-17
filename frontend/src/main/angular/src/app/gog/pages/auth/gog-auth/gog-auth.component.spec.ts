import {ComponentFixture, TestBed} from '@angular/core/testing';
import {GogAuthComponent} from './gog-auth.component';
import {provideHttpClientTesting} from "@angular/common/http/testing";
import {LoadedContentStubComponent} from "@app/shared/components/loaded-content/loaded-content.component.stub";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {GOGAuthenticationClient, RefreshTokenResponse} from "@backend";
import {provideHttpClient, withInterceptorsFromDi} from '@angular/common/http';
import {defer, of, throwError} from "rxjs";
import {ButtonComponent} from "@app/shared/components/button/button.component";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import createSpyObj = jasmine.createSpyObj;

describe('GogAuthComponent', () => {
  let component: GogAuthComponent;
  let fixture: ComponentFixture<GogAuthComponent>;

  let gogAuthClientMock: any;
  let notificationService: NotificationService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        FormsModule,
        ReactiveFormsModule,
        ButtonComponent,
        GogAuthComponent,
        LoadedContentStubComponent
      ],
      providers: [
        {
          provide: GOGAuthenticationClient,
          useValue: createSpyObj(GOGAuthenticationClient, ['checkAuthentication', 'authenticate'])
        },
        {
          provide: NotificationService,
          useValue: createSpyObj('NotificationService', ['showSuccess', 'showFailure'])
        },
        provideHttpClient(withInterceptorsFromDi()),
        provideHttpClientTesting()
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
    component.gogAuthForm.get('gogCodeUrl')?.setValue('https://www.example.com?code=1234');
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
  });

  it('should not authenticate if refresh token is missing', () => {
    component.gogAuthForm.get('gogCodeUrl')?.setValue('https://www.example.com?code=1234');
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
    component.gogAuthForm.get('gogCodeUrl')?.setValue('');

    component.authenticateGog();

    expect(gogAuthClientMock.authenticate).not.toHaveBeenCalled();
    let expectedErrors = {required: true};
    expect(notificationService.showFailure).toHaveBeenCalledWith("Form is invalid", expectedErrors)
  });

  it('should log an error when signOutGog is called', () => {
    component.signOutGog();

    expect(notificationService.showFailure).toHaveBeenCalledWith('Not yet implemented');
  });
});
