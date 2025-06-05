package dev.codesoapbox.backity.core.backup.application.downloadprogress;

import dev.codesoapbox.backity.core.backup.domain.events.FileDownloadProgressChangedEvent;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import dev.codesoapbox.backity.testing.time.FakeClock;
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
class DownloadProgressFactoryTest {

    private DownloadProgressFactory downloadProgressFactory;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    private FakeClock clock;

    @BeforeEach
    void setUp() {
        clock = new FakeClock(Clock.fixed(Instant.EPOCH, ZoneId.of("UTC")));
        downloadProgressFactory = new DownloadProgressFactory(domainEventPublisher);
    }

    @Test
    void shouldCreate() {
        var fileCopyId = new FileCopyId("6df888e8-90b9-4df5-a237-0cba422c0310");
        DownloadProgress result = downloadProgressFactory.create(fileCopyId);

        assertThat(result).isNotNull();
    }

    @Test
    void createdDownloadProgressShouldPublishEventOnChange() {
        var fileCopyId = new FileCopyId("6df888e8-90b9-4df5-a237-0cba422c0310");
        long entireContentLengthBytes = 10L;
        int halfContentLengthBytes = 5;
        long timeToDownloadHalfOfContentInSeconds = 2L;
        DownloadProgress downloadProgress = downloadProgressFactory.create(fileCopyId);
        downloadProgress.initializeTracking(entireContentLengthBytes, clock);
        clock.moveForward(Duration.of(timeToDownloadHalfOfContentInSeconds, ChronoUnit.SECONDS));

        downloadProgress.incrementDownloadedBytes(halfContentLengthBytes);

        verify(domainEventPublisher).publish(
                new FileDownloadProgressChangedEvent(fileCopyId, 50,
                        Duration.ofSeconds(timeToDownloadHalfOfContentInSeconds)));
    }
}