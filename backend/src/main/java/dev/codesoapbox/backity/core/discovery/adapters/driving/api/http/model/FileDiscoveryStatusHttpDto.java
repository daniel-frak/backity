package dev.codesoapbox.backity.core.discovery.adapters.driving.api.http.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "FileDiscoveryStatus")
@Data
public class FileDiscoveryStatusHttpDto {

    private String source;
    private boolean isInProgress;
}
