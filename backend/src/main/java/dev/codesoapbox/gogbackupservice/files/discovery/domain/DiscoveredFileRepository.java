package dev.codesoapbox.gogbackupservice.files.discovery.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface DiscoveredFileRepository {

    Optional<DiscoveredFile> findByUniqueId(UUID uniqueId);

    Page<DiscoveredFile> findAll(Pageable pageable);
}
