package dev.codesoapbox.gogbackupservice.files.discovery.domain.services;

import dev.codesoapbox.gogbackupservice.files.discovery.domain.model.DiscoveredFile;

import java.util.function.Consumer;

public interface SourceFileDiscoveryService {

    String getSource();

    void discoverNewFiles(Consumer<DiscoveredFile> discoveredFileConsumer);
}
