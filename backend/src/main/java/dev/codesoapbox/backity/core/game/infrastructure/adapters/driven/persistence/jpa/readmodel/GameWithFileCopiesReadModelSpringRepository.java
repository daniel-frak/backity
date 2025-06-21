package dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa.readmodel;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface GameWithFileCopiesReadModelSpringRepository
        extends JpaRepository<GameWithFileCopiesReadModelJpaEntity, UUID>,
        JpaSpecificationExecutor<GameWithFileCopiesReadModelJpaEntity> {

    @EntityGraph("gameFilesWithCopies")
    Page<GameWithFileCopiesReadModelJpaEntity> findAll(
            Specification<GameWithFileCopiesReadModelJpaEntity> spec, Pageable pageable);

    // Necessary to solve MultipleBagFetchException
    @Query("""
                SELECT gf FROM GameFileWithCopiesReadModelJpaEntity gf
                JOIN FETCH gf.fileCopies
                WHERE gf.id IN :gameFileIds
            """)
    List<GameFileWithCopiesReadModelJpaEntity> fetchFileCopies(List<UUID> gameFileIds);
}
