package dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driven.persistence.jpa;

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
@SuppressWarnings(
        // @Data and @EqualsAndHashCode are safe to use here because:
        // - We explicitly add a @NoArgsConstructor (required by Jpa spec)
        // - @EqualsAndHashCode only uses id (so won't break HashSets)
        // - We don't do lazy loading (so toString() won't break it).
        {"com.intellij.jpb.LombokDataInspection", "com.intellij.jpb.LombokEqualsAndHashCodeInspection"})
@Entity(name = "GameFile")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class GameFileJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    @NotNull
    private UUID gameId;

    @Embedded
    private FileSourceJpaEntity fileSource;

    @Embedded
    private FileCopyJpaEntity fileCopy;

    @NotNull
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime dateCreated;

    @NotNull
    @LastModifiedDate
    private LocalDateTime dateModified;
}
