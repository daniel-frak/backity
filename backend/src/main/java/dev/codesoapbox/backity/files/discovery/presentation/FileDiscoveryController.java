package dev.codesoapbox.backity.files.discovery.presentation;

import dev.codesoapbox.backity.files.discovery.domain.model.DiscoveredFile;
import dev.codesoapbox.backity.files.discovery.application.services.FileDiscoveryFacade;
import dev.codesoapbox.backity.shared.presentation.ApiControllerPaths;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "File discovery")
@RestController
@RequestMapping(ApiControllerPaths.API + "discovered-files")
@RequiredArgsConstructor
public class FileDiscoveryController {

    private final FileDiscoveryFacade fileDiscoveryFacade;

    @GetMapping
    public Page<DiscoveredFile> getDiscoveredFiles(Pageable pageable) {
        return fileDiscoveryFacade.getDiscoveredFiles(pageable);
    }

    @GetMapping("discover")
    public void discover() {
        fileDiscoveryFacade.discoverNewFiles();
    }
}
