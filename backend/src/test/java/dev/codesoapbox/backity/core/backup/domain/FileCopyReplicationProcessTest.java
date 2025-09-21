package dev.codesoapbox.backity.core.backup.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FileCopyReplicationProcessTest {

    private FileCopyReplicationProcess process;

    @BeforeEach
    void setUp() {
        process = new FileCopyReplicationProcess();
    }

    @Test
    void tryStartShouldReturnTrueGivenNotInProgressAndBackupRecoveryCompleted() {
        process.markBackupRecoveryCompleted();

        boolean result = process.tryStart();

        assertThat(result).isTrue();
    }

    @Test
    void tryStartShouldReturnFalseGivenNotInProgressButBackupRecoveryNotCompleted() {
        boolean result = process.tryStart();

        assertThat(result).isFalse();
    }

    @Test
    void tryStartShouldReturnFalseGivenBackupRecoveryCompletedButInProgress() {
        process.markBackupRecoveryCompleted();
        process.tryStart();

        boolean result = process.tryStart();

        assertThat(result).isFalse();
    }

    @Test
    void tryStartShouldReturnTrueGivenInProgressProcessWasCompleted() {
        process.markBackupRecoveryCompleted();
        process.tryStart();

        process.markAsCompleted();
        boolean result = process.tryStart();

        assertThat(result).isTrue();
    }
}