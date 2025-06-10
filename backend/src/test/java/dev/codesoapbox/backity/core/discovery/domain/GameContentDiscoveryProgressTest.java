package dev.codesoapbox.backity.core.discovery.domain;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.domain.exceptions.InvalidGameContentDiscoveryProgressPercentageException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GameContentDiscoveryProgressTest {

    @Nested
    class Creation {

        @ParameterizedTest
        @ValueSource(ints = {-1, 101})
        void constructorShouldThrowGivenInvalidPercentage(int invalidPercentage) {
            var gameProviderId = new GameProviderId("GOG");
            Duration duration = Duration.ofSeconds(1);
            int gamesDiscovered = 5;
            int gameFilesDiscovered = 70;

            assertThatThrownBy(() -> new GameContentDiscoveryProgress(
                    gameProviderId, invalidPercentage, duration, gamesDiscovered, gameFilesDiscovered))
                    .isInstanceOf(InvalidGameContentDiscoveryProgressPercentageException.class)
                    .hasMessageContaining(String.valueOf(invalidPercentage));
        }
    }
}