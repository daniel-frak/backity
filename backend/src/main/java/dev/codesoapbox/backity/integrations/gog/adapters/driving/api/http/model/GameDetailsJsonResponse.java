package dev.codesoapbox.backity.integrations.gog.adapters.driving.api.http.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(name = "GameDetailsResponse")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameDetailsJsonResponse {

    private String title;
    private String backgroundImage;
    private String cdKey;
    private String textInformation;
    private List<GameFileDetailsJsonResponse> files;
    private String changelog;
}
