import {ComponentFixture, TestBed} from '@angular/core/testing';
import {GogAuthComponent} from './gog-auth.component';
import {provideHttpClientTesting} from "@angular/common/http/testing";
import {LoadedContentStubComponent} from "@app/shared/components/loaded-content/loaded-content.component.stub";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {GamesClient, GOGAuthenticationClient, RefreshTokenResponse} from "@backend";
import {provideHttpClient, withInterceptorsFromDi} from '@angular/common/http';
import createSpyObj = jasmine.createSpyObj;
import {of} from "rxjs";
import {ButtonComponent} from "@app/shared/components/button/button.component";

describe('GogAuthComponent', () => {
  let component: GogAuthComponent;
  let fixture: ComponentFixture<GogAuthComponent>;
  let gogAuthClientMock: any;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [GogAuthComponent, LoadedContentStubComponent],
      imports: [FormsModule, ReactiveFormsModule, ButtonComponent],
      providers: [
        {
          provide: GOGAuthenticationClient,
          useValue: createSpyObj(GOGAuthenticationClient, ['checkAuthentication', 'authenticate'])
        },
        provideHttpClient(withInterceptorsFromDi()),
        provideHttpClientTesting()
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(GogAuthComponent);
    component = fixture.componentInstance;

    gogAuthClientMock = TestBed.inject(GOGAuthenticationClient);
    gogAuthClientMock.checkAuthentication.and.returnValue(of(false));

    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should check authentication status on init', () => {
    gogAuthClientMock.checkAuthentication.and.returnValue({
      subscribe: (callback: (response: boolean) => any) => {
        expect(component.gogIsLoading).toBeTrue();
        callback(true);
      }
    });

    component.ngOnInit();

    expect(component.gogAuthenticated).toBeTrue();
    expect(component.gogIsLoading).toBeFalse();
  });

  it('should open a new window for authenticating', () => {
    spyOn(window, 'open');

    component.showGogAuthPopup();

    expect(window.open).toHaveBeenCalled();
  });

  it('should authenticate with a valid URL', () => {
    spyOn(console, 'info');
    spyOn(console, 'error');

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
    expect(console.info).toHaveBeenCalledWith('Refresh token: someRefreshToken');
    expect(console.info).toHaveBeenCalledWith('Authentication code: 1234');
    expect(console.error).toHaveBeenCalledTimes(0);
  });

  it('should not authenticate if refresh token is missing', () => {
    spyOn(console, 'info');
    spyOn(console, 'error');

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
    expect(console.info).toHaveBeenCalledWith('Authentication code: 1234');
    expect(console.error).toHaveBeenCalled();
  });

  it('should not authenticate given GOG code URL is empty', () => {
    component.gogAuthForm.get('gogCodeUrl')?.setValue('');

    component.authenticateGog();

    expect(gogAuthClientMock.authenticate).not.toHaveBeenCalled();
  });

  it('should log an error when signOutGog is called', () => {
    spyOn(console, 'error');

    component.signOutGog();

    expect(console.error).toHaveBeenCalled();
  });
});
