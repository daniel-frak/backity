package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.gameproviders.gog.application.GogConfigInfo;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class GogConfigHttpDtoMapperTest {

    private static final GogConfigHttpDtoMapper MAPPER =
            Mappers.getMapper(GogConfigHttpDtoMapper.class);

    @Test
    void shouldMapToDto() {
        GogConfigInfo domainObject = domainObject();

        GogConfigHttpDto result = MAPPER.toDto(domainObject);

        GogConfigHttpDto expectedResult = dto();
        assertThat(result).isEqualTo(expectedResult);
    }

    private GogConfigInfo domainObject() {
        return new GogConfigInfo("someAuthUrl");
    }

    private GogConfigHttpDto dto() {
        return new GogConfigHttpDto("someAuthUrl");
    }
}