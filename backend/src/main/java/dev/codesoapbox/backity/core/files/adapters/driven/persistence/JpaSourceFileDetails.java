package dev.codesoapbox.backity.core.files.adapters.driven.persistence;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JpaSourceFileDetails {

    private String sourceId;
    private String originalGameTitle;
    private String fileTitle;
    private String version;
    private String url;
    private String originalFileName;
    private String size;
}
