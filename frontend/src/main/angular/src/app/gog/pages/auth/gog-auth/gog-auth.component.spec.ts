import {ComponentFixture, TestBed} from '@angular/core/testing';

import {GogAuthComponent} from './gog-auth.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {LoadedContentStubComponent} from "@app/shared/components/loaded-content/loaded-content.component.stub";
import {FormsModule} from "@angular/forms";
import {GOGAuthenticationClient} from "@backend";
import createSpyObj = jasmine.createSpyObj;

describe('GogAuthComponent', () => {
  let component: GogAuthComponent;
  let fixture: ComponentFixture<GogAuthComponent>;
  let gogAuthClientMock: any;

  beforeEach(async () => {
    gogAuthClientMock = createSpyObj(GOGAuthenticationClient, ['check', 'authenticate']);
    gogAuthClientMock.check.and.returnValue({subscribe: (s: (f: any) => any) => s(false)});

    await TestBed.configureTestingModule({
      declarations: [
        GogAuthComponent,
        LoadedContentStubComponent
      ],
      imports: [
        HttpClientTestingModule,
        FormsModule
      ],
      providers: [
        {
          provide: GOGAuthenticationClient,
          useValue: gogAuthClientMock
        }
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GogAuthComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should check authentications status on init', () => {
    expect(component.gogAuthenticated).toBeFalse();

    gogAuthClientMock.check.and.returnValue({
      subscribe: (s: (f: any) => any) => {
        expect(component.gogIsLoading).toBeTrue();
        s(true);
      }
    });

    component.ngOnInit();

    expect(component.gogAuthenticated).toBeTrue();
    expect(component.gogIsLoading).toBeFalse();
  });

  it('should open new window for authenticating', () => {
    spyOn(window, 'open');

    component.showGogAuthPopup();

    expect(window.open).toHaveBeenCalled();
  });

  it('should authenticate', () => {
    spyOn(console, 'info');
    spyOn(console, 'warn');
    spyOn(console, 'error');
    component.gogCodeUrl = 'https://www.example.com?code=1234';

    gogAuthClientMock.authenticate.and.returnValue({
      subscribe: (s: (f: any) => any) => {
        expect(component.gogIsLoading).toBeTrue();
        s({refresh_token: 'someRefreshToken'});
      }
    });

    component.authenticateGog();
    expect(component.gogAuthenticated).toBeTrue();
    expect(component.gogIsLoading).toBeFalse();
    expect(console.info).toHaveBeenCalledWith('Refresh token: someRefreshToken');
    expect(console.info).toHaveBeenCalledWith('Authentication code: 1234');
    expect(console.error).toHaveBeenCalledTimes(0);
  });

  it('should not authenticate if refresh token is missing from response', () => {
    spyOn(console, 'info');
    spyOn(console, 'warn');
    spyOn(console, 'error');
    component.gogCodeUrl = 'https://www.example.com?code=1234';

    gogAuthClientMock.authenticate.and.returnValue({
      subscribe: (s: (f: any) => any) => {
        expect(component.gogIsLoading).toBeTrue();
        s({});
      }
    });

    component.authenticateGog();
    expect(component.gogAuthenticated).toBeFalse();
    expect(component.gogIsLoading).toBeFalse();
    expect(console.info).toHaveBeenCalledWith('Authentication code: 1234');
    expect(console.error).toHaveBeenCalled();
  });

  it('should log an error when signOutGog is called', () => {
    spyOn(console, 'error');
    component.signOutGog();
    expect(console.error).toHaveBeenCalled();
  });
});
