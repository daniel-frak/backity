package dev.codesoapbox.backity.core.game.domain;

import lombok.*;

import java.time.LocalDateTime;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@Getter
public class Game {

    @EqualsAndHashCode.Include
    @NonNull
    private GameId id;

    private final LocalDateTime dateCreated; // Provided by DB
    private final LocalDateTime dateModified; // Provided by DB

    @NonNull
    @Setter
    private String title;

    public static Game createNew(String title) {
        return new Game(GameId.newInstance(), null, null, title);
    }
}
