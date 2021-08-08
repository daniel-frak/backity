package dev.codesoapbox.backity.integrations.gog.application.dto.embed;

import lombok.Data;

@Data
public class GameFileDetailsResponse {

    private String version;
    private String manualUrl;
    private String name;
    private String size;
}
