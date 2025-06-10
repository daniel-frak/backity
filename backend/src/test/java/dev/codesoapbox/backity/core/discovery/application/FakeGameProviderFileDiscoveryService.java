package dev.codesoapbox.backity.core.discovery.application;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.ProgressInfo;
import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.gamefile.domain.FileSource;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Slf4j
class FakeGameProviderFileDiscoveryService implements GameProviderFileDiscoveryService {

    public final CountDownLatch finishLatch = new CountDownLatch(1);
    private final AtomicReference<Consumer<FileSource>> fileSourceConsumerRef = new AtomicReference<>();
    private final List<Consumer<ProgressInfo>> progressConsumers = new CopyOnWriteArrayList<>();

    @Getter
    private final AtomicInteger timesTriggered = new AtomicInteger();

    @Getter
    private int stoppedTimes = 0;

    @Setter
    private RuntimeException exceptionToThrowDuringDiscovery;

    public boolean hasBeenTriggered() {
        return timesTriggered.get() > 0;
    }

    public void simulateFileDiscovery(FileSource fileSource) {
        fileSourceConsumerRef.get().accept(fileSource);
    }

    public void simulateProgressUpdate() {
        progressConsumers.forEach(c -> c.accept(getProgressInfo()));
    }

    @Override
    public ProgressInfo getProgressInfo() {
        return new ProgressInfo(25, Duration.of(1234, ChronoUnit.SECONDS));
    }

    public void complete() {
        finishLatch.countDown();
    }

    @Override
    public GameProviderId getGameProviderId() {
        return new GameProviderId("someGameProviderId");
    }

    @Override
    public void discoverAllFiles(Consumer<FileSource> fileSourceConsumer) {
        timesTriggered.incrementAndGet();
        if (exceptionToThrowDuringDiscovery != null) {
            throw exceptionToThrowDuringDiscovery;
        }
        this.fileSourceConsumerRef.set(fileSourceConsumer);

        try {
            finishLatch.await();
            log.info("Finished file discovery");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("File discovery interrupted", e);
        }
    }

    @Override
    public void stopFileDiscovery() {
        stoppedTimes++;
        finishLatch.countDown();
    }

    @Override
    public void subscribeToProgress(Consumer<ProgressInfo> progressConsumer) {
        progressConsumers.add(progressConsumer);
    }
}
