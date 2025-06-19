package dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa.readmodel;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GameWithFileCopiesReadModelSpringRepository
        extends JpaRepository<GameWithFileCopiesReadModelJpaEntity, UUID> {
}
