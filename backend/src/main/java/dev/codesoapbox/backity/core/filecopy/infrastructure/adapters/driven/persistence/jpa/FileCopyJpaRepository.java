package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.DoNotMutate;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.shared.domain.DomainEvent;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.PageEntityMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.PaginationEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FileCopyJpaRepository implements FileCopyRepository {

    private static final Sort SORT_BY_DATE_CREATED_ASC = Sort.by(Sort.Direction.ASC, "dateCreated");

    private final FileCopySpringRepository springRepository;
    private final PageEntityMapper pageMapper;
    private final PaginationEntityMapper paginationMapper;
    private final DomainEventPublisher domainEventPublisher;

    // @TODO IMPLEMENT AND TEST
    @Transactional
    @Override
    public FileCopy save(FileCopy file) {
//        GameFileJpaEntity entity = entityMapper.toEntity(gameFile);
//        GameFileJpaEntity savedEntity = springRepository.save(entity);
//        schedulePublishingDomainEventsAfterCommit(gameFile);
//
//        return entityMapper.toModel(savedEntity);
        return null;
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

    // @TODO IMPLEMENT AND TEST
    @Override
    public FileCopy getById(FileCopyId id) {
        return null;
    }

    // @TODO IMPLEMENT AND TEST
    @Override
    public Optional<FileCopy> findCurrentlyDownloading() {
//        return springRepository.findByFileCopyStatus(FileBackupStatus.IN_PROGRESS)
//                .map(entityMapper::toModel);
        return Optional.empty();
    }

    // @TODO IMPLEMENT AND TEST
    @Override
    public Optional<FileCopy> findOldestWaitingForDownload() {
        return Optional.empty();
    }

    // @TODO IMPLEMENT AND TEST
    @Override
    public Page<FileCopy> findAllDiscovered(Pagination pagination) {
//        Pageable pageable = paginationMapper.toEntity(pagination, SORT_BY_DATE_CREATED_ASC);
//        org.springframework.data.domain.Page<GameFileJpaEntity> foundPage =
//                springRepository.findAllByFileCopyStatusIn(pageable, List.of(FileBackupStatus.DISCOVERED));
//        return pageMapper.toDomain(foundPage, entityMapper::toModel);
        return null;
    }

    // @TODO IMPLEMENT AND TEST
    @Override
    public Page<FileCopy> findAllWaitingForDownload(Pagination pagination) {
        return null;
    }

    // @TODO IMPLEMENT AND TEST
    @Override
    public Page<FileCopy> findAllProcessed(Pagination pagination) {
//        Pageable pageable = paginationMapper.toEntity(pagination, SORT_BY_DATE_CREATED_ASC);
//        org.springframework.data.domain.Page<GameFileJpaEntity> foundPage =
//                springRepository.findAllByFileCopyStatusIn(pageable,
//                        List.of(FileBackupStatus.SUCCESS, FileBackupStatus.FAILED));
//        return pageMapper.toDomain(foundPage, entityMapper::toModel);
        return null;
    }

    // @TODO IMPLEMENT AND TEST
    @Override
    public List<FileCopy> findAllByGameFileId(GameFileId id) {
        return List.of();
    }

    // @TODO TEST
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
