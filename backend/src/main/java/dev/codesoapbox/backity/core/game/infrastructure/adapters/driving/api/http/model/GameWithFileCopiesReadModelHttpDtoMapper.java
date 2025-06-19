package dev.codesoapbox.backity.core.game.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgress;
import dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.model.filecopy.FileCopyHttpDto;
import dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.model.filecopy.FileCopyHttpDtoMapper;
import dev.codesoapbox.backity.core.game.application.GameWithFileCopiesAndReplicationProgresses;
import dev.codesoapbox.backity.core.game.application.readmodel.FileCopyReadModel;
import dev.codesoapbox.backity.core.game.application.readmodel.GameFileWithCopiesReadModel;
import dev.codesoapbox.backity.core.game.application.readmodel.GameWithFileCopiesReadModel;
import dev.codesoapbox.backity.core.game.infrastructure.adapters.driving.api.http.model.game.GameIdHttpDtoMapper;
import dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driving.api.http.model.gamefile.GameFileHttpDtoMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.ProgressHttpDto;
import org.mapstruct.*;

import java.util.List;

@SuppressWarnings("java:S1694") // False positive
@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE,
        uses = {GameFileHttpDtoMapper.class, GameIdHttpDtoMapper.class, FileCopyHttpDtoMapper.class})
public abstract class GameWithFileCopiesReadModelHttpDtoMapper {

    public GameWithFileCopiesHttpDto toDto(GameWithFileCopiesAndReplicationProgresses model) {
        return toDto(model.gameWithFileCopies(), model.replicationProgresses());
    }

    protected abstract GameWithFileCopiesHttpDto toDto(GameWithFileCopiesReadModel model,
                                                    @Context List<FileCopyReplicationProgress> replicationProgresses);

    @Mapping(target = "fileCopiesWithProgress", source = "fileCopies")
    protected abstract GameFileWithCopiesHttpDto toDto(
            GameFileWithCopiesReadModel model, @Context List<FileCopyReplicationProgress> replicationProgresses);

    @Mapping(target = "fileCopy", source = ".")
    @Mapping(target = "progress", expression = "java( mapProgress(fileCopy, replicationProgresses) )")
    protected abstract FileCopyWithProgressHttpDto toDto(
            FileCopyReadModel model, @Context List<FileCopyReplicationProgress> replicationProgresses);

    @Mapping(target = "timeLeftSeconds", source = "timeLeft.seconds")
    protected abstract ProgressHttpDto toDto(FileCopyReplicationProgress domain);

    @Named("mapProgress")
    protected ProgressHttpDto mapProgress(FileCopyHttpDto fileCopyDto,
                                          @Context List<FileCopyReplicationProgress> progresses) {
        return progresses.stream()
                .filter(p -> p.fileCopyId().value().toString().equals(fileCopyDto.id()))
                .findFirst()
                .map(this::toDto)
                .orElse(null);
    }
}
