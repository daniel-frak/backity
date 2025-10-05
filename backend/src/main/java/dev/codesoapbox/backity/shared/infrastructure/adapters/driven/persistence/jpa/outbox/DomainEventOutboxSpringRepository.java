package dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.outbox;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DomainEventOutboxSpringRepository extends JpaRepository<OutboxEventEntity, UUID> {

    Page<OutboxEventEntity> findAllByProcessedFalse(Pageable pageable);
}
