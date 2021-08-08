package dev.codesoapbox.backity.core.files.discovery.domain.services;

import dev.codesoapbox.backity.core.files.discovery.domain.model.DiscoveredFile;

import java.util.function.Consumer;

public interface SourceFileDiscoveryService {

    String getSource();

    void discoverNewFiles(Consumer<DiscoveredFile> discoveredFileConsumer);
}
