package dev.codesoapbox.backity.core.files.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.files.adapters.driving.api.http.model.GameFileDetailsHttpDto;
import dev.codesoapbox.backity.core.files.adapters.driving.api.http.model.GameFileDetailsHttpDtoMapper;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetailsId;
import dev.codesoapbox.backity.core.files.domain.backup.repositories.GameFileDetailsRepository;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.PageHttpDto;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.PageHttpDtoMapper;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.PaginationHttpDto;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.PaginationHttpDtoMapper;
import dev.codesoapbox.backity.core.shared.domain.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@Tag(name = "Backups", description = "Everything to do with downloading discovered files")
@RestController
@RequestMapping("backups")
@RequiredArgsConstructor
@Slf4j
public class FileBackupController {

    private final GameFileDetailsRepository gameFileDetailsRepository;
    private final GameFileDetailsHttpDtoMapper gameFileDetailsMapper;
    private final PaginationHttpDtoMapper paginationMapper;
    private final PageHttpDtoMapper pageMapper;

    @Operation(summary = "List queue items", description = "Returns the file currently being downloaded")
    @GetMapping("current")
    public GameFileDetailsHttpDto getCurrentlyProcessing() {
        return gameFileDetailsRepository.findCurrentlyDownloading()
                .map(GameFileDetailsHttpDtoMapper.INSTANCE::toDto)
                .orElse(null);
    }

    @Operation(summary = "List queue items", description = "Returns a paginated list of all downloads in the queue")
    @GetMapping("queue")
    public PageHttpDto<GameFileDetailsHttpDto> getQueueItems(PaginationHttpDto pagination) {
        Page<GameFileDetails> foundPage =
                gameFileDetailsRepository.findAllWaitingForDownload(paginationMapper.toModel(pagination));
        return pageMapper.toDto(foundPage, gameFileDetailsMapper::toDto);
    }

    @Operation(summary = "List queue items",
            description = "Returns a paginated list of all processed files (downloaded or failed)")
    @GetMapping("processed")
    public PageHttpDto<GameFileDetailsHttpDto> getProcessedFiles(PaginationHttpDto pagination) {
        Page<GameFileDetails> foundPage =
                gameFileDetailsRepository.findAllProcessed(paginationMapper.toModel(pagination));
        return pageMapper.toDto(foundPage, gameFileDetailsMapper::toDto);
    }

    // @TODO Should be POST
    @Operation(summary = "Enqueue file", description = "Adds a discovered file to the download queue")
    @GetMapping("enqueue/{gameFileVersionId}")
    public ResponseEntity<Void> download(@PathVariable String gameFileVersionId) {
        // @TODO Return illegal argument if can't construct id
        Optional<GameFileDetails> gameFileVersionBackup = gameFileDetailsRepository
                .findById(new GameFileDetailsId(UUID.fromString(gameFileVersionId)));

        if (gameFileVersionBackup.isPresent()) {
            gameFileVersionBackup.get().enqueue();
            gameFileDetailsRepository.save(gameFileVersionBackup.get());
            return ResponseEntity.ok().build();
        }

        log.warn("Could not enqueue file. Game file version not found: " + gameFileVersionId);
        return ResponseEntity.badRequest().build();
    }
}
