import {ComponentFixture, TestBed} from '@angular/core/testing';
import {GogAuthComponent} from './gog-auth.component';
import {GOGAuthenticationClient, GOGConfigurationClient} from "@backend";
import {defer, of, throwError} from "rxjs";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {By} from '@angular/platform-browser';
import {DebugElement} from "@angular/core";
import {NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {GogAuthModalComponent} from "@app/gog/components/modals/gog-auth-modal/gog-auth-modal.component";
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
  let modalService: SpyObj<NgbModal>;

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
          useValue: createSpyObj(GOGAuthenticationClient, ['checkAuthentication', 'logOutOfGog'])
        },
        {
          provide: NotificationService,
          useValue: createSpyObj('NotificationService', ['showSuccess', 'showFailure'])
        },
        {provide: NgbModal, useValue: createSpyObj('NgbModal', ['open'])}
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(GogAuthComponent);
    component = fixture.componentInstance;

    gogConfigClientMock = TestBed.inject(GOGConfigurationClient) as SpyObj<GOGConfigurationClient>;
    gogAuthClientMock = TestBed.inject(GOGAuthenticationClient) as SpyObj<GOGAuthenticationClient>;
    notificationService = TestBed.inject(NotificationService);
    modalService = TestBed.inject(NgbModal) as SpyObj<NgbModal>;

    gogAuthClientMock.checkAuthentication.and.returnValue(of(false) as any);
    gogConfigClientMock.getGogConfig.and.returnValue(of(GOG_CONFIG_RESPONSE) as any);

    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
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

  it('should open authentication modal when user clicks on "Authenticate with GOG"', async () => {
    const mockModalRef = mockAuthModalImmediatelyReturnsTrue();

    modalService.open.and.returnValue(mockModalRef as any);

    const authenticateButton: DebugElement = getAuthenticateButton();
    await authenticateButton.nativeElement.click();

    expect(modalService.open).toHaveBeenCalledWith(GogAuthModalComponent);
  });

  function mockAuthModalImmediatelyReturnsTrue(): NgbModalRef {
    const mockModalRef = {
      componentInstance: {},
      result: Promise.resolve(true) // Simulates modal closing behavior
    } as NgbModalRef;
    modalService.open.and.returnValue(mockModalRef as any);
    return mockModalRef;
  }

  function getAuthenticateButton(): DebugElement {
    return fixture.debugElement.query(By.css('[data-testid="show-gog-auth-modal-btn"]'));
  }

  it('should not open second authentication modal when one is already open', async () => {
    mockAuthModalNeverCloses();

    const authenticateButton: DebugElement = getAuthenticateButton();
    await authenticateButton.nativeElement.click(); // First modal opens
    await authenticateButton.nativeElement.click(); // Second modal opens

    expect(modalService.open).toHaveBeenCalledTimes(1);
  });

  function mockAuthModalNeverCloses() {
    const mockModalRef = {
      componentInstance: {},
      result: new Promise(() => {
      }) // Never closes
    } as NgbModalRef;
    modalService.open.and.returnValue(mockModalRef as any);
  }

  it('should open authentication modal after authentication succeeded', async () => {
    mockAuthModalImmediatelyReturnsTrue();

    const authenticateButton: DebugElement = getAuthenticateButton();
    await authenticateButton.nativeElement.click(); // First modal opens and succeeds
    await authenticateButton.nativeElement.click(); // Second modal opens and succeeds

    expect(modalService.open).toHaveBeenCalledTimes(2);
  });

  it('should open authentication modal after authentication failed', async () => {
    mockAuthModalImmediatelyRejects();

    const authenticateButton: DebugElement = getAuthenticateButton();
    await authenticateButton.nativeElement.click(); // First modal opens and fails
    await authenticateButton.nativeElement.click(); // Second modal opens and fails

    expect(modalService.open).toHaveBeenCalledTimes(2);
  });

  function mockAuthModalImmediatelyRejects() {
    const mockModalRef = {
      componentInstance: {},
      result: Promise.reject("some rejection reason") // Simulates modal closing behavior
    } as NgbModalRef;
    modalService.open.and.returnValue(mockModalRef as any);
  }

  it('should log out given logged in', async () => {
    makeAuthenticated();
    gogAuthClientMock.logOutOfGog.and.returnValue(of(true) as any);
    const logOutButton: DebugElement = getLogOutButton();

    await logOutButton.nativeElement.click();

    expect(component.gogIsLoading).toBeFalsy();
    expect(component.gogAuthenticated).toBeFalsy();
    expect(notificationService.showSuccess).toHaveBeenCalledWith("Logged out of GOG");
  });

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
