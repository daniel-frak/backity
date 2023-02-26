package dev.codesoapbox.backity.core.files.adapters.driving.api.http.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "FileDiscoveryStatus")
@Data
public class FileDiscoveryStatusJson {

    private String source;
    private boolean isInProgress;
}
