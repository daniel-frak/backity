package dev.codesoapbox.backity.core.filedetails.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.filedetails.domain.FileBackupStatus;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetails;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetailsId;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetailsRepository;
import dev.codesoapbox.backity.core.filedetails.domain.exceptions.FileDetailsNotFoundException;
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
public class FileDetailsJpaRepository implements FileDetailsRepository {

    private static final Sort SORT_BY_DATE_CREATED_ASC = Sort.by(Sort.Direction.ASC, "dateCreated");

    private final FileDetailsJpaEntitySpringRepository springRepository;
    private final FileDetailsJpaEntityMapper entityMapper;
    private final PageEntityMapper pageMapper;
    private final PaginationEntityMapper paginationMapper;

    @Override
    public Optional<FileDetails> findOldestWaitingForDownload() {
        PageRequest pageable = PageRequest.of(0, 1, SORT_BY_DATE_CREATED_ASC);
        return springRepository.findAllWaitingForDownload(pageable).get()
                .findFirst()
                .map(entityMapper::toModel);
    }

    @Override
    public Page<FileDetails> findAllWaitingForDownload(Pagination pagination) {
        Pageable pageable = paginationMapper.toEntity(pagination, SORT_BY_DATE_CREATED_ASC);
        org.springframework.data.domain.Page<FileDetailsJpaEntity> foundPage =
                springRepository.findAllWaitingForDownload(pageable);
        return pageMapper.toDomain(foundPage, entityMapper::toModel);
    }

    @Transactional
    @Override
    public FileDetails save(FileDetails fileDetails) {
        FileDetailsJpaEntity entity = entityMapper.toEntity(fileDetails);
        FileDetailsJpaEntity savedEntity = springRepository.save(entity);
        return entityMapper.toModel(savedEntity);
    }

    @Override
    public Optional<FileDetails> findCurrentlyDownloading() {
        return springRepository.findByBackupDetailsStatus(FileBackupStatus.IN_PROGRESS)
                .map(entityMapper::toModel);
    }

    @Override
    public Page<FileDetails> findAllProcessed(Pagination pagination) {
        Pageable pageable = paginationMapper.toEntity(pagination, SORT_BY_DATE_CREATED_ASC);
        org.springframework.data.domain.Page<FileDetailsJpaEntity> foundPage =
                springRepository.findAllProcessed(pageable);
        return pageMapper.toDomain(foundPage, entityMapper::toModel);
    }

    @Override
    public boolean existsByUrlAndVersion(String url, String version) {
        return springRepository.existsBySourceFileDetailsUrlAndSourceFileDetailsVersion(url, version);
    }

    @Override
    public FileDetails getById(FileDetailsId id) {
        return findById(id)
                .orElseThrow(() -> new FileDetailsNotFoundException(id));
    }

    @Override
    public Optional<FileDetails> findById(FileDetailsId id) {
        return springRepository.findById(id.value())
                .map(entityMapper::toModel);
    }

    @Override
    public Page<FileDetails> findAllDiscovered(Pagination pagination) {
        Pageable pageable = paginationMapper.toEntity(pagination, SORT_BY_DATE_CREATED_ASC);
        org.springframework.data.domain.Page<FileDetailsJpaEntity> foundPage =
                springRepository.findAllByBackupDetailsStatus(pageable, FileBackupStatus.DISCOVERED);
        return pageMapper.toDomain(foundPage, entityMapper::toModel);
    }

    @Override
    public List<FileDetails> findAllByGameId(GameId gameId) {
        return springRepository.findAllByGameId(gameId.value()).stream()
                .map(entityMapper::toModel)
                .toList();
    }
}
