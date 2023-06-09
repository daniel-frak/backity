package dev.codesoapbox.backity.core.files.adapters.driven.persistence.game;

import dev.codesoapbox.backity.core.files.domain.game.Game;
import dev.codesoapbox.backity.core.files.domain.game.GameId;
import dev.codesoapbox.backity.core.files.domain.game.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
public class GameJpaRepository implements GameRepository {

    private final JpaGameSpringRepository springRepository;
    private final JpaGameMapper mapper;

    @Transactional
    @Override
    public void save(Game game) {
        var entity = mapper.toEntity(game);

        springRepository.save(entity);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Game> findByTitle(String title) {
        return springRepository.findByTitle(title)
                .map(mapper::toDomain);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Game> findById(GameId id) {
        return springRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    public Page<Game> findAll(Pageable pageable) {
        return springRepository.findAll(pageable)
                .map(mapper::toDomain);
    }
}
