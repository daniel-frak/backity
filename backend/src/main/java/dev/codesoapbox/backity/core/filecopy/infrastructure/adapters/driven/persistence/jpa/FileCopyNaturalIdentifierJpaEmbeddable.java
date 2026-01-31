package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driven.persistence.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileCopyNaturalIdentifierJpaEmbeddable {

    @NotNull
    @Column(nullable = false)
    private UUID gameFileId;

    @NotNull
    @Column(nullable = false)
    private UUID backupTargetId;
}
