package dev.codesoapbox.backity.core.discovery.application.eventhandlers;

import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryStartedEvent;
import dev.codesoapbox.backity.core.discovery.domain.events.TestGameContentDiscoveryEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GameContentDiscoveryStartedEventHandlerTest {

    @Mock
    private GameContentDiscoveryStartedEventExternalForwarder eventForwarder;

    private GameContentDiscoveryStartedEventHandler eventHandler;

    @BeforeEach
    void setUp() {
        eventHandler = new GameContentDiscoveryStartedEventHandler(eventForwarder);
    }

    @Test
    void shouldForwardEvent() {
        GameContentDiscoveryStartedEvent event = TestGameContentDiscoveryEvent.discoveryStarted();

        eventHandler.handle(event);

        verify(eventForwarder).forward(event);
    }
}