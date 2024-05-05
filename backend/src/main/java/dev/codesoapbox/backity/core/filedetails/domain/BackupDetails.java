package dev.codesoapbox.backity.core.filedetails.domain;

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
