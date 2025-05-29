package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.model.filecopy;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driving.api.http.model.gamefile.FileCopyStatusHttpDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class FileCopyHttpDtoMapperTest {

    private static final FileCopyHttpDtoMapper MAPPER = Mappers.getMapper(FileCopyHttpDtoMapper.class);

    @Test
    void shouldMapDiscoveredToDto() {
        FileCopy domain = TestFileCopy.discovered();

        FileCopyHttpDto result = MAPPER.toDto(domain);

        var expectedResult = new FileCopyHttpDto(
                "6df888e8-90b9-4df5-a237-0cba422c0310",
                new FileCopyNaturalIdHttpDto(
                        "acde26d7-33c7-42ee-be16-bca91a604b48",
                        "eda52c13-ddf7-406f-97d9-d3ce2cab5a76"
                ),
                FileCopyStatusHttpDto.DISCOVERED,
                null,
                null,
                LocalDateTime.parse("2022-04-29T14:15:53"),
                LocalDateTime.parse("2023-04-29T14:15:53")
        );
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }

    @Test
    void shouldMapSuccessfulToDto() {
        FileCopy domain = TestFileCopy.successful();

        FileCopyHttpDto result = MAPPER.toDto(domain);

        var expectedResult = new FileCopyHttpDto(
                "6df888e8-90b9-4df5-a237-0cba422c0310",
                new FileCopyNaturalIdHttpDto(
                        "acde26d7-33c7-42ee-be16-bca91a604b48",
                        "eda52c13-ddf7-406f-97d9-d3ce2cab5a76"
                ),
                FileCopyStatusHttpDto.SUCCESS,
                null,
                "someFilePath",
                LocalDateTime.parse("2022-04-29T14:15:53"),
                LocalDateTime.parse("2023-04-29T14:15:53")
        );
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }

    @Test
    void shouldMapFailedToDto() {
        FileCopy domain = TestFileCopy.failed();

        FileCopyHttpDto result = MAPPER.toDto(domain);

        var expectedResult = new FileCopyHttpDto(
                "6df888e8-90b9-4df5-a237-0cba422c0310",
                new FileCopyNaturalIdHttpDto(
                        "acde26d7-33c7-42ee-be16-bca91a604b48",
                        "eda52c13-ddf7-406f-97d9-d3ce2cab5a76"
                ),
                FileCopyStatusHttpDto.FAILED,
                "someFailedReason",
                null,
                LocalDateTime.parse("2022-04-29T14:15:53"),
                LocalDateTime.parse("2023-04-29T14:15:53")
        );
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }
}