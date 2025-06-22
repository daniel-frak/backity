package dev.codesoapbox.backity.core.game.application;

public record GameWithFileCopiesSearchFilter(
        String searchQuery
) {

    public static GameWithFileCopiesSearchFilter onlySearchQuery(String searchQuery) {
        return new GameWithFileCopiesSearchFilter(searchQuery);
    }
}
