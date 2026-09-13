import {ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';

import {GameProvidersComponent} from './game-providers.component';
import {GogAuthComponentStub} from "@app/gog/pages/auth/gog-auth/gog-auth.component.stub";
import {GogAuthComponent} from "@app/gog/pages/auth/gog-auth/gog-auth.component";
import {
  GameContentDiscoveryClient,
  GameContentDiscoveryOverview,
  GameContentDiscoveryProgressChangedEvent,
  GameContentDiscoveryStartedEvent,
  GameContentDiscoveryStoppedEvent,
  GameContentDiscoveryWebSocketTopics
} from "@backend";
import {NotificationService} from "@app/shared/services/notification/notification.service";
import {MessageService} from "@app/shared/backend/services/message.service";
import {of, throwError} from "rxjs";
import {MessageSimulator} from "@app/shared/testing/message-simulator";
import {TestGameContentDiscoveryOverview} from "@app/shared/testing/objects/test-game-content-discovery-overview";
import {HttpResponse} from "@angular/common/http";
import {
  TestGameContentDiscoveryProgressChangedEvent
} from "@app/shared/testing/objects/test-game-content-discovery-progress-changed-event";
import {
  TestGameContentDiscoveryStartedEvent
} from "@app/shared/testing/objects/test-game-content-discovery-started-event";
import {
  TestGameContentDiscoveryStoppedEvent
} from "@app/shared/testing/objects/test-game-content-discovery-stopped-event";
import {AutoLayoutComponent} from "@app/shared/components/auto-layout/auto-layout.component";
import {AutoLayoutStubComponent} from "@app/shared/components/auto-layout/auto-layout.stub.component";
import {By} from "@angular/platform-browser";
import {Mocked} from "vitest";

describe('GameProvidersComponent', () => {
    let component: GameProvidersComponent;
    let fixture: ComponentFixture<GameProvidersComponent>;

    let messageSimulator: MessageSimulator;
    let gameContentDiscoveryClient: Mocked<GameContentDiscoveryClient>;
    let notificationService: Mocked<NotificationService>;
    let messageService: Mocked<MessageService>;

    beforeEach(async () => {
        messageService = { watch: vi.fn() } as unknown as Mocked<MessageService>;
        gameContentDiscoveryClient = {
            getGameContentDiscoveryOverviews: vi.fn(),
            startGameContentDiscovery: vi.fn(),
            stopGameContentDiscovery: vi.fn()
        } as unknown as Mocked<GameContentDiscoveryClient>;
        notificationService = {
            showSuccess: vi.fn(),
            showFailure: vi.fn()
        } as unknown as Mocked<NotificationService>;

        await TestBed.configureTestingModule({
            imports: [GameProvidersComponent],
            providers: [
                {
                    provide: MessageService,
                    useValue: messageService
                },
                {
                    provide: GameContentDiscoveryClient,
                    useValue: gameContentDiscoveryClient
                },
                {
                    provide: NotificationService,
                    useValue: notificationService
                }
            ]
        })
            .overrideComponent(GameProvidersComponent, {
            remove: { imports: [GogAuthComponent, AutoLayoutComponent] },
            add: { imports: [GogAuthComponentStub, AutoLayoutStubComponent] }
        })
            .compileComponents();
    });

    beforeEach(() => {
        messageSimulator = MessageSimulator.given(messageService);
    });

    it('should create', () => {
        gameContentDiscoveryClient.getGameContentDiscoveryOverviews.mockReturnValue(of([]) as any);
        fixture = TestBed.createComponent(GameProvidersComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
        expect(component).toBeTruthy();
    });

    it('should refresh info on init', fakeAsync(() => {
        const newOverview: GameContentDiscoveryOverview = TestGameContentDiscoveryOverview.inProgress();
        gameContentDiscoveryClient.getGameContentDiscoveryOverviews.mockReturnValue(of([newOverview]) as any);

        fixture = TestBed.createComponent(GameProvidersComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
        tick();
        fixture.detectChanges();

        expect(component.discoveryIsInProgressByGameProviderId().get('someGameProviderId')).toBe(true);
        expect(component.discoveryOverviewsByGameProviderId().get('someGameProviderId')).toEqual(newOverview);
        expect(component.discoveryStatusUnknownByGameProviderId().get('someGameProviderId')).toBe(false);
    }));

    it('should subscribe to discovery started events', () => {
        gameContentDiscoveryClient.getGameContentDiscoveryOverviews.mockReturnValue(of([]) as any);
        fixture = TestBed.createComponent(GameProvidersComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
        expect(messageService.watch)
            .toHaveBeenCalledWith(GameContentDiscoveryWebSocketTopics.TopicGameContentDiscoveryDiscoveryStarted);
    });

    it('should subscribe to discovery stopped events', () => {
        gameContentDiscoveryClient.getGameContentDiscoveryOverviews.mockReturnValue(of([]) as any);
        fixture = TestBed.createComponent(GameProvidersComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
        expect(messageService.watch)
            .toHaveBeenCalledWith(GameContentDiscoveryWebSocketTopics.TopicGameContentDiscoveryDiscoveryStopped);
    });

    it('should subscribe to discovery progress updates', () => {
        gameContentDiscoveryClient.getGameContentDiscoveryOverviews.mockReturnValue(of([]) as any);
        fixture = TestBed.createComponent(GameProvidersComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
        expect(messageService.watch)
            .toHaveBeenCalledWith(GameContentDiscoveryWebSocketTopics.TopicGameContentDiscoveryProgressUpdate);
    });

    it('should update discovery status given discovery started event received', fakeAsync(() => {
        gameContentDiscoveryClient.getGameContentDiscoveryOverviews.mockReturnValue(of([]) as any);
        fixture = TestBed.createComponent(GameProvidersComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
        const event: GameContentDiscoveryStartedEvent = TestGameContentDiscoveryStartedEvent.any();

        emitDiscoveryStarted(event);
        tick();

        expect(component.discoveryIsInProgressByGameProviderId().get(event.gameProviderId)).toBe(true);
        expect(component.discoveryStatusUnknownByGameProviderId().get(event.gameProviderId)).toBe(false);
    }));

    function emitDiscoveryStarted(event: GameContentDiscoveryStartedEvent) {
        messageSimulator.emit(GameContentDiscoveryWebSocketTopics.TopicGameContentDiscoveryDiscoveryStarted, event);
    }

    it('should not refresh info when already loading', () => {
        gameContentDiscoveryClient.getGameContentDiscoveryOverviews.mockReturnValue(of([]) as any);
        fixture = TestBed.createComponent(GameProvidersComponent);
        component = fixture.componentInstance;
        component.infoIsLoading.set(true);
        gameContentDiscoveryClient.getGameContentDiscoveryOverviews.mockClear();

        fixture.detectChanges(); // Triggers refreshInfo() on init

        expect(gameContentDiscoveryClient.getGameContentDiscoveryOverviews).not.toHaveBeenCalled();
    });

    it('should update overview given discovery started event received and overview exists', fakeAsync(() => {
        gameContentDiscoveryClient.getGameContentDiscoveryOverviews.mockReturnValue(of([]) as any);
        fixture = TestBed.createComponent(GameProvidersComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
        const event: GameContentDiscoveryStartedEvent = TestGameContentDiscoveryStartedEvent.any();
        component.discoveryOverviewsByGameProviderId.update(map => new Map(map).set(event.gameProviderId, TestGameContentDiscoveryOverview.notInProgress()));

        emitDiscoveryStarted(event);
        tick();

        expect(component.discoveryOverviewsByGameProviderId().get(event.gameProviderId)?.isInProgress).toBe(true);
    }));

    it('should update overview given discovery started event received and overview is undefined', fakeAsync(() => {
        gameContentDiscoveryClient.getGameContentDiscoveryOverviews.mockReturnValue(of([]) as any);
        fixture = TestBed.createComponent(GameProvidersComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
        const event: GameContentDiscoveryStartedEvent = TestGameContentDiscoveryStartedEvent.any();
        component.discoveryOverviewsByGameProviderId.update(map => {
            const newMap = new Map(map);
            newMap.delete(event.gameProviderId);
            return newMap;
        });

        emitDiscoveryStarted(event);
        tick();

        expect(component.discoveryOverviewsByGameProviderId().get(event.gameProviderId)?.isInProgress).toBe(true);
    }));

    it('should update discovery status given discovery stopped event received', fakeAsync(() => {
        gameContentDiscoveryClient.getGameContentDiscoveryOverviews.mockReturnValue(of([]) as any);
        fixture = TestBed.createComponent(GameProvidersComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
        const event: GameContentDiscoveryStoppedEvent = TestGameContentDiscoveryStoppedEvent.successfulSubsequent();
        component.discoveryIsInProgressByGameProviderId.update(map => new Map(map).set(event.gameProviderId, true));

        emitDiscoveryStopped(event);
        tick();

        expect(component.discoveryIsInProgressByGameProviderId().get(event.gameProviderId)).toBe(false);
        expect(component.discoveryStatusUnknownByGameProviderId().get(event.gameProviderId)).toBe(false);
    }));

    function emitDiscoveryStopped(event: GameContentDiscoveryStoppedEvent) {
        messageSimulator.emit(GameContentDiscoveryWebSocketTopics.TopicGameContentDiscoveryDiscoveryStopped, event);
    }

    it('should update overview given discovery stopped event received and overview exists', fakeAsync(() => {
        gameContentDiscoveryClient.getGameContentDiscoveryOverviews.mockReturnValue(of([]) as any);
        fixture = TestBed.createComponent(GameProvidersComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
        const event: GameContentDiscoveryStoppedEvent = TestGameContentDiscoveryStoppedEvent.successfulSubsequent();
        component.discoveryOverviewsByGameProviderId.update(map => new Map(map).set(event.gameProviderId, TestGameContentDiscoveryOverview.inProgress()));

        emitDiscoveryStopped(event);
        tick();

        const expectedOverview: GameContentDiscoveryOverview = TestGameContentDiscoveryOverview.notInProgressAfterSuccessfulSubsequent();
        expect(component.discoveryOverviewsByGameProviderId().get(event.gameProviderId)).toEqual(expectedOverview);
        expect(component.discoveryStatusUnknownByGameProviderId().get(event.gameProviderId)).toBe(false);
    }));

    it('should update overview given discovery stopped event received and overview is undefined', fakeAsync(() => {
        gameContentDiscoveryClient.getGameContentDiscoveryOverviews.mockReturnValue(of([]) as any);
        fixture = TestBed.createComponent(GameProvidersComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
        const event: GameContentDiscoveryStoppedEvent = TestGameContentDiscoveryStoppedEvent.successfulSubsequent();
        component.discoveryOverviewsByGameProviderId.update(map => {
            const newMap = new Map(map);
            newMap.delete(event.gameProviderId);
            return newMap;
        });

        emitDiscoveryStopped(event);
        tick();

        const expectedOverview: GameContentDiscoveryOverview = TestGameContentDiscoveryOverview.notInProgressAfterSuccessfulSubsequent();
        expect(component.discoveryOverviewsByGameProviderId().get(event.gameProviderId)).toEqual(expectedOverview);
        expect(component.discoveryStatusUnknownByGameProviderId().get(event.gameProviderId)).toBe(false);
    }));

    async function clickButtonByTestId(testId: string) {
        const button = fixture.debugElement.query(By.css('[data-testid="' + testId + '"]'));
        button.triggerEventHandler('click');
        await fixture.whenStable();
        fixture.detectChanges();
    }

    it('should start game content discovery', async () => {
        gameContentDiscoveryClient.getGameContentDiscoveryOverviews.mockReturnValue(of([]) as any);
        fixture = TestBed.createComponent(GameProvidersComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
        const fakeObservable = of(new HttpResponse());
        gameContentDiscoveryClient.startGameContentDiscovery.mockReturnValue(fakeObservable);
        component.discoveryStatusUnknownByGameProviderId.update(map => new Map(map).set('someGameProviderId', false));

        await clickButtonByTestId('start-game-content-discovery-btn');

        expect(gameContentDiscoveryClient.startGameContentDiscovery).toHaveBeenCalled();
        expect(component.discoveryStatusUnknownByGameProviderId().get('someGameProviderId')).toBe(true);
    });

    it('should log an error when game content discovery cannot be started', async () => {
        gameContentDiscoveryClient.getGameContentDiscoveryOverviews.mockReturnValue(of([]) as any);
        fixture = TestBed.createComponent(GameProvidersComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
        const mockError = new Error('Discovery failed');

        gameContentDiscoveryClient.startGameContentDiscovery.mockReturnValue(throwError(() => mockError));

        await component.startGameContentDiscovery();

        expect(notificationService.showFailure).toHaveBeenCalledWith('Error starting discovery', mockError);
    });

    it('should stop game content discovery', async () => {
        gameContentDiscoveryClient.getGameContentDiscoveryOverviews.mockReturnValue(of([]) as any);
        fixture = TestBed.createComponent(GameProvidersComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
        const fakeObservable = of(new HttpResponse());
        gameContentDiscoveryClient.stopGameContentDiscovery.mockReturnValue(fakeObservable);
        component.discoveryStatusUnknownByGameProviderId.update(map => new Map(map).set('someGameProviderId', false));

        await clickButtonByTestId('stop-game-content-discovery-btn');

        expect(gameContentDiscoveryClient.stopGameContentDiscovery).toHaveBeenCalled();
        expect(component.discoveryStatusUnknownByGameProviderId().get('someGameProviderId')).toBe(true);
    });

    it('should log an error when game content discovery cannot be stopped', async () => {
        gameContentDiscoveryClient.getGameContentDiscoveryOverviews.mockReturnValue(of([]) as any);
        fixture = TestBed.createComponent(GameProvidersComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
        const mockError = new Error('Discovery failed');

        gameContentDiscoveryClient.stopGameContentDiscovery.mockReturnValue(throwError(() => mockError));

        await component.stopGameContentDiscovery();

        expect(notificationService.showFailure).toHaveBeenCalledWith('Error stopping discovery', mockError);
    });

    it('should update discovery progress given event received and overview is defined', () => {
        gameContentDiscoveryClient.getGameContentDiscoveryOverviews.mockReturnValue(of([]) as any);
        fixture = TestBed.createComponent(GameProvidersComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
        const event: GameContentDiscoveryProgressChangedEvent = TestGameContentDiscoveryProgressChangedEvent.twentyFivePercent();
        component.discoveryOverviewsByGameProviderId.update(map => new Map(map).set(event.gameProviderId, {
            gameProviderId: event.gameProviderId,
            isInProgress: true
        }));

        emitProgressUpdate(event);

        expect(component.discoveryOverviewsByGameProviderId().get(event.gameProviderId!))
            .toEqual(TestGameContentDiscoveryOverview.inProgressAtTwentyFivePercent());
        expect(component.discoveryStatusUnknownByGameProviderId().get(event.gameProviderId)).toBe(false);
    });

    it('should update discovery progress given event received and overview is undefined', () => {
        gameContentDiscoveryClient.getGameContentDiscoveryOverviews.mockReturnValue(of([]) as any);
        fixture = TestBed.createComponent(GameProvidersComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
        const event: GameContentDiscoveryProgressChangedEvent = TestGameContentDiscoveryProgressChangedEvent.twentyFivePercent();
        component.discoveryOverviewsByGameProviderId.update(map => {
            const newMap = new Map(map);
            newMap.delete(event.gameProviderId!);
            return newMap;
        });

        emitProgressUpdate(event);

        expect(component.discoveryOverviewsByGameProviderId().get(event.gameProviderId!))
            .toEqual(TestGameContentDiscoveryOverview.inProgressAtTwentyFivePercent());
        expect(component.discoveryStatusUnknownByGameProviderId().get(event.gameProviderId!)).toBe(false);
        expect(component.discoveryIsInProgressByGameProviderId().get(event.gameProviderId!)).toBe(true);
    });

    it('should show error when discovery overviews fetch fails in refreshInfo', fakeAsync(() => {
        const mockError = new Error('fetch failed');
        gameContentDiscoveryClient.getGameContentDiscoveryOverviews.mockReturnValue(throwError(() => mockError) as any);

        fixture = TestBed.createComponent(GameProvidersComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
        tick();

        expect(notificationService.showFailure)
            .toHaveBeenCalledWith('Error fetching discovery overviews', mockError);
        expect(component.infoIsLoading()).toBe(false);
    }));

    it('should block refreshInfo when already loading', () => {
        gameContentDiscoveryClient.getGameContentDiscoveryOverviews.mockReturnValue(of([]) as any);
        fixture = TestBed.createComponent(GameProvidersComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();

        gameContentDiscoveryClient.getGameContentDiscoveryOverviews.mockClear();
        component.infoIsLoading.set(true);

        // Triggers refreshInfo
        void component.stopGameContentDiscovery();

        expect(gameContentDiscoveryClient.getGameContentDiscoveryOverviews).not.toHaveBeenCalled();
    });

    function emitProgressUpdate(event: GameContentDiscoveryProgressChangedEvent) {
        messageSimulator.emit(GameContentDiscoveryWebSocketTopics.TopicGameContentDiscoveryProgressUpdate, event);
    }

    it('should return undefined from getOverview when game provider not found', () => {
        gameContentDiscoveryClient.getGameContentDiscoveryOverviews.mockReturnValue(of([]) as any);
        fixture = TestBed.createComponent(GameProvidersComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
        const gameProviderId = 'someGameProvider';
        expect(component.getOverview(gameProviderId)).toEqual(undefined);
    });

    it('should get overview for specific game provider', () => {
        gameContentDiscoveryClient.getGameContentDiscoveryOverviews.mockReturnValue(of([]) as any);
        fixture = TestBed.createComponent(GameProvidersComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
        const expectedOverview: GameContentDiscoveryOverview = TestGameContentDiscoveryOverview.notInProgressAfterSuccessfulSubsequent();
        component.discoveryOverviewsByGameProviderId.update(map => new Map(map).set(expectedOverview.gameProviderId, expectedOverview));
        expect(component.getOverview(expectedOverview.gameProviderId)).toEqual(expectedOverview);
    });

    it('should return if discovery is ongoing', () => {
        gameContentDiscoveryClient.getGameContentDiscoveryOverviews.mockReturnValue(of([]) as any);
        fixture = TestBed.createComponent(GameProvidersComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();

        expect(component.discoveryOngoing()).toBe(false);

        component.discoveryIsInProgressByGameProviderId.update(map => new Map(map).set('someGameProviderId', false));
        expect(component.discoveryOngoing()).toBe(false);

        component.discoveryIsInProgressByGameProviderId.update(map => new Map(map).set('someGameProviderId', true));
        expect(component.discoveryOngoing()).toBe(true);
    });
});
