package dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.gamefile.domain.FileBackupStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameFileJpaEntitySpringRepository extends JpaRepository<GameFileJpaEntity, UUID> {

    Optional<GameFileJpaEntity> findByFileBackupStatus(FileBackupStatus status);

    boolean existsByFileSourceUrlAndFileSourceVersion(String url, String version);

    Page<GameFileJpaEntity> findAllByFileBackupStatusIn(Pageable pageable, List<FileBackupStatus> status);

    List<GameFileJpaEntity> findAllByGameId(UUID gameId);
}
