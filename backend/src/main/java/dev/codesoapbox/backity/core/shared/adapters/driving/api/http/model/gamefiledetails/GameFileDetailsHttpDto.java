package dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.gamefiledetails;

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
