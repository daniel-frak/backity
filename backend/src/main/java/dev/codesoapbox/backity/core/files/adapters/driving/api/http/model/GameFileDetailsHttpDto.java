package dev.codesoapbox.backity.core.files.adapters.driving.api.http.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(name = "GameFileDetails")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameFileDetailsHttpDto {

    private String id;
    private String gameId;
    private SourceFileDetailsHttpDto sourceFileDetails;
    private BackupDetailsHttpDto backupDetails;
    private LocalDateTime dateCreated;
    private LocalDateTime dateModified;
}
