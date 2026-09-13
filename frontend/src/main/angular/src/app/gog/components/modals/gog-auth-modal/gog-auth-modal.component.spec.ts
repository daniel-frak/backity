import {ComponentFixture, TestBed} from '@angular/core/testing';

import {GogAuthModalComponent} from './gog-auth-modal.component';
import {GOGAuthenticationClient} from "@backend";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {of, tap, throwError} from "rxjs";
import {Mocked} from "vitest";

const USER_AUTH_URL = "someGogAuthUrl";

const GOG_CONFIG_RESPONSE = {
    userAuthUrl: USER_AUTH_URL
};

describe('GogAuthModalComponent', () => {
    let component: GogAuthModalComponent;
    let fixture: ComponentFixture<GogAuthModalComponent>;

    let gogAuthClientMock: Mocked<GOGAuthenticationClient>;
    let notificationService: NotificationService;
    let ngbActiveModalSpy: Mocked<NgbActiveModal>;

    beforeEach(async () => {
        const modalMock = {
            close: vi.fn(),
            dismiss: vi.fn()
        };

        await TestBed.configureTestingModule({
            imports: [GogAuthModalComponent],
            providers: [
                { provide: NgbActiveModal, useValue: modalMock },
                {
                    provide: GOGAuthenticationClient,
                    useValue: {
                        authenticateGog: vi.fn()
                    }
                },
                {
                    provide: NotificationService,
                    useValue: {
                        showSuccess: vi.fn(),
                        showFailure: vi.fn()
                    }
                },
            ]
        })
            .compileComponents();

        fixture = TestBed.createComponent(GogAuthModalComponent);
        component = fixture.componentInstance;

        ngbActiveModalSpy = TestBed.inject(NgbActiveModal) as any;
        gogAuthClientMock = TestBed.inject(GOGAuthenticationClient) as any;
        notificationService = TestBed.inject(NotificationService);

        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should open a new window for authenticating', () => {
        vi.spyOn(window, 'open').mockReturnValue(null);
        component.gogAuthUrl = USER_AUTH_URL;
        component.showGogAuthPopup();

        expect(window.open).toHaveBeenCalledWith(GOG_CONFIG_RESPONSE.userAuthUrl, '_blank', 'toolbar=0,location=0,menubar=0');
    });

    it('should authenticate with a valid URL', () => {
        component.gogCodeUrlInput.setValue('https://www.example.com?code=1234');
        gogAuthClientMock.authenticateGog.mockReturnValue(of({ refresh_token: 'someRefreshToken' }).pipe(tap(() => {
            expect(component.isLoading).toBe(true);
        })) as any);

        component.authenticateGog();

        expect(ngbActiveModalSpy.close).toHaveBeenCalledWith(true);
        expect(component.isLoading).toBe(false);
        expect(notificationService.showSuccess).toHaveBeenCalledWith('GOG authentication successful');
        expect(notificationService.showFailure).toHaveBeenCalledTimes(0);
        expect(component.gogCodeUrlInput.value).toBe('');
    });

    it('should not authenticate if refresh token is missing', () => {
        component.gogCodeUrlInput.setValue('https://www.example.com?code=1234');
        gogAuthClientMock.authenticateGog.mockReturnValue(of({ refresh_token: undefined }).pipe(tap(() => {
            expect(component.isLoading).toBe(true);
        })) as any);

        component.authenticateGog();

        expect(ngbActiveModalSpy.close).not.toHaveBeenCalled();
        expect(component.isLoading).toBe(false);
        expect(notificationService.showFailure)
            .toHaveBeenCalledWith('Something went wrong during GOG authentication');
    });

    it('should not authenticate given GOG code URL is empty', () => {
        component.gogCodeUrlInput.setValue('');

        component.authenticateGog();

        expect(ngbActiveModalSpy.close).not.toHaveBeenCalled();
        expect(gogAuthClientMock.authenticateGog).not.toHaveBeenCalled();
        const expectedErrors = {
            gogCodeUrl: { required: true }
        };
        expect(notificationService.showFailure)
            .toHaveBeenCalledWith("Please check the form for errors and try again.", expectedErrors);
    });

    it('should not authenticate given response returns error', () => {
        component.gogCodeUrlInput.setValue('https://www.example.com?code=1234');
        gogAuthClientMock.authenticateGog.mockReturnValue(throwError(() => new Error('Authentication failed')));

        component.authenticateGog();

        expect(ngbActiveModalSpy.close).not.toHaveBeenCalled();
        expect(component.isLoading).toBe(false);
        expect(notificationService.showFailure)
            .toHaveBeenCalledWith("Something went wrong during GOG authentication");
    });

    it('should not authenticate given authentication throws', () => {
        component.gogCodeUrlInput.setValue('invalidUrl'); // Will throw during new URL construction

        component.authenticateGog();

        expect(ngbActiveModalSpy.close).not.toHaveBeenCalled();
        expect(component.isLoading).toBe(false);
        expect(notificationService.showFailure)
            .toHaveBeenCalledWith("Something went wrong during GOG authentication", expect.any(Error));
    });

    it('authenticateGog should throw given code is missing from URL', () => {
        component.gogCodeUrlInput.setValue('https://www.missingcodeparam.com');

        component.authenticateGog();

        expect(ngbActiveModalSpy.close).not.toHaveBeenCalled();
        expect(component.isLoading).toBe(false);
        expect(notificationService.showFailure)
            .toHaveBeenCalledWith("Invalid URL: missing 'code' parameter");
    });
});
