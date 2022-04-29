package dev.codesoapbox.backity.integrations.gog.adapters.driving.api.http.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "GameFileDetailsResponse")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameFileDetailsJsonResponse {

    private String version;
    private String manualUrl;
    private String name;
    private String size;
}
