package dev.codesoapbox.backity.core.game.application;

import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GameFileWithCopiesTest {

    @Nested
    class Creation {

        @SuppressWarnings("DataFlowIssue")
        @Test
        void shouldThrowGivenNullGameFile() {
            List<FileCopyWithProgress> fileCopiesWithProgress = emptyList();
            assertThatThrownBy(() -> new GameFileWithCopies(null, fileCopiesWithProgress))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("gameFile");
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void shouldThrowGivenNullFileCopiesWithProgress() {
            GameFile gameFile = TestGameFile.gog();
            assertThatThrownBy(() -> new GameFileWithCopies(gameFile, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("fileCopiesWithProgress");
        }
    }
}