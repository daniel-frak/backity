package dev.codesoapbox.backity.integrations.gog.domain.model.embed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameDetailsResponse {

    private String title;
    private String backgroundImage;
    private String cdKey;
    private String textInformation;
    private List<GameFileDetailsResponse> files;
    private String changelog;
}
