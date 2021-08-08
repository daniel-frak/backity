package dev.codesoapbox.gogbackupservice.files.discovery.infrastructure.repositories;

import dev.codesoapbox.gogbackupservice.files.discovery.domain.model.DiscoveredFile;
import dev.codesoapbox.gogbackupservice.files.discovery.domain.model.DiscoveredFileId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DiscoveredFileSpringRepository extends JpaRepository<DiscoveredFile, DiscoveredFileId> {

    Optional<DiscoveredFile> findByUniqueId(UUID uniqueId);
}
