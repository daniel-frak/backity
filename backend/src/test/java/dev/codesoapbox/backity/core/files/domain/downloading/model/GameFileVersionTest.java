package dev.codesoapbox.backity.core.files.domain.downloading.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameFileVersionTest {

    @Test
    void shouldFail() {
        var enqueuedFileDownload = new GameFileVersion();

        enqueuedFileDownload.fail("someFailedReason");

        assertEquals("someFailedReason", enqueuedFileDownload.getFailedReason());
        assertEquals(DownloadStatus.FAILED, enqueuedFileDownload.getStatus());
    }
}