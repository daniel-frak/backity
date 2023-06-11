package dev.codesoapbox.backity.core.files.domain.backup.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class BackupDetails {

    @NonNull
    private FileBackupStatus status;

    private String failedReason;
    private String filePath;
}
