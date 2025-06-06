package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.inmemory;

import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryProgress;
import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryProgressRepository;
import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryProgressChangedEvent;
import dev.codesoapbox.backity.core.discovery.domain.events.TestGameContentDiscoveryEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GameContentDiscoveryProgressChangedEventRepositoryHandlerTest {

    private GameContentDiscoveryProgressChangedEventRepositoryHandler handler;

    @Mock
    private GameContentDiscoveryProgressRepository discoveryProgressRepository;

    @BeforeEach
    void setUp() {
        handler = new GameContentDiscoveryProgressChangedEventRepositoryHandler(discoveryProgressRepository);
    }

    @Test
    void shouldGetEventClass() {
        Class<GameContentDiscoveryProgressChangedEvent> result = handler.getEventClass();

        assertThat(result).isEqualTo(GameContentDiscoveryProgressChangedEvent.class);
    }

    @Test
    void shouldHandle() {
        GameContentDiscoveryProgressChangedEvent event = TestGameContentDiscoveryEvent.progressChanged();

        handler.handle(event);

        var expectedReplicationProgress = new GameContentDiscoveryProgress(
                event.gameProviderId(), event.percentage(), event.timeLeft());
        verify(discoveryProgressRepository).save(expectedReplicationProgress);
    }
}