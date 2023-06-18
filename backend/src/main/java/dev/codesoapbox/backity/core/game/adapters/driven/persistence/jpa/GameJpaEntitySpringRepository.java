package dev.codesoapbox.backity.core.game.adapters.driven.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GameJpaEntitySpringRepository extends JpaRepository<GameJpaEntity, UUID> {

    Optional<GameJpaEntity> findByTitle(String title);
}
