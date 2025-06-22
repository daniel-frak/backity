package dev.codesoapbox.backity.core.game.application;

import dev.codesoapbox.backity.core.game.application.readmodel.GameWithFileCopiesReadModel;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;

public interface GameWithFileCopiesReadModelRepository {

    Page<GameWithFileCopiesReadModel> findAllPaginated(Pagination pagination, GameWithFileCopiesSearchFilter filter);
}
