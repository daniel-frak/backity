package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.inmemory;

import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryProgressRepository;
import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryStatusChangedEvent;
import dev.codesoapbox.backity.core.discovery.domain.events.TestGameContentDiscoveryEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class GameContentDiscoveryStatusChangedEventRepositoryHandlerTest {

    private GameContentDiscoveryStatusChangedEventRepositoryHandler handler;

    @Mock
    private GameContentDiscoveryProgressRepository discoveryProgressRepository;

    @BeforeEach
    void setUp() {
        handler = new GameContentDiscoveryStatusChangedEventRepositoryHandler(discoveryProgressRepository);
    }

    @Test
    void shouldGetEventClass() {
        Class<GameContentDiscoveryStatusChangedEvent> result = handler.getEventClass();

        assertThat(result).isEqualTo(GameContentDiscoveryStatusChangedEvent.class);
    }

    @Test
    void shouldHandleEventGivenStatusChangedToStopped() {
        GameContentDiscoveryStatusChangedEvent event = TestGameContentDiscoveryEvent.statusChangedToStopped();

        handler.handle(event);

        verify(discoveryProgressRepository).deleteByGameProviderId(event.gameProviderId());
    }

    @Test
    void shouldHandleEventGivenStatusChangedToInProgress() {
        GameContentDiscoveryStatusChangedEvent event = TestGameContentDiscoveryEvent.statusChangedToInProgress();

        handler.handle(event);

        verifyNoInteractions(discoveryProgressRepository);
    }
}