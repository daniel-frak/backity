package dev.codesoapbox.backity.core.game.application;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyStatus;

public record GameWithFileCopiesSearchFilter(
        String searchQuery,
        FileCopyStatus status
) {

    public static GameWithFileCopiesSearchFilter onlySearchQuery(String searchQuery) {
        return new GameWithFileCopiesSearchFilter(searchQuery, null);
    }
}
