package dev.codesoapbox.gogbackupservice.files.discovery.domain;

import dev.codesoapbox.gogbackupservice.files.discovery.application.FileDiscoveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

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
