package dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa.readmodel;

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
public class FileCopyNaturalIdReadModelJpaEmbeddable {

    @NotNull
    @Column(nullable = false)
    private UUID gameFileId;

    @NotNull
    @Column(nullable = false)
    private UUID backupTargetId;
}
