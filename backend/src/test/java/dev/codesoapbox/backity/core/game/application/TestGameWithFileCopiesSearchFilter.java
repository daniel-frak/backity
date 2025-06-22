package dev.codesoapbox.backity.core.game.application;

public class TestGameWithFileCopiesSearchFilter {

    public static GameWithFileCopiesSearchFilter onlySearchQuery(String searchQuery) {
        return new GameWithFileCopiesSearchFilter(searchQuery, null);
    }
}