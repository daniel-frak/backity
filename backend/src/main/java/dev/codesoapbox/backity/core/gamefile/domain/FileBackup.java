package dev.codesoapbox.backity.core.gamefile.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class FileBackup {

    @NonNull
    private FileBackupStatus status;

    private String failedReason;
    private String filePath;
}
