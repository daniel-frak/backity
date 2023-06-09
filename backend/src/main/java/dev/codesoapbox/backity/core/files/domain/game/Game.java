package dev.codesoapbox.backity.core.files.domain.game;

import lombok.*;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@Getter
public class Game {

    @EqualsAndHashCode.Include
    @NonNull
    private GameId id;

    @NonNull
    @Setter
    private String title;

    public static Game createNew(String title) {
        return new Game(GameId.newInstance(), title);
    }
}
