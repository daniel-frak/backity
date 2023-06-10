package dev.codesoapbox.backity.core.files.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(name = "GameFileDetails")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameFileDetailsJson {

    private Long id;
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
