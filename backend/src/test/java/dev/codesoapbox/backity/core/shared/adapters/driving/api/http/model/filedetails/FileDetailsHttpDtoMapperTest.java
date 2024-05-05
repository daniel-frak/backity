package dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.filedetails;

import dev.codesoapbox.backity.core.filedetails.domain.FileDetails;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static dev.codesoapbox.backity.core.filedetails.domain.TestFileDetails.fullFileDetails;
import static org.assertj.core.api.Assertions.assertThat;

class FileDetailsHttpDtoMapperTest {

    private static final FileDetailsHttpDtoMapper MAPPER = Mappers.getMapper(FileDetailsHttpDtoMapper.class);

    @Test
    void shouldMapToDto() {
        FileDetails domain = fullFileDetails().build();

        FileDetailsHttpDto result = MAPPER.toDto(domain);

        var expectedResult = new FileDetailsHttpDto(
                "acde26d7-33c7-42ee-be16-bca91a604b48",
                "1eec1c19-25bf-4094-b926-84b5bb8fa281",
                new SourceFileDetailsHttpDto(
                        "someSourceId",
                        "someOriginalGameTitle",
                        "someFileTitle",
                        "someVersion",
                        "someUrl",
                        "someOriginalFileName",
                        "5 KB"
                ),
                new BackupDetailsHttpDto(
                        FileBackupStatusHttpDto.DISCOVERED,
                        "someFailedReason",
                        "someFilePath"
                ),
                LocalDateTime.parse("2022-04-29T14:15:53"),
                LocalDateTime.parse("2023-04-29T14:15:53")
        );

        assertThat(result)
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }
}