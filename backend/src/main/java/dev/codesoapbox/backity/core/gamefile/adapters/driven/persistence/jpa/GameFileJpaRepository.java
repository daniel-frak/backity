package dev.codesoapbox.backity.core.gamefile.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.gamefile.domain.FileBackupStatus;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.core.gamefile.domain.exceptions.GameFileNotFoundException;
import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.core.shared.adapters.driven.persistence.PageEntityMapper;
import dev.codesoapbox.backity.core.shared.adapters.driven.persistence.PaginationEntityMapper;
import dev.codesoapbox.backity.core.shared.domain.Page;
import dev.codesoapbox.backity.core.shared.domain.Pagination;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GameFileJpaRepository implements GameFileRepository {

    private static final Sort SORT_BY_DATE_CREATED_ASC = Sort.by(Sort.Direction.ASC, "dateCreated");

    private final GameFileJpaEntitySpringRepository springRepository;
    private final GameFileJpaEntityMapper entityMapper;
    private final PageEntityMapper pageMapper;
    private final PaginationEntityMapper paginationMapper;

    @Override
    public Optional<GameFile> findOldestWaitingForDownload() {
        PageRequest pageable = PageRequest.of(0, 1, SORT_BY_DATE_CREATED_ASC);
        return springRepository.findAllWaitingForDownload(pageable).get()
                .findFirst()
                .map(entityMapper::toModel);
    }

    @Override
    public Page<GameFile> findAllWaitingForDownload(Pagination pagination) {
        Pageable pageable = paginationMapper.toEntity(pagination, SORT_BY_DATE_CREATED_ASC);
        org.springframework.data.domain.Page<GameFileJpaEntity> foundPage =
                springRepository.findAllWaitingForDownload(pageable);
        return pageMapper.toDomain(foundPage, entityMapper::toModel);
    }

    @Transactional
    @Override
    public GameFile save(GameFile gameFile) {
        GameFileJpaEntity entity = entityMapper.toEntity(gameFile);
        GameFileJpaEntity savedEntity = springRepository.save(entity);
        return entityMapper.toModel(savedEntity);
    }

    @Override
    public Optional<GameFile> findCurrentlyDownloading() {
        return springRepository.findByFileBackupStatus(FileBackupStatus.IN_PROGRESS)
                .map(entityMapper::toModel);
    }

    @Override
    public Page<GameFile> findAllProcessed(Pagination pagination) {
        Pageable pageable = paginationMapper.toEntity(pagination, SORT_BY_DATE_CREATED_ASC);
        org.springframework.data.domain.Page<GameFileJpaEntity> foundPage =
                springRepository.findAllProcessed(pageable);
        return pageMapper.toDomain(foundPage, entityMapper::toModel);
    }

    @Override
    public boolean existsByUrlAndVersion(String url, String version) {
        return springRepository.existsByGameProviderFileUrlAndGameProviderFileVersion(url, version);
    }

    @Override
    public GameFile getById(GameFileId id) {
        return findById(id)
                .orElseThrow(() -> new GameFileNotFoundException(id));
    }

    @Override
    public Optional<GameFile> findById(GameFileId id) {
        return springRepository.findById(id.value())
                .map(entityMapper::toModel);
    }

    @Override
    public Page<GameFile> findAllDiscovered(Pagination pagination) {
        Pageable pageable = paginationMapper.toEntity(pagination, SORT_BY_DATE_CREATED_ASC);
        org.springframework.data.domain.Page<GameFileJpaEntity> foundPage =
                springRepository.findAllByFileBackupStatus(pageable, FileBackupStatus.DISCOVERED);
        return pageMapper.toDomain(foundPage, entityMapper::toModel);
    }

    @Override
    public List<GameFile> findAllByGameId(GameId gameId) {
        return springRepository.findAllByGameId(gameId.value()).stream()
                .map(entityMapper::toModel)
                .toList();
    }

    @Transactional
    @Override
    public void deleteById(GameFileId gameFileId) {
        springRepository.deleteById(gameFileId.value());
    }
}
