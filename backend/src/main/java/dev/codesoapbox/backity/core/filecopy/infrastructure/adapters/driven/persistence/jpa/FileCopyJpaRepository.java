package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.filecopy.domain.*;
import dev.codesoapbox.backity.core.filecopy.domain.exceptions.FileCopyNotFoundException;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFileId;
import dev.codesoapbox.backity.shared.domain.DomainEvent;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.SpringPageMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.SpringPageableMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings("java:S1200") // No good way to reduce dependencies in this class
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FileCopyJpaRepository implements FileCopyRepository {

    private static final Sort SORT_BY_DATE_MODIFIED_ASC = Sort.by(Sort.Direction.ASC, "dateModified");

    private final FileCopySpringRepository springRepository;
    private final FileCopyJpaEntityMapper entityMapper;
    private final SpringPageMapper pageMapper;
    private final SpringPageableMapper paginationMapper;
    private final DomainEventPublisher domainEventPublisher;

    @Transactional
    @Override
    public void save(FileCopy fileCopy) {
        internalSave(fileCopy);
    }

    private FileCopy internalSave(FileCopy fileCopy) {
        FileCopyJpaEntity entity = entityMapper.toEntity(fileCopy);
        FileCopyJpaEntity savedEntity = springRepository.save(entity);
        publishDomainEvents(fileCopy);

        return entityMapper.toDomain(savedEntity);
    }

    private void publishDomainEvents(FileCopy fileCopy) {
        for (DomainEvent domainEvent : fileCopy.getDomainEvents()) {
            domainEventPublisher.publish(domainEvent);
        }

        // Clear the domain events to prevent them from being republished in case this instance is saved several times:
        fileCopy.clearDomainEvents();
    }

    @Override
    public FileCopy getById(FileCopyId id) {
        return findBySourceFileId(id)
                .orElseThrow(() -> new FileCopyNotFoundException(id));
    }

    private Optional<FileCopy> findBySourceFileId(FileCopyId id) {
        return springRepository.findById(id.value())
                .map(entityMapper::toDomain);
    }

    @SuppressWarnings("java:S1166") // No reason to log or rethrow DataIntegrityViolationException
    @Override
    public FileCopy getByNaturalIdOrCreate(FileCopyNaturalId naturalId, Supplier<FileCopy> fileCopyFactory) {
        return springRepository.findByNaturalIdSourceFileIdAndNaturalIdBackupTargetId(
                        naturalId.sourceFileId().value(), naturalId.backupTargetId().value())
                .map(entityMapper::toDomain)
                .orElseGet(() -> {
                    try {
                        return create(fileCopyFactory);
                    } catch (DataIntegrityViolationException _) {
                        // Someone else just inserted it – reload and return that
                        FileCopyJpaEntity entity =
                                springRepository.getByNaturalIdSourceFileIdAndNaturalIdBackupTargetId(
                                        naturalId.sourceFileId().value(), naturalId.backupTargetId().value());
                        return entityMapper.toDomain(entity);
                    }
                });
    }

    private FileCopy create(Supplier<FileCopy> fileCopyFactory) {
        FileCopy fileCopy = fileCopyFactory.get();
        return internalSave(fileCopy);
    }

    @Override
    public Optional<FileCopy> findOldestEnqueued() {
        PageRequest pageable = PageRequest.of(0, 1, SORT_BY_DATE_MODIFIED_ASC);

        return springRepository.findAllByStatusIn(pageable, List.of(FileCopyStatus.ENQUEUED)).get()
                .findFirst()
                .map(entityMapper::toDomain);
    }

    @Override
    public Page<FileCopy> findAllInProgressOrEnqueued(Pagination pagination) {
        Pageable pageable = paginationMapper.toPageable(pagination);
        org.springframework.data.domain.Page<FileCopyJpaEntity> foundPage =
                springRepository.findAllInProgressOrEnqueuedOrderByStatusThenDateModified(pageable);

        return pageMapper.toDomain(foundPage, entityMapper::toDomain);
    }

    @Override
    public List<FileCopy> findAllInProgress() {
        org.springframework.data.domain.Page<FileCopyJpaEntity> foundPage =
                springRepository.findAllByStatusIn(Pageable.unpaged(), List.of(FileCopyStatus.IN_PROGRESS));

        return foundPage.getContent().stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Override
    public List<FileCopy> findAllBySourceFileId(SourceFileId id) {
        return springRepository.findAllByNaturalIdSourceFileId(id.value()).stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existByBackupTargetIdAndStatusNotIn(BackupTargetId id, List<FileCopyStatus> statuses) {
        return springRepository.existsByNaturalIdBackupTargetIdAndStatusNotIn(id.value(), statuses);
    }

    @Override
    public List<BackupTargetId> getUniqueBackupTargetIdsByStatusNotIn(List<FileCopyStatus> statuses) {
        return springRepository.getUniqueBackupTargetIdsByStatusNotIn(statuses);
    }

    @Transactional
    @Override
    public void deleteByBackupTargetIdAndStatusIn(BackupTargetId id, List<FileCopyStatus> statuses) {
        springRepository.deleteByNaturalIdBackupTargetIdAndStatusIn(id.value(), statuses);
    }
}
