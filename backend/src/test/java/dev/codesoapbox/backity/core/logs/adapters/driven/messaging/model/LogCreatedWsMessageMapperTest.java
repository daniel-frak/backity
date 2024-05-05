package dev.codesoapbox.backity.core.logs.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.logs.domain.model.LogCreatedMessage;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class LogCreatedWsMessageMapperTest {

    private static final LogCreatedWsMessageMapper MAPPER = Mappers.getMapper(LogCreatedWsMessageMapper.class);

    @Test
    void shouldMapToWsMessage() {
        LogCreatedMessage message = LogCreatedMessage.of("someMessage", 5);

        LogCreatedWsMessage result = MAPPER.toWsMessage(message);

        var expectedResult = new LogCreatedWsMessage("someMessage", 5);
        assertThat(result).hasNoNullFieldsOrProperties()
                .isEqualTo(expectedResult);
    }
}