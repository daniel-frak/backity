package dev.codesoapbox.gogbackupservice.gog.application.dto.embed;

import lombok.Data;

import java.util.List;

@Data
public class GameDetailsResponse {

    private String title;
    private String backgroundImage;
    private String cdKey;
    private String textInformation;
    private List<GameFileDetailsResponse> files;
//    private List<GameExtrasDetails> extrasFiles;
    private String changelog;
}
