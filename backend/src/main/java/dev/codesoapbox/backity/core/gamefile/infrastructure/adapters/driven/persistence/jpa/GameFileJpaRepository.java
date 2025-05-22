package dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.core.gamefile.domain.exceptions.GameFileNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GameFileJpaRepository implements GameFileRepository {

    private final GameFileJpaEntitySpringRepository springRepository;
    private final GameFileJpaEntityMapper entityMapper;

    @Transactional
    @Override
    public GameFile save(GameFile gameFile) {
        GameFileJpaEntity entity = entityMapper.toEntity(gameFile);
        GameFileJpaEntity savedEntity = springRepository.save(entity);

        return entityMapper.toDomain(savedEntity);
    }

    @Override
    public boolean existsByUrlAndVersion(String url, String version) {
        return springRepository.existsByFileSourceUrlAndFileSourceVersion(url, version);
    }

    @Override
    public GameFile getById(GameFileId id) {
        return findById(id)
                .orElseThrow(() -> new GameFileNotFoundException(id));
    }

    @Override
    public Optional<GameFile> findById(GameFileId id) {
        return springRepository.findById(id.value())
                .map(entityMapper::toDomain);
    }

    @Override
    public List<GameFile> findAllByGameId(GameId gameId) {
        return springRepository.findAllByGameId(gameId.value()).stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Transactional
    @Override
    public void deleteById(GameFileId gameFileId) {
        springRepository.deleteById(gameFileId.value());
    }
}
