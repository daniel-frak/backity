package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.embed;

import dev.codesoapbox.backity.core.gamefile.domain.FileSize;
import dev.codesoapbox.backity.core.gamefile.domain.GameProviderFile;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameProviderFile;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogGameWithFiles;
import dev.codesoapbox.backity.gameproviders.gog.domain.TestGogGameFile;
import dev.codesoapbox.backity.gameproviders.gog.domain.TestGogGameWithFiles;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GogGameWithFilesMapperTest {

    private static final GogGameWithFilesMapper MAPPER = Mappers.getMapper(GogGameWithFilesMapper.class);

    @Test
    void shouldMapMinimalWithFileToGameProviderFile() {
        GogGameWithFiles gogGame = TestGogGameWithFiles.minimalBuilder()
                .withFiles(List.of(TestGogGameFile.minimal()))
                .build();

        List<GameProviderFile> result = MAPPER.toGameProviderFiles(gogGame);

        List<GameProviderFile> expectedResult = List.of(
                TestGameProviderFile.minimalGogBuilder()
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
    void shouldMapMinimalWithNoFilesToGameProviderFile() {
        GogGameWithFiles gogGame = TestGogGameWithFiles.minimal();

        List<GameProviderFile> result = MAPPER.toGameProviderFiles(gogGame);

        assertThat(result).isEmpty();
    }
}