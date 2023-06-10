package dev.codesoapbox.backity.core.files.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import dev.codesoapbox.backity.core.shared.domain.IncludeInDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@IncludeInDocumentation
@Schema(name = "GameFileDetailsMessage")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameFileDetailsMessage {

    private String id;
    private String source;
    private String url;
    private String title;
    private String originalFileName;
    private String filePath;
    private String gameTitle;
    private String gameId;
    private String version;
    private String size;
    private LocalDateTime dateCreated;
    private LocalDateTime dateModified;
    private FileBackupStatus backupStatus;
    private String backupFailedReason;
}
