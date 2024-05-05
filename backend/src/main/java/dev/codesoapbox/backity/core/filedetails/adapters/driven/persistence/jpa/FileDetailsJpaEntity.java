package dev.codesoapbox.backity.core.filedetails.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.game.adapters.driven.persistence.jpa.GameJpaEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * A version of a game file, either not yet downloaded, already downloaded or anything in-between.
 */
@Entity(name = "FileDetails")
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FileDetailsJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", referencedColumnName = "id")
    private GameJpaEntity game;

    @Embedded
    private SourceFileDetailsJpaEntity sourceFileDetails;

    @Embedded
    private BackupDetailsJpaEntity backupDetails;

    @NotNull
    @CreatedDate
    private LocalDateTime dateCreated;

    @NotNull
    @LastModifiedDate
    private LocalDateTime dateModified;
}
