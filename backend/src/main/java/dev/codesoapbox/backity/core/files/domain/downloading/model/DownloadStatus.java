package dev.codesoapbox.backity.core.files.domain.downloading.model;

public enum DownloadStatus {

    // @TODO Rename 'WAITING' to 'ENQUEUED'
    // @TODO Add 'IGNORED'
    DISCOVERED, WAITING, IN_PROGRESS, DOWNLOADED, FAILED
}
