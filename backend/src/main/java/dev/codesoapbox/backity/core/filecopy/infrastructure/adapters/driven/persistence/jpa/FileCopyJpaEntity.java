package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@SuppressWarnings(
        // @Data and @EqualsAndHashCode are safe to use here because:
        // - We explicitly add a @NoArgsConstructor (required by Jpa spec)
        // - @EqualsAndHashCode only uses id (so won't break HashSets)
        // - We don't do lazy loading (so toString() won't break it).
        {"com.intellij.jpb.LombokDataInspection", "com.intellij.jpb.LombokEqualsAndHashCodeInspection"})
@Entity(name = "FileCopy")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FileCopyJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    @Embedded
    private FileCopyNaturalIdentifierJpaEmbeddable naturalId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private FileCopyStatus status;

    private String failedReason;
    private String filePath;

    @NotNull
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime dateCreated;

    @NotNull
    @LastModifiedDate
    private LocalDateTime dateModified;
}
