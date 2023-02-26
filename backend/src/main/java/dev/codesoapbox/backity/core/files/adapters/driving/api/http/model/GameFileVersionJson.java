package dev.codesoapbox.backity.core.files.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.files.domain.downloading.model.FileStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(name = "GameFileVersion")
@Data
public class GameFileVersionJson {

    private Long id;
    private String source;
    private String url;
    private String name;
    private String gameTitle;
    private String version;
    private String size;
    private LocalDateTime dateCreated;
    private LocalDateTime dateModified;
    private FileStatus status;
    private String failedReason;
}
