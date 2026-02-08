package dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa.readmodel;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "source_file")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SourceFileWithCopiesReadModelJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    @NotNull
    private UUID gameId;

    @NotNull
    private LocalDateTime dateCreated;

    @NotNull
    private LocalDateTime dateModified;

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

    @OneToMany(mappedBy = "naturalId.sourceFileId")
    private List<FileCopyReadModelJpaEntity> fileCopies = new ArrayList<>();
}