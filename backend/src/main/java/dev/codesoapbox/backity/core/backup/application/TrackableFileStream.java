package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backup.application.writeprogress.OutputStreamProgressTracker;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import reactor.core.publisher.Flux;

public interface TrackableFileStream {

    void writeToStorageSolution(StorageSolution storageSolution, String filePath, Flux<Boolean> cancelTrigger);

    OutputStreamProgressTracker outputStreamProgressTracker();
}
