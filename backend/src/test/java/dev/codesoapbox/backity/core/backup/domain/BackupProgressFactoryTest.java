package dev.codesoapbox.backity.core.backup.domain;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupProgressChangedEvent;
import dev.codesoapbox.backity.core.shared.domain.DomainEventPublisher;
import dev.codesoapbox.backity.testing.FakeClock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BackupProgressFactoryTest {

    private BackupProgressFactory backupProgressFactory;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    private FakeClock clock;

    @BeforeEach
    void setUp() {
        clock = new FakeClock(Clock.fixed(Instant.EPOCH, ZoneId.of("UTC")));
        backupProgressFactory = new BackupProgressFactory(domainEventPublisher);
    }

    @Test
    void shouldCreate() {
        BackupProgress result = backupProgressFactory.create();

        assertThat(result).isNotNull();
    }

    @Test
    void createdBackupProgressShouldPublishEventOnChange() {
        long entireContentLengthBytes = 10L;
        int halfContentLengthBytes = 5;
        long timeToDownloadHalfOfContentInSeconds = 2L;
        BackupProgress backupProgress = backupProgressFactory.create();
        backupProgress.initializeTracking(entireContentLengthBytes, clock);
        clock.moveForward(Duration.of(timeToDownloadHalfOfContentInSeconds, ChronoUnit.SECONDS));

        backupProgress.incrementDownloadedBytes(halfContentLengthBytes);

        verify(domainEventPublisher).publish(
                new FileBackupProgressChangedEvent(50, timeToDownloadHalfOfContentInSeconds));
    }
}