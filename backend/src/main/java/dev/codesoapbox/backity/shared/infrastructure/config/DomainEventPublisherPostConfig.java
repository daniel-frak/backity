package dev.codesoapbox.backity.shared.infrastructure.config;

import dev.codesoapbox.backity.shared.domain.DomainEventHandler;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.DomainEventWebSocketPublisher;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@AutoConfigureAfter(DomainEventPublisherBeanConfig.class)
public class DomainEventPublisherPostConfig {

    private final DomainEventWebSocketPublisher domainEventWebSocketPublisher;
    private final List<DomainEventHandler<?>> domainEventHandlers;

    public DomainEventPublisherPostConfig(DomainEventWebSocketPublisher domainEventWebSocketPublisher,
                                          @Autowired(required = false) // Some tests might not need event handlers
                                          List<DomainEventHandler<?>> domainEventHandlers) {
        this.domainEventWebSocketPublisher = domainEventWebSocketPublisher;
        this.domainEventHandlers = (domainEventHandlers != null)
                ? domainEventHandlers
                : List.of();
    }


    /**
     * {@link DomainEventHandler} instances might want to use aggregate root repositories.
     * Those same repositories might want to publish domain events using {@link DomainEventWebSocketPublisher},
     * which itself depends on {@link DomainEventHandler} instances.
     * <p>
     * To avoid a circular-dependency instantiation problem, we add the handlers after the publisher is instantiated.
     */
    @PostConstruct
    void addDomainEventHandlers() {
        domainEventWebSocketPublisher.addHandlers(domainEventHandlers);
    }
}
