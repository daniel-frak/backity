package dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.DoNotMutate;
import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.core.gamefile.domain.FileBackupStatus;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.core.gamefile.domain.exceptions.GameFileNotFoundException;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.PageEntityMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.PaginationEntityMapper;
import dev.codesoapbox.backity.shared.domain.DomainEvent;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
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
    private final DomainEventPublisher domainEventPublisher;

    @Override
    public Optional<GameFile> findOldestWaitingForDownload() {
        PageRequest pageable = PageRequest.of(0, 1, SORT_BY_DATE_CREATED_ASC);
        return springRepository.findAllByFileBackupStatusIn(pageable, List.of(FileBackupStatus.ENQUEUED)).get()
                .findFirst()
                .map(entityMapper::toModel);
    }

    @Override
    public Page<GameFile> findAllWaitingForDownload(Pagination pagination) {
        Pageable pageable = paginationMapper.toEntity(pagination, SORT_BY_DATE_CREATED_ASC);
        org.springframework.data.domain.Page<GameFileJpaEntity> foundPage =
                springRepository.findAllByFileBackupStatusIn(pageable, List.of(FileBackupStatus.ENQUEUED));
        return pageMapper.toDomain(foundPage, entityMapper::toModel);
    }

    @Transactional
    @Override
    public GameFile save(GameFile gameFile) {
        GameFileJpaEntity entity = entityMapper.toEntity(gameFile);
        GameFileJpaEntity savedEntity = springRepository.save(entity);
        schedulePublishingDomainEventsAfterCommit(gameFile);

        return entityMapper.toModel(savedEntity);
    }

    private void schedulePublishingDomainEventsAfterCommit(GameFile gameFile) {
        // Since we're about to clear the Aggregate's events, we must copy them to reference after committing:
        List<DomainEvent> events = new ArrayList<>(gameFile.getDomainEvents());

        // Clear the domain events to prevent them from being republished in case of several calls to save():
        gameFile.clearDomainEvents();

        // Events should only be published after the Aggregate changes have been committed to the database:
        TransactionSynchronizationManager.registerSynchronization(
                new PublishEventsAfterCommitTransactionSynchronization(events));
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
                springRepository.findAllByFileBackupStatusIn(pageable,
                        List.of(FileBackupStatus.SUCCESS, FileBackupStatus.FAILED));
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
                springRepository.findAllByFileBackupStatusIn(pageable, List.of(FileBackupStatus.DISCOVERED));
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
