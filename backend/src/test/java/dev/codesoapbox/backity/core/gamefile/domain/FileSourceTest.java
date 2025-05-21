package dev.codesoapbox.backity.core.gamefile.domain;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.gamefile.domain.exceptions.FileSourceUrlEmptyException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileSourceTest {

    @Nested
    class Creation {

        @SuppressWarnings("DataFlowIssue")
        @Test
        void constructorShouldThrowGivenGameProviderIdIsNull() {
            var fileSize = new FileSize(5120L);
            assertThatThrownBy(() -> new FileSource(
                    null,
                    "Game 1",
                    "Game 1 (Installer)",
                    "1.0.0",
                    "/downlink/some_game/some_file",
                    "game_1_installer.exe",
                    fileSize
            ))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("gameProviderId");
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void constructorShouldThrowGivenOriginalGameTitleIsNull() {
            var gameProviderId = new GameProviderId("GOG");
            var fileSize = new FileSize(5120L);
            assertThatThrownBy(() -> new FileSource(
                    gameProviderId,
                    null,
                    "Game 1 (Installer)",
                    "1.0.0",
                    "/downlink/some_game/some_file",
                    "game_1_installer.exe",
                    fileSize
            ))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("originalGameTitle");
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void constructorShouldThrowGivenFileTitleIsNull() {
            var gameProviderId = new GameProviderId("GOG");
            var fileSize = new FileSize(5120L);
            assertThatThrownBy(() -> new FileSource(
                    gameProviderId,
                    "Game 1",
                    null,
                    "1.0.0",
                    "/downlink/some_game/some_file",
                    "game_1_installer.exe",
                    fileSize
            ))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("fileTitle");
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void constructorShouldThrowGivenVersionIsNull() {
            var gameProviderId = new GameProviderId("GOG");
            var fileSize = new FileSize(5120L);
            assertThatThrownBy(() -> new FileSource(
                    gameProviderId,
                    "Game 1",
                    "Game 1 (Installer)",
                    null,
                    "/downlink/some_game/some_file",
                    "game_1_installer.exe",
                    fileSize
            ))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("version");
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void constructorShouldThrowGivenUrlIsNull() {
            var gameProviderId = new GameProviderId("GOG");
            var fileSize = new FileSize(5120L);
            assertThatThrownBy(() -> new FileSource(
                    gameProviderId,
                    "Game 1",
                    "Game 1 (Installer)",
                    "1.0.0",
                    null,
                    "game_1_installer.exe",
                    fileSize
            ))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("url");
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void constructorShouldThrowGivenOriginalFileNameIsNull() {
            var gameProviderId = new GameProviderId("GOG");
            var fileSize = new FileSize(5120L);
            assertThatThrownBy(() -> new FileSource(
                    gameProviderId,
                    "Game 1",
                    "Game 1 (Installer)",
                    "1.0.0",
                    "/downlink/some_game/some_file",
                    null,
                    fileSize
            ))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("originalFileName");
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void constructorShouldThrowGivenFileSourceIsNull() {
            var gameProviderId = new GameProviderId("GOG");
            assertThatThrownBy(() -> new FileSource(
                    gameProviderId,
                    "Game 1",
                    "Game 1 (Installer)",
                    "1.0.0",
                    "/downlink/some_game/some_file",
                    "/downlink/some_game/some_file",
                    null
            ))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("size");
        }

        @Test
        void constructorShouldThrowGivenUrlIsEmpty() {
            TestFileSource.Builder fileSourceBuilder = TestFileSource.minimalGogBuilder()
                    .url("   ");

            assertThatThrownBy(fileSourceBuilder::build)
                    .isInstanceOf(FileSourceUrlEmptyException.class);
        }
    }
}