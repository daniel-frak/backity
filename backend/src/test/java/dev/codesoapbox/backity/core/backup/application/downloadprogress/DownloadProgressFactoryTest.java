package dev.codesoapbox.backity.core.backup.application.downloadprogress;

import dev.codesoapbox.backity.core.backup.domain.events.FileDownloadProgressChangedEvent;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import dev.codesoapbox.backity.testing.time.FakeClock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DownloadProgressFactoryTest {

    private DownloadProgressFactory downloadProgressFactory;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    private FakeClock clock;

    @BeforeEach
    void setUp() {
        clock = FakeClock.atEpochUtc();
        downloadProgressFactory = new DownloadProgressFactory(domainEventPublisher);
    }

    @Test
    void shouldCreate() {
        FileCopy fileCopy = TestFileCopy.inProgress();
        DownloadProgress result = downloadProgressFactory.create(fileCopy);

        assertThat(result).isNotNull();
    }

    @Test
    void createdDownloadProgressShouldPublishEventOnChange() {
        FileCopy fileCopy = TestFileCopy.inProgress();
        long entireContentLengthBytes = 10L;
        int halfContentLengthBytes = 5;
        long timeToDownloadHalfOfContentInSeconds = 2L;
        DownloadProgress downloadProgress = downloadProgressFactory.create(fileCopy);
        downloadProgress.initializeTracking(entireContentLengthBytes, clock);
        clock.moveForward(Duration.of(timeToDownloadHalfOfContentInSeconds, ChronoUnit.SECONDS));

        downloadProgress.incrementDownloadedBytes(halfContentLengthBytes);

        verify(domainEventPublisher).publish(
                new FileDownloadProgressChangedEvent(fileCopy.getId(), fileCopy.getNaturalId(), 50,
                        Duration.ofSeconds(timeToDownloadHalfOfContentInSeconds)));
    }
}