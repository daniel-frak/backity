package dev.codesoapbox.backity.core.sourcefile.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFile;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFileId;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFileRepository;
import dev.codesoapbox.backity.core.sourcefile.domain.exceptions.SourceFileNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SourceFileJpaRepository implements SourceFileRepository {

    private final SourceFileJpaEntitySpringRepository springRepository;
    private final SourceFileJpaEntityMapper entityMapper;

    @Transactional
    @Override
    public SourceFile save(SourceFile sourceFile) {
        SourceFileJpaEntity entity = entityMapper.toEntity(sourceFile);
        SourceFileJpaEntity savedEntity = springRepository.save(entity);

        return entityMapper.toDomain(savedEntity);
    }

    @Override
    public boolean existsByUrlAndVersion(String url, String version) {
        return springRepository.existsByUrlAndVersion(url, version);
    }

    @Override
    public SourceFile getById(SourceFileId id) {
        return findById(id)
                .orElseThrow(() -> new SourceFileNotFoundException(id));
    }

    @Override
    public Optional<SourceFile> findById(SourceFileId id) {
        return springRepository.findById(id.value())
                .map(entityMapper::toDomain);
    }

    @Override
    public List<SourceFile> findAllByGameId(GameId gameId) {
        return springRepository.findAllByGameId(gameId.value()).stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Transactional
    @Override
    public void deleteById(SourceFileId sourceFileId) {
        springRepository.deleteById(sourceFileId.value());
    }

    @Override
    public List<SourceFile> findAllByIdIn(Collection<SourceFileId> ids) {
        List<UUID> sourceFileUuids = ids.stream()
                .map(SourceFileId::value)
                .toList();
        return springRepository.findAllByIdIn(sourceFileUuids).stream()
                .map(entityMapper::toDomain)
                .toList();
    }
}
