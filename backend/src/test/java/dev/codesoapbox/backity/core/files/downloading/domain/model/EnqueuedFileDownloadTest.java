package dev.codesoapbox.backity.core.files.downloading.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnqueuedFileDownloadTest {

    @Test
    void shouldFail() {
        var enqueuedFileDownload = new EnqueuedFileDownload();

        enqueuedFileDownload.fail("someFailedReason");

        assertEquals("someFailedReason", enqueuedFileDownload.getFailedReason());
        assertEquals(DownloadStatus.FAILED, enqueuedFileDownload.getStatus());
    }
}