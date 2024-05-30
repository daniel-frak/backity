package dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.filedetails.domain.SourceFileDetails;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static dev.codesoapbox.backity.core.filedetails.domain.TestFileDetails.discoveredFileDetails;
import static org.assertj.core.api.Assertions.assertThat;

class FileDiscoveredWsEventMapperTest {

    private static final FileDiscoveredWsEventMapper MAPPER = Mappers.getMapper(FileDiscoveredWsEventMapper.class);

    @Test
    void shouldMapToMessage() {
        SourceFileDetails sourceFileDetails = discoveredFileDetails().build().getSourceFileDetails();

        FileDiscoveredWsEvent result = MAPPER.toWsEvent(sourceFileDetails);

        var expectedResult = new FileDiscoveredWsEvent(
                sourceFileDetails.originalGameTitle(),
                sourceFileDetails.originalFileName(),
                sourceFileDetails.fileTitle(),
                sourceFileDetails.size()
        );
        assertThat(result).hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }
}