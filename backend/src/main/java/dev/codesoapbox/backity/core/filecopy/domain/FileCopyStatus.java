package dev.codesoapbox.backity.core.filecopy.domain;

import java.util.List;

public enum FileCopyStatus {

    TRACKED, ENQUEUED, IN_PROGRESS, STORED_INTEGRITY_UNKNOWN, STORED_INTEGRITY_VERIFIED, FAILED;

    /*
    A FileCopy in this status means that a BackupTarget can be deleted without losing files.
     */
    public static final List<FileCopyStatus> NON_LOCKING_STATUSES = List.of(TRACKED, FAILED);
}
