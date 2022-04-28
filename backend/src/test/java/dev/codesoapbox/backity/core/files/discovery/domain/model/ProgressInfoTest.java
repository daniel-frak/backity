package dev.codesoapbox.backity.core.files.discovery.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ProgressInfoTest {

    @Test
    void noneShouldCreateInfoWithNoProgress() {
        var result = ProgressInfo.none();

        assertEquals(0, result.getPercentage());
        assertNull(result.getTimeLeft());
    }
}