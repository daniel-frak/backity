package dev.codesoapbox.backity.core.discovery.application.eventhandlers;

import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryProgressChangedEvent;
import dev.codesoapbox.backity.core.discovery.domain.events.TestGameContentDiscoveryEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GameContentDiscoveryProgressChangedEventHandlerTest {

    @Mock
    private GameContentDiscoveryProgressChangedEventExternalForwarder eventForwarder;

    private GameContentDiscoveryProgressChangedEventHandler eventHandler;

    @BeforeEach
    void setUp() {
        eventHandler = new GameContentDiscoveryProgressChangedEventHandler(eventForwarder);
    }

    @Test
    void shouldForwardEvent() {
        GameContentDiscoveryProgressChangedEvent event = TestGameContentDiscoveryEvent.progressChanged();

        eventHandler.handle(event);

        verify(eventForwarder).forward(event);
    }
}