package dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa.readmodel;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileCopyNaturalIdReadModelJpaEmbeddable {

    private UUID gameFileId;
    private UUID backupTargetId;
}
