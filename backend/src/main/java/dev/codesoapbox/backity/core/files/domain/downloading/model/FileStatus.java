package dev.codesoapbox.backity.core.files.domain.downloading.model;

public enum FileStatus {

    // @TODO Add 'IGNORED'
    DISCOVERED, ENQUEUED_FOR_DOWNLOAD, DOWNLOAD_IN_PROGRESS, DOWNLOADED, DOWNLOAD_FAILED
}
