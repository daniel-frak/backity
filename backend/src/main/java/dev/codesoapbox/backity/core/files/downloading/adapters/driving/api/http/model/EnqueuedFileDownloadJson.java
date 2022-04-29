package dev.codesoapbox.backity.core.files.downloading.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.files.downloading.domain.model.DownloadStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(name = "EnqueuedFileDownload")
@Data
public class EnqueuedFileDownloadJson {

    private Long id;
    private String source;
    private String url;
    private String name;
    private String gameTitle;
    private String version;
    private String size;
    private LocalDateTime dateCreated;
    private DownloadStatus status;
    private String failedReason;
}
