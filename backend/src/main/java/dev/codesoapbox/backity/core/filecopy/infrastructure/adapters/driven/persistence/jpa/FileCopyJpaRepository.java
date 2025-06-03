package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.DoNotMutate;
import dev.codesoapbox.backity.core.filecopy.domain.*;
import dev.codesoapbox.backity.core.filecopy.domain.exceptions.FileCopyNotFoundException;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.shared.domain.DomainEvent;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.PageEntityMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.PaginationEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
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
    private final PageEntityMapper pageMapper;
    private final PaginationEntityMapper paginationMapper;
    private final DomainEventPublisher domainEventPublisher;

    @Transactional
    @Override
    public FileCopy save(FileCopy fileCopy) {
        return internalSave(fileCopy);
    }

    private FileCopy internalSave(FileCopy fileCopy) {
        FileCopyJpaEntity entity = entityMapper.toEntity(fileCopy);
        FileCopyJpaEntity savedEntity = springRepository.save(entity);
        schedulePublishingDomainEventsAfterCommit(fileCopy);

        return entityMapper.toDomain(savedEntity);
    }

    private void schedulePublishingDomainEventsAfterCommit(FileCopy fileCopy) {
        // Since we're about to clear the Aggregate's events, we must copy them to reference after committing:
        List<DomainEvent> events = new ArrayList<>(fileCopy.getDomainEvents());

        // Clear the domain events to prevent them from being republished in case of several calls to save():
        fileCopy.clearDomainEvents();

        // Events should only be published after the Aggregate changes have been committed to the database:
        TransactionSynchronizationManager.registerSynchronization(
                new PublishEventsAfterCommitTransactionSynchronization(events));
    }

    @Override
    public FileCopy getById(FileCopyId id) {
        return findByGameFileId(id)
                .orElseThrow(() -> new FileCopyNotFoundException(id));
    }

    private Optional<FileCopy> findByGameFileId(FileCopyId id) {
        return springRepository.findById(id.value())
                .map(entityMapper::toDomain);
    }

    @SuppressWarnings("java:S1166") // No reason to log or rethrow DataIntegrityViolationException
    @Override
    public FileCopy findByNaturalIdOrCreate(FileCopyNaturalId naturalId, Supplier<FileCopy> fileCopyFactory) {
        return springRepository.findByNaturalIdGameFileIdAndNaturalIdBackupTargetId(
                        naturalId.gameFileId().value(), naturalId.backupTargetId().value())
                .map(entityMapper::toDomain)
                .orElseGet(() -> {
                    try {
                        return create(fileCopyFactory);
                    } catch (DataIntegrityViolationException e) {
                        // Someone else just inserted it – reload and return that
                        FileCopyJpaEntity entity =
                                springRepository.getByNaturalIdGameFileIdAndNaturalIdBackupTargetId(
                                        naturalId.gameFileId().value(), naturalId.backupTargetId().value());
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
    public Page<FileCopy> findAllEnqueued(Pagination pagination) {
        return findAllByStatusOrderedByDateModified(pagination, List.of(FileCopyStatus.ENQUEUED));
    }

    private Page<FileCopy> findAllByStatusOrderedByDateModified(Pagination pagination, List<FileCopyStatus> statuses) {
        Pageable pageable = paginationMapper.toEntity(pagination, SORT_BY_DATE_MODIFIED_ASC);
        org.springframework.data.domain.Page<FileCopyJpaEntity> foundPage =
                springRepository.findAllByStatusIn(pageable, statuses);

        return pageMapper.toDomain(foundPage, entityMapper::toDomain);
    }

    @Override
    public List<FileCopy> findAllByGameFileId(GameFileId id) {
        return springRepository.findAllByNaturalIdGameFileId(id.value()).stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @DoNotMutate // Not covered by unit tests
    private class PublishEventsAfterCommitTransactionSynchronization implements TransactionSynchronization {

        private final List<DomainEvent> events;

        public PublishEventsAfterCommitTransactionSynchronization(List<DomainEvent> events) {
            this.events = new ArrayList<>(events);
        }

        @Override
        public void afterCommit() {
            publish();
        }

        private void publish() {
            for (DomainEvent domainEvent : events) {
                domainEventPublisher.publish(domainEvent);
            }
        }
    }
}
