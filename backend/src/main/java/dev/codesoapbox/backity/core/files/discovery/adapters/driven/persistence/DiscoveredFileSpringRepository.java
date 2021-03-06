package dev.codesoapbox.backity.core.files.discovery.adapters.driven.persistence;

import dev.codesoapbox.backity.core.files.discovery.domain.model.DiscoveredFile;
import dev.codesoapbox.backity.core.files.discovery.domain.model.DiscoveredFileId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DiscoveredFileSpringRepository extends JpaRepository<DiscoveredFile, DiscoveredFileId> {

    Optional<DiscoveredFile> findByUniqueId(UUID uniqueId);

    Page<DiscoveredFile> findAllByEnqueuedFalse(Pageable pageable);
}
