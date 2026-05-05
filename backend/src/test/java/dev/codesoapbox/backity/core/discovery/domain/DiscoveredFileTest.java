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
            var fileSize = new FileSize(5120L);
            assertThatThrownBy(() -> new DiscoveredFile(
                    null,
                    new GameTitle("Game 1"),
                    new FileTitle("Game 1 (Installer)"),
                    new FileVersion("1.0.0"),
                    new SourceFileUrl("/downlink/some_game/some_file"),
                    new FileName("game_1_installer.exe"),
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
            assertThatThrownBy(() -> new DiscoveredFile(
                    gameProviderId,
                    null,
                    new FileTitle("Game 1 (Installer)"),
                    new FileVersion("1.0.0"),
                    new SourceFileUrl("/downlink/some_game/some_file"),
                    new FileName("game_1_installer.exe"),
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
            assertThatThrownBy(() -> new DiscoveredFile(
                    gameProviderId,
                    new GameTitle("Game 1"),
                    null,
                    new FileVersion("1.0.0"),
                    new SourceFileUrl("/downlink/some_game/some_file"),
                    new FileName("game_1_installer.exe"),
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
            assertThatThrownBy(() -> new DiscoveredFile(
                    gameProviderId,
                    new GameTitle("Game 1"),
                    new FileTitle("Game 1 (Installer)"),
                    null,
                    new SourceFileUrl("/downlink/some_game/some_file"),
                    new FileName("game_1_installer.exe"),
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
            assertThatThrownBy(() -> new DiscoveredFile(
                    gameProviderId,
                    new GameTitle("Game 1"),
                    new FileTitle("Game 1 (Installer)"),
                    new FileVersion("1.0.0"),
                    null,
                    new FileName("game_1_installer.exe"),
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
            assertThatThrownBy(() -> new DiscoveredFile(
                    gameProviderId,
                    new GameTitle("Game 1"),
                    new FileTitle("Game 1 (Installer)"),
                    new FileVersion("1.0.0"),
                    new SourceFileUrl("/downlink/some_game/some_file"),
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
            assertThatThrownBy(() -> new DiscoveredFile(
                    gameProviderId,
                    new GameTitle("Game 1"),
                    new FileTitle("Game 1 (Installer)"),
                    new FileVersion("1.0.0"),
                    new SourceFileUrl("/downlink/some_game/some_file"),
                    new FileName("/downlink/some_game/some_file"),
                    null
            ))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("size");
        }
    }
}