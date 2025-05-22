package dev.codesoapbox.backity.core.discovery.application;

import dev.codesoapbox.backity.core.gamefile.domain.FileCopy;
import dev.codesoapbox.backity.core.gamefile.domain.FileCopyId;
import dev.codesoapbox.backity.core.gamefile.domain.TestFileCopy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FileCopyFactoryTest {

    private static final FileCopyId FILE_COPY_ID = TestFileCopy.discovered().getId();

    private FileCopyFactory factory;

    @BeforeEach
    void setUp() {
        factory = new FileCopyFactory(() -> FILE_COPY_ID);
    }

    @Test
    void shouldCreate() {
        FileCopy result = factory.create();

        FileCopy expectedFileCopy = TestFileCopy.discovered();
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expectedFileCopy);
    }
}