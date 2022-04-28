package dev.codesoapbox.backity.core.files.discovery.domain.repositories;

import dev.codesoapbox.backity.core.files.discovery.domain.model.DiscoveredFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface DiscoveredFileRepository {

    Optional<DiscoveredFile> findByUniqueId(UUID uniqueId);

    Page<DiscoveredFile> findAllUnqueued(Pageable pageable);

    DiscoveredFile save(DiscoveredFile discoveredFile);
}
