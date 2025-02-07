package dev.codesoapbox.backity.core.backup.application.downloadprogress;

public interface ProgressTracker {

    void incrementDownloadedBytes(int length);

    void incrementProcessedElements();

    void updateContentLength();
}
