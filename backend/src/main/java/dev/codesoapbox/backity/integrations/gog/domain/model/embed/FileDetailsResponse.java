package dev.codesoapbox.backity.integrations.gog.domain.model.embed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDetailsResponse {

    private String version;
    private String manualUrl;
    private String name;
    private String size;
    private String fileTitle;
}
