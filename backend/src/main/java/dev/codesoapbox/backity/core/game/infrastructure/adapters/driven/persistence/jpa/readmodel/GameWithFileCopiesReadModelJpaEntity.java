package dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa.readmodel;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
@Table(name = "game")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class GameWithFileCopiesReadModelJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;
    private String title;
    private LocalDateTime dateCreated;
    private LocalDateTime dateModified;

    @OneToMany(mappedBy = "gameId")
    private List<GameFileWithCopiesReadModelJpaEntity> gameFilesWithCopies = new ArrayList<>();
}