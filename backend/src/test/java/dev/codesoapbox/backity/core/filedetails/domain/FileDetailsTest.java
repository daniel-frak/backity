package dev.codesoapbox.backity.core.filedetails.domain;

import org.junit.jupiter.api.Test;

import static dev.codesoapbox.backity.core.filedetails.domain.TestFileDetails.discoveredFileDetails;
import static org.assertj.core.api.Assertions.assertThat;

class FileDetailsTest {

    @Test
    void shouldEnqueue() {
        FileDetails fileDetails = discoveredFileDetails().build();

        fileDetails.enqueue();

        assertThat(fileDetails.getBackupDetails().getStatus()).isEqualTo(FileBackupStatus.ENQUEUED);
    }

    @Test
    void shouldFail() {
        FileDetails fileDetails = discoveredFileDetails().build();

        fileDetails.fail("someFailedReason");

        assertThat(fileDetails.getBackupDetails().getFailedReason()).isEqualTo("someFailedReason");
        assertThat(fileDetails.getBackupDetails().getStatus()).isEqualTo(FileBackupStatus.FAILED);
    }

    @Test
    void shouldMarkAsDownloaded() {
        FileDetails fileDetails = discoveredFileDetails().build();

        fileDetails.markAsDownloaded("someFilePath");

        assertThat(fileDetails.getBackupDetails().getFilePath()).isEqualTo("someFilePath");
        assertThat(fileDetails.getBackupDetails().getStatus()).isEqualTo(FileBackupStatus.SUCCESS);
    }
}