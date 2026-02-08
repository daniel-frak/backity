package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.domain.DiscoveredFile;
import dev.codesoapbox.backity.core.sourcefile.domain.FileSize;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogFile;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogGameWithFiles;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public abstract class GogGameWithFilesMapper {

    protected static final GameProviderId GAME_PROVIDER_ID = GogGameProviderId.get();

    public List<DiscoveredFile> toDiscoveredFiles(GogGameWithFiles gogGame) {
        return gogGame.files().stream()
                .map(sourceFile -> toDiscoveredFile(gogGame, sourceFile))
                .toList();
    }

    @Mapping(target = "gameProviderId", expression = "java(GAME_PROVIDER_ID)")
    @Mapping(target = "originalGameTitle", source = "gogGame.title")
    @Mapping(target = "fileTitle", source = "sourceFile.fileTitle")
    @Mapping(target = "version", source = "sourceFile.version")
    @Mapping(target = "url", source = "sourceFile.manualUrl")
    @Mapping(target = "originalFileName", source = "sourceFile.fileName")
    @Mapping(target = "size", source = "sourceFile.size")
    protected abstract DiscoveredFile toDiscoveredFile(GogGameWithFiles gogGame, GogFile sourceFile);

    protected FileSize toFileSize(String value) {
        return FileSize.fromString(value);
    }
}
