package dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.model.embed;

import lombok.Data;

@Data
public class GameFileDetailsResponse {

    private String version;
    private String manualUrl;
    private String name;
    private String size;
}
