package dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.model.embed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameFileDetailsResponse {

    private String version;
    private String manualUrl;
    private String name;
    private String size;
}
