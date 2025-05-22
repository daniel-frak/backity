package dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driven.persistence.jpa;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileSourceJpaEmbeddable {

    @NotNull
    private String gameProviderId;

    @NotNull
    private String originalGameTitle;

    @NotNull
    private String fileTitle;

    @NotNull
    private String version;

    @NotNull
    private String url;

    @NotNull
    private String originalFileName;

    @NotNull
    private long sizeInBytes;
}
