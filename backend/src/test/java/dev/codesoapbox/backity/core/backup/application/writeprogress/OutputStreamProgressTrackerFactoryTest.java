package dev.codesoapbox.backity.core.backup.application.writeprogress;

import dev.codesoapbox.backity.core.backup.domain.events.FileCopyReplicationProgressChangedEvent;
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
class OutputStreamProgressTrackerFactoryTest {

    private OutputStreamProgressTrackerFactory outputStreamProgressTrackerFactory;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    private FakeClock clock;

    @BeforeEach
    void setUp() {
        clock = FakeClock.atEpochUtc();
        outputStreamProgressTrackerFactory = new OutputStreamProgressTrackerFactory(domainEventPublisher);
    }

    @Test
    void shouldCreate() {
        FileCopy fileCopy = TestFileCopy.inProgress();
        OutputStreamProgressTracker result = outputStreamProgressTrackerFactory.create(fileCopy);

        assertThat(result).isNotNull();
    }

    @Test
    void createdOutputStreamProgressShouldPublishEventOnChange() {
        FileCopy fileCopy = TestFileCopy.inProgress();
        long entireContentLengthBytes = 10L;
        int halfContentLengthBytes = 5;
        long timeToWriteHalfOfContentInSeconds = 2L;
        OutputStreamProgressTracker outputStreamProgressTracker = outputStreamProgressTrackerFactory.create(fileCopy);
        outputStreamProgressTracker.initializeTracking(entireContentLengthBytes, clock);
        clock.moveForward(Duration.of(timeToWriteHalfOfContentInSeconds, ChronoUnit.SECONDS));

        outputStreamProgressTracker.incrementWrittenBytes(halfContentLengthBytes);

        verify(domainEventPublisher).publish(
                new FileCopyReplicationProgressChangedEvent(fileCopy.getId(), fileCopy.getNaturalId(), 50,
                        Duration.ofSeconds(timeToWriteHalfOfContentInSeconds)));
    }
}