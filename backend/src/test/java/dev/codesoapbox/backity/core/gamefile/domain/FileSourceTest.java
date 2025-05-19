package dev.codesoapbox.backity.core.gamefile.domain;

import dev.codesoapbox.backity.core.gamefile.domain.exceptions.FileSourceUrlEmptyException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileSourceTest {

    @Test
    void constructorShouldThrowGivenUrlIsEmpty() {
        TestFileSource.Builder fileSourceBuilder = TestFileSource.minimalGogBuilder()
                .url("   ");

        assertThatThrownBy(fileSourceBuilder::build)
                .isInstanceOf(FileSourceUrlEmptyException.class);
    }
}