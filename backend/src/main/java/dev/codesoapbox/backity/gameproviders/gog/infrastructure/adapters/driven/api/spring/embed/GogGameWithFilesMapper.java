package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.domain.DiscoveredFile;
import dev.codesoapbox.backity.core.gamefile.domain.FileSize;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogGameFile;
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
                .map(gameFile -> toFileSource(gogGame, gameFile))
                .toList();
    }

    @Mapping(target = "gameProviderId", expression = "java(GAME_PROVIDER_ID)")
    @Mapping(target = "originalGameTitle", source = "gogGame.title")
    @Mapping(target = "fileTitle", source = "gameFile.fileTitle")
    @Mapping(target = "version", source = "gameFile.version")
    @Mapping(target = "url", source = "gameFile.manualUrl")
    @Mapping(target = "originalFileName", source = "gameFile.fileName")
    @Mapping(target = "size", source = "gameFile.size")
    protected abstract DiscoveredFile toFileSource(GogGameWithFiles gogGame, GogGameFile gameFile);

    protected FileSize toFileSize(String value) {
        return FileSize.fromString(value);
    }
}
