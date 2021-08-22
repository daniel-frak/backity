package dev.codesoapbox.backity.core.files.discovery.infrastructure.repositories;

import dev.codesoapbox.backity.core.files.discovery.domain.model.DiscoveredFile;
import dev.codesoapbox.backity.core.files.discovery.domain.repositories.DiscoveredFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class DiscoveredFileJpaRepository implements DiscoveredFileRepository {

    private final DiscoveredFileSpringRepository springRepository;

    @Override
    public Optional<DiscoveredFile> findByUniqueId(UUID uniqueId) {
        return springRepository.findByUniqueId(uniqueId);
    }

    @Override
    public Page<DiscoveredFile> findAllUnqueued(Pageable pageable) {
        return springRepository.findAllByEnqueuedFalse(pageable);
    }

    @Override
    public DiscoveredFile save(DiscoveredFile discoveredFile) {
        return springRepository.save(discoveredFile);
    }
}
