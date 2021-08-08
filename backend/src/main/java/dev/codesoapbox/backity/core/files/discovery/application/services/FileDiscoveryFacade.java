package dev.codesoapbox.backity.core.files.discovery.application.services;

import dev.codesoapbox.backity.core.files.discovery.domain.model.DiscoveredFile;
import dev.codesoapbox.backity.core.files.discovery.domain.repositories.DiscoveredFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileDiscoveryFacade {

    private final FileDiscoveryService fileDiscoveryService;
    private final DiscoveredFileRepository repository;

    public Page<DiscoveredFile> getDiscoveredFiles(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public void discoverNewFiles() {
        fileDiscoveryService.discoverNewFiles();
    }
}
