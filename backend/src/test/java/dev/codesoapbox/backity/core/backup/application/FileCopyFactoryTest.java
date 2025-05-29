package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FileCopyFactoryTest {

    private static final FileCopyId FILE_COPY_ID = TestFileCopy.tracked().getId();

    private FileCopyFactory factory;

    @BeforeEach
    void setUp() {
        factory = new FileCopyFactory(() -> FILE_COPY_ID);
    }

    @Test
    void shouldCreate() {
        FileCopy expectedFileCopy = TestFileCopy.trackedBuilder()
                .dateCreated(null)
                .dateModified(null)
                .build();
        FileCopy result = factory.create(expectedFileCopy.getNaturalId());

        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expectedFileCopy);
    }
}