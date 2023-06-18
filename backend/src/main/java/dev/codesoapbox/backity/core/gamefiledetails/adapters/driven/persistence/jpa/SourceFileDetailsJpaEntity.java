package dev.codesoapbox.backity.core.gamefiledetails.adapters.driven.persistence.jpa;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SourceFileDetailsJpaEntity {

    private String sourceId;
    private String originalGameTitle;
    private String fileTitle;
    private String version;
    private String url;
    private String originalFileName;
    private String size;
}
