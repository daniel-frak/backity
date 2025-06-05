package dev.codesoapbox.backity.core.storagesolution.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionId;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionStatus;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class StorageSolutionStatusesResponseHttpDtoMapperTest {

    private static final StorageSolutionStatusesResponseHttpDtoMapper MAPPER =
            Mappers.getMapper(StorageSolutionStatusesResponseHttpDtoMapper.class);

    @Test
    void shouldMapToDto() {
        Map<StorageSolutionId, StorageSolutionStatus> domain = domain();

        StorageSolutionStatusesResponseHttpDto result = MAPPER.toDto(domain);

        StorageSolutionStatusesResponseHttpDto expectedResult = dto();
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }

    private Map<StorageSolutionId, StorageSolutionStatus> domain() {
        return Map.of(new StorageSolutionId("S3"), StorageSolutionStatus.NOT_CONNECTED);
    }

    private StorageSolutionStatusesResponseHttpDto dto() {
        return new StorageSolutionStatusesResponseHttpDto(Map.of("S3", StorageSolutionStatusHttpDto.NOT_CONNECTED));
    }
}