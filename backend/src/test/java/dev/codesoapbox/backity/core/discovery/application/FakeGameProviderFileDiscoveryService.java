package dev.codesoapbox.backity.core.discovery.application;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.domain.DiscoveredFile;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Slf4j
class FakeGameProviderFileDiscoveryService implements GameProviderFileDiscoveryService {

    public final CountDownLatch finishLatch = new CountDownLatch(1);
    private final AtomicReference<Consumer<DiscoveredFile>> discoveredFileConsumerRef = new AtomicReference<>();

    @Getter
    private final AtomicInteger timesTriggered = new AtomicInteger();

    @Getter
    private int stoppedTimes = 0;

    @Setter
    private RuntimeException exceptionToThrowDuringDiscovery;

    public boolean hasBeenTriggered() {
        return timesTriggered.get() > 0;
    }

    public void simulateFileDiscovery(DiscoveredFile discoveredFile) {
        discoveredFileConsumerRef.get().accept(discoveredFile);
    }

    public void complete() {
        finishLatch.countDown();
    }

    @Override
    public GameProviderId getGameProviderId() {
        return new GameProviderId("someGameProviderId");
    }

    @Override
    public void discoverAllFiles(
            Consumer<DiscoveredFile> discoveredFileConsumer, GameDiscoveryProgressTracker progressTracker) {
        timesTriggered.incrementAndGet();
        if (exceptionToThrowDuringDiscovery != null) {
            throw exceptionToThrowDuringDiscovery;
        }
        this.discoveredFileConsumerRef.set(discoveredFileConsumer);

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
}
