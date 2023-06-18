package dev.codesoapbox.backity.core.gamefiledetails.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.gamefiledetails.domain.FileBackupStatus;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BackupDetailsJpaEntity {

    @NotNull
    @Enumerated(EnumType.STRING)
    private FileBackupStatus status;

    private String failedReason;
    private String filePath;
}
