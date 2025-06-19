package dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa.readmodel;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileSourceReadModelJpaEmbeddable {

    private String gameProviderId;
    private String originalGameTitle;
    private String fileTitle;
    private String version;
    private String url;
    private String originalFileName;
    private long sizeInBytes;
}
