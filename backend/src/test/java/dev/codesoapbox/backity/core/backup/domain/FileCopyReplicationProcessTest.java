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
    void canStartShouldReturnTrueGivenNotInProgressAndBackupRecoveryCompleted() {
        process.markBackupRecoveryCompleted();

        boolean result = process.canStart();

        assertThat(result).isTrue();
    }

    @Test
    void canStartShouldReturnFalseGivenNotInProgressButBackupRecoveryNotCompleted() {
        boolean result = process.canStart();

        assertThat(result).isFalse();
    }

    @Test
    void canStartShouldReturnFalseGivenBackupRecoveryCompletedButInProgress() {
        process.markAsInProgress();

        boolean result = process.canStart();

        assertThat(result).isFalse();
    }

    @Test
    void catStartShouldReturnTrueGivenInProgressProcessWasCompleted() {
        process.markBackupRecoveryCompleted();
        process.markAsInProgress();

        process.markAsCompleted();
        boolean result = process.canStart();

        assertThat(result).isTrue();
    }
}