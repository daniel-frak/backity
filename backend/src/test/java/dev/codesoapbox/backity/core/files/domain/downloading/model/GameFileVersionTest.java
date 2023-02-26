package dev.codesoapbox.backity.core.files.domain.downloading.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameFileVersionTest {

    @Test
    void shouldFail() {
        var enqueuedFileDownload = new GameFileVersion();

        enqueuedFileDownload.fail("someFailedReason");

        assertEquals("someFailedReason", enqueuedFileDownload.getFailedReason());
        assertEquals(FileStatus.DOWNLOAD_FAILED, enqueuedFileDownload.getStatus());
    }

    @Test
    void shouldMarkAsDownloaded() {
        var enqueuedFileDownload = new GameFileVersion();

        enqueuedFileDownload.markAsDownloaded("someFilePath");

        assertEquals("someFilePath", enqueuedFileDownload.getFilePath());
        assertEquals(FileStatus.DOWNLOADED, enqueuedFileDownload.getStatus());
    }
}