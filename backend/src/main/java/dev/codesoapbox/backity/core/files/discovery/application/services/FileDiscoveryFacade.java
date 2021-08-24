package dev.codesoapbox.backity.core.files.discovery.application.services;

import dev.codesoapbox.backity.core.files.discovery.domain.model.DiscoveredFile;
import dev.codesoapbox.backity.core.files.discovery.application.messages.FileDiscoveryStatus;
import dev.codesoapbox.backity.core.files.discovery.domain.repositories.DiscoveredFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FileDiscoveryFacade {

    private final FileDiscoveryService fileDiscoveryService;
    private final DiscoveredFileRepository repository;

    public Page<DiscoveredFile> getDiscoveredFiles(Pageable pageable) {
        return repository.findAllUnqueued(pageable);
    }

    public void discoverNewFiles() {
        fileDiscoveryService.discoverNewFiles();
    }

    public List<FileDiscoveryStatus> getStatuses() {
        return fileDiscoveryService.getStatuses();
    }
}
