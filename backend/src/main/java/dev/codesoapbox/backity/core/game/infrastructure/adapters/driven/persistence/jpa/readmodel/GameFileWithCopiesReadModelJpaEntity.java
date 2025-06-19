package dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa.readmodel;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings(
        // @Data and @EqualsAndHashCode are safe to use here because:
        // - We explicitly add a @NoArgsConstructor (required by Jpa spec)
        // - @EqualsAndHashCode only uses id (so won't break HashSets)
        // - We don't do lazy loading (so toString() won't break it).
        {"com.intellij.jpb.LombokDataInspection", "com.intellij.jpb.LombokEqualsAndHashCodeInspection"})
@Entity
@Immutable
@Table(name = "game_file")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class GameFileWithCopiesReadModelJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    private UUID gameId;

    private LocalDateTime dateCreated;
    private LocalDateTime dateModified;

    @Embedded
    private FileSourceReadModelJpaEmbeddable fileSource;

    @OneToMany(mappedBy = "naturalId.gameFileId")
    private List<FileCopyReadModelJpaEntity> fileCopies = new ArrayList<>();
}