package dev.codesoapbox.backity.integrations.gog.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.integrations.gog.application.GogConfigInfo;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class GogConfigResponseHttpDtoMapperTest {

    private static final GogConfigResponseHttpDtoMapper MAPPER =
            Mappers.getMapper(GogConfigResponseHttpDtoMapper.class);

    @Test
    void shouldMapToDto() {
        GogConfigInfo domainObject = domainObject();

        GogConfigResponseHttpDto result = MAPPER.toDto(domainObject);

        GogConfigResponseHttpDto expectedResult = dto();
        assertThat(result).isEqualTo(expectedResult);
    }

    private GogConfigInfo domainObject() {
        return new GogConfigInfo("someAuthUrl");
    }

    private GogConfigResponseHttpDto dto() {
        return new GogConfigResponseHttpDto("someAuthUrl");
    }
}