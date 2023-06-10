package dev.codesoapbox.backity.core.files.adapters.driven.persistence;

import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * A version of a game file, either not yet downloaded, already downloaded or anything in-between.
 */
@Entity(name = "GameFileDetails")
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JpaGameFileDetails {

    @Id
    private UUID id;

    @NotNull
    private String source;

    @NotNull
    private String url;

    @NotNull
    private String title;

    @NotNull
    private String originalFileName;

    private String filePath;

    @NotNull
    private String gameTitle;

    @NotNull String gameId;

    @NotNull
    private String version;

    @NotNull
    private String size;

    @NotNull
    @CreatedDate
    private LocalDateTime dateCreated;

    @NotNull
    @LastModifiedDate
    private LocalDateTime dateModified;

    @NotNull
    @Enumerated(EnumType.STRING)
    private FileBackupStatus backupStatus;

    private String backupFailedReason;
}
