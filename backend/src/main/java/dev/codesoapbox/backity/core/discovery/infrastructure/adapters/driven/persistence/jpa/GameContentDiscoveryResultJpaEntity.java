package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryOutcome;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@SuppressWarnings(
        // @Data and @EqualsAndHashCode are safe to use here because:
        // - We explicitly add a @NoArgsConstructor (required by Jpa spec)
        // - @EqualsAndHashCode only uses id (so won't break HashSets)
        // - We don't do lazy loading (so toString() won't break it).
        {"com.intellij.jpb.LombokDataInspection", "com.intellij.jpb.LombokEqualsAndHashCodeInspection"})
@Entity(name = "GameContentDiscoveryResult")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class GameContentDiscoveryResultJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    private String gameProviderId;

    @NotNull
    private LocalDateTime startedAt;

    @NotNull
    private LocalDateTime stoppedAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    private GameContentDiscoveryOutcome discoveryOutcome;

    @NotNull
    private LocalDateTime lastSuccessfulDiscoveryCompletedAt;

    @NotNull
    private int gamesDiscovered;

    @NotNull
    private int gameFilesDiscovered;
}
