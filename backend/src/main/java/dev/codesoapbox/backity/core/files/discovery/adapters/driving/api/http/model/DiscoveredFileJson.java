package dev.codesoapbox.backity.core.files.discovery.adapters.driving.api.http.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(name = "DiscoveredFile")
@Data
public class DiscoveredFileJson {

    private String id;
    private String url;
    private String version;
    private String source;
    private String name;
    private String gameTitle;
    private String size;
    private LocalDateTime dateCreated;
    private LocalDateTime dateModified;
    private boolean enqueued;
    private boolean ignored;
}
