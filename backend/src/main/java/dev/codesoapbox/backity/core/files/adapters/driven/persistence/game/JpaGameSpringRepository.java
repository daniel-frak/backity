package dev.codesoapbox.backity.core.files.adapters.driven.persistence.game;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaGameSpringRepository extends JpaRepository<JpaGame, UUID> {

    Optional<JpaGame> findByTitle(String title);
}
