package dev.codesoapbox.backity.core.logs.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.logs.domain.model.LogCreatedEvent;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class LogCreatedWsEventMapperTest {

    private static final LogCreatedWsEventMapper MAPPER = Mappers.getMapper(LogCreatedWsEventMapper.class);

    @Test
    void shouldMapToWsEvent() {
        LogCreatedEvent message = LogCreatedEvent.of("someMessage", 5);

        LogCreatedWsEvent result = MAPPER.toWsEvent(message);

        var expectedResult = new LogCreatedWsEvent("someMessage", 5);
        assertThat(result).hasNoNullFieldsOrProperties()
                .isEqualTo(expectedResult);
    }
}