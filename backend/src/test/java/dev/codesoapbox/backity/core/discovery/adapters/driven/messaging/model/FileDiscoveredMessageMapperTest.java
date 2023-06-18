package dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.gamefiledetails.domain.SourceFileDetails;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static dev.codesoapbox.backity.core.gamefiledetails.domain.TestGameFileDetails.discoveredFileDetails;
import static org.assertj.core.api.Assertions.assertThat;

class FileDiscoveredMessageMapperTest {

    private static final FileDiscoveredMessageMapper MAPPER = Mappers.getMapper(FileDiscoveredMessageMapper.class);

    @Test
    void shouldMapToMessage() {
        SourceFileDetails sourceFileDetails = discoveredFileDetails().build().getSourceFileDetails();

        FileDiscoveredMessage result = MAPPER.toMessage(sourceFileDetails);

        var expectedResult = new FileDiscoveredMessage(
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