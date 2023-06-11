package dev.codesoapbox.backity.core.files.adapters.driven.messaging.model;

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
    private String gameId;
    private SourceFileDetailsMessage sourceFileDetails;
    private BackupDetailsMessage backupDetails;
    private LocalDateTime dateCreated;
    private LocalDateTime dateModified;
}
