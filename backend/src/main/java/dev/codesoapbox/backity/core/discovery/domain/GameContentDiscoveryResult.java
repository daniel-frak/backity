package dev.codesoapbox.backity.core.discovery.domain;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import lombok.*;

import java.time.LocalDateTime;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@Getter
@Setter
public final class GameContentDiscoveryResult {

    @EqualsAndHashCode.Include
    @NonNull
    private final GameProviderId gameProviderId;

    @NonNull
    private LocalDateTime startedAt;

    @NonNull
    private LocalDateTime stoppedAt;

    private GameContentDiscoveryOutcome discoveryOutcome;
    private LocalDateTime lastSuccessfulDiscoveryCompletedAt;
    private int gamesDiscovered;
    private int gameFilesDiscovered;
}
