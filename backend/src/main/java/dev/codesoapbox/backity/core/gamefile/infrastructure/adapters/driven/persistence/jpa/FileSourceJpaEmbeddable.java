package dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driven.persistence.jpa;

import jakarta.persistence.Column;
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
    @Column(nullable = false)
    private String gameProviderId;

    @NotNull
    @Column(nullable = false)
    private String originalGameTitle;

    @NotNull
    @Column(nullable = false)
    private String fileTitle;

    @NotNull
    @Column(nullable = false)
    private String version;

    @NotNull
    @Column(nullable = false)
    private String url;

    @NotNull
    @Column(nullable = false)
    private String originalFileName;

    @NotNull
    @Column(nullable = false)
    private long sizeInBytes;
}
