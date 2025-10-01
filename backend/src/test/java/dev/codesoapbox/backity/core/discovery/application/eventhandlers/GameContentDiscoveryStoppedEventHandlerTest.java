package dev.codesoapbox.backity.core.discovery.application.eventhandlers;

import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryStoppedEvent;
import dev.codesoapbox.backity.core.discovery.domain.events.TestGameContentDiscoveryEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GameContentDiscoveryStoppedEventHandlerTest {

    @Mock
    private GameContentDiscoveryStoppedEventExternalForwarder eventForwarder;

    private GameContentDiscoveryStoppedEventHandler eventHandler;

    @BeforeEach
    void setUp() {
        eventHandler = new GameContentDiscoveryStoppedEventHandler(eventForwarder);
    }

    @Test
    void shouldForwardEvent() {
        GameContentDiscoveryStoppedEvent event = TestGameContentDiscoveryEvent.discoveryStopped();

        eventHandler.handle(event);

        verify(eventForwarder).forward(event);
    }
}