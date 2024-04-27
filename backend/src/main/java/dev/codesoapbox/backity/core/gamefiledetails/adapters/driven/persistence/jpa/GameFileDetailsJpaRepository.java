package dev.codesoapbox.backity.core.gamefiledetails.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.core.gamefiledetails.domain.FileBackupStatus;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetails;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetailsId;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetailsRepository;
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

@Transactional(readOnly=true)
@RequiredArgsConstructor
public class GameFileDetailsJpaRepository implements GameFileDetailsRepository {

    private static final Sort SORT_BY_DATE_CREATED_ASC = Sort.by(Sort.Direction.ASC, "dateCreated");

    private final GameFileDetailsJpaEntitySpringRepository springRepository;
    private final GameFileDetailsJpaEntityMapper entityMapper;
    private final PageEntityMapper pageMapper;
    private final PaginationEntityMapper paginationMapper;

    @Override
    public Optional<GameFileDetails> findOldestWaitingForDownload() {
        PageRequest pageable = PageRequest.of(0, 1, SORT_BY_DATE_CREATED_ASC);
        return springRepository.findAllWaitingForDownload(pageable).get()
                .findFirst()
                .map(entityMapper::toModel);
    }

    @Override
    public Page<GameFileDetails> findAllWaitingForDownload(Pagination pagination) {
        Pageable pageable = paginationMapper.toEntity(pagination, SORT_BY_DATE_CREATED_ASC);
        org.springframework.data.domain.Page<GameFileDetailsJpaEntity> foundPage =
                springRepository.findAllWaitingForDownload(pageable);
        return pageMapper.toDomain(foundPage, entityMapper::toModel);
    }

    @Transactional
    @Override
    public GameFileDetails save(GameFileDetails gameFileDetails) {
        GameFileDetailsJpaEntity entity = entityMapper.toEntity(gameFileDetails);
        GameFileDetailsJpaEntity savedEntity = springRepository.save(entity);
        return entityMapper.toModel(savedEntity);
    }

    @Override
    public Optional<GameFileDetails> findCurrentlyDownloading() {
        return springRepository.findByBackupDetailsStatus(FileBackupStatus.IN_PROGRESS)
                .map(entityMapper::toModel);
    }

    @Override
    public Page<GameFileDetails> findAllProcessed(Pagination pagination) {
        Pageable pageable = paginationMapper.toEntity(pagination, SORT_BY_DATE_CREATED_ASC);
        org.springframework.data.domain.Page<GameFileDetailsJpaEntity> foundPage =
                springRepository.findAllProcessed(pageable);
        return pageMapper.toDomain(foundPage, entityMapper::toModel);
    }

    @Override
    public boolean existsByUrlAndVersion(String url, String version) {
        return springRepository.existsBySourceFileDetailsUrlAndSourceFileDetailsVersion(url, version);
    }

    @Override
    public Optional<GameFileDetails> findById(GameFileDetailsId id) {
        return springRepository.findById(id.value())
                .map(entityMapper::toModel);
    }

    @Override
    public Page<GameFileDetails> findAllDiscovered(Pagination pagination) {
        Pageable pageable = paginationMapper.toEntity(pagination, SORT_BY_DATE_CREATED_ASC);
        org.springframework.data.domain.Page<GameFileDetailsJpaEntity> foundPage =
                springRepository.findAllByBackupDetailsStatus(pageable, FileBackupStatus.DISCOVERED);
        return pageMapper.toDomain(foundPage, entityMapper::toModel);
    }

    @Override
    public List<GameFileDetails> findAllByGameId(GameId gameId) {
        return springRepository.findAllByGameId(gameId.value()).stream()
                .map(entityMapper::toModel)
                .toList();
    }
}
