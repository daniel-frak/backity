package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.embed;

import dev.codesoapbox.backity.core.gamefile.domain.FileSize;
import dev.codesoapbox.backity.core.gamefile.domain.FileSource;
import dev.codesoapbox.backity.core.gamefile.domain.TestFileSource;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogGameWithFiles;
import dev.codesoapbox.backity.gameproviders.gog.domain.TestGogGameFile;
import dev.codesoapbox.backity.gameproviders.gog.domain.TestGogGameWithFiles;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed.GogGameWithFilesMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GogGameWithFileCopiesMapperTest {

    private static final GogGameWithFilesMapper MAPPER = Mappers.getMapper(GogGameWithFilesMapper.class);

    @Test
    void shouldMapMinimalWithFileToFileSourceList() {
        GogGameWithFiles gogGame = TestGogGameWithFiles.minimalBuilder()
                .withFiles(List.of(TestGogGameFile.minimal()))
                .build();

        List<FileSource> result = MAPPER.toFileSources(gogGame);

        List<FileSource> expectedResult = List.of(
                TestFileSource.minimalGogBuilder()
                        .originalGameTitle("Test Game")
                        .fileTitle("Game 1 (Installer)")
                        .version("unknown")
                        .url("/downlink/some_game/some_file")
                        .originalFileName("game_1_installer.exe")
                        .size(new FileSize(1_048_576L))
                        .build()
        );
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }

    @Test
    void shouldMapMinimalWithNoFilesToFileSourceList() {
        GogGameWithFiles gogGame = TestGogGameWithFiles.minimal();

        List<FileSource> result = MAPPER.toFileSources(gogGame);

        assertThat(result).isEmpty();
    }
}