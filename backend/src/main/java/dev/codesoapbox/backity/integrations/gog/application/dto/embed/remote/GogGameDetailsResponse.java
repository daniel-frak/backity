package dev.codesoapbox.backity.integrations.gog.application.dto.embed.remote;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class GogGameDetailsResponse {

    private String title;
    private String backgroundImage;
    private String cdKey;
    private String textInformation;
    private List<List<Object>> downloads;
    private List<Object> extras;
    private String changelog;
}
