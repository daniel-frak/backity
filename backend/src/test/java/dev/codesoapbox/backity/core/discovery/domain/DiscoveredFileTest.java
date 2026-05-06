package dev.codesoapbox.backity.core.discovery.domain;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.game.domain.GameTitle;
import dev.codesoapbox.backity.core.sourcefile.domain.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DiscoveredFileTest {

    @Nested
    class Creation {

        @SuppressWarnings("DataFlowIssue")
        @Test
        void constructorShouldThrowGivenGameProviderIdIsNull() {
            var originalGameTitle = new GameTitle("Game 1");
            var fileTitle = new FileTitle("Game 1 (Installer)");
            var version = new FileVersion("1.0.0");
            var url = new SourceFileUrl("/downlink/some_game/some_file");
            var originalFileName = new FileName("game_1_installer.exe");
            var fileSize = new FileSize(5120L);

            assertThatThrownBy(() -> new DiscoveredFile(
                    null,
                    originalGameTitle,
                    fileTitle,
                    version,
                    url,
                    originalFileName,
                    fileSize
            ))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("gameProviderId");
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void constructorShouldThrowGivenOriginalGameTitleIsNull() {
            var gameProviderId = new GameProviderId("GOG");
            var fileTitle = new FileTitle("Game 1 (Installer)");
            var version = new FileVersion("1.0.0");
            var url = new SourceFileUrl("/downlink/some_game/some_file");
            var originalFileName = new FileName("game_1_installer.exe");
            var fileSize = new FileSize(5120L);

            assertThatThrownBy(() -> new DiscoveredFile(
                    gameProviderId,
                    null,
                    fileTitle,
                    version,
                    url,
                    originalFileName,
                    fileSize
            ))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("originalGameTitle");
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void constructorShouldThrowGivenFileTitleIsNull() {
            var gameProviderId = new GameProviderId("GOG");
            var originalGameTitle = new GameTitle("Game 1");
            var version = new FileVersion("1.0.0");
            var url = new SourceFileUrl("/downlink/some_game/some_file");
            var originalFileName = new FileName("game_1_installer.exe");
            var fileSize = new FileSize(5120L);

            assertThatThrownBy(() -> new DiscoveredFile(
                    gameProviderId,
                    originalGameTitle,
                    null,
                    version,
                    url,
                    originalFileName,
                    fileSize
            ))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("fileTitle");
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void constructorShouldThrowGivenVersionIsNull() {
            var gameProviderId = new GameProviderId("GOG");
            var originalGameTitle = new GameTitle("Game 1");
            var fileTitle = new FileTitle("Game 1 (Installer)");
            var url = new SourceFileUrl("/downlink/some_game/some_file");
            var originalFileName = new FileName("game_1_installer.exe");
            var fileSize = new FileSize(5120L);

            assertThatThrownBy(() -> new DiscoveredFile(
                    gameProviderId,
                    originalGameTitle,
                    fileTitle,
                    null,
                    url,
                    originalFileName,
                    fileSize
            ))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("version");
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void constructorShouldThrowGivenUrlIsNull() {
            var gameProviderId = new GameProviderId("GOG");
            var originalGameTitle = new GameTitle("Game 1");
            var fileTitle = new FileTitle("Game 1 (Installer)");
            var version = new FileVersion("1.0.0");
            var originalFileName = new FileName("game_1_installer.exe");
            var fileSize = new FileSize(5120L);

            assertThatThrownBy(() -> new DiscoveredFile(
                    gameProviderId,
                    originalGameTitle,
                    fileTitle,
                    version,
                    null,
                    originalFileName,
                    fileSize
            ))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("url");
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void constructorShouldThrowGivenOriginalFileNameIsNull() {
            var gameProviderId = new GameProviderId("GOG");
            var originalGameTitle = new GameTitle("Game 1");
            var fileTitle = new FileTitle("Game 1 (Installer)");
            var version = new FileVersion("1.0.0");
            var url = new SourceFileUrl("/downlink/some_game/some_file");
            var fileSize = new FileSize(5120L);

            assertThatThrownBy(() -> new DiscoveredFile(
                    gameProviderId,
                    originalGameTitle,
                    fileTitle,
                    version,
                    url,
                    null,
                    fileSize
            ))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("originalFileName");
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void constructorShouldThrowGivenSizeIsNull() {
            var gameProviderId = new GameProviderId("GOG");
            var originalGameTitle = new GameTitle("Game 1");
            var fileTitle = new FileTitle("Game 1 (Installer)");
            var version = new FileVersion("1.0.0");
            var url = new SourceFileUrl("/downlink/some_game/some_file");
            var originalFileName = new FileName("/downlink/some_game/some_file");

            assertThatThrownBy(() -> new DiscoveredFile(
                    gameProviderId,
                    originalGameTitle,
                    fileTitle,
                    version,
                    url,
                    originalFileName,
                    null
            ))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("size");
        }
    }
}