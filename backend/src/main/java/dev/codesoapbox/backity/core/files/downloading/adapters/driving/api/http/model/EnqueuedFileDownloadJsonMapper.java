package dev.codesoapbox.backity.core.files.downloading.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.files.downloading.domain.model.EnqueuedFileDownload;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EnqueuedFileDownloadJsonMapper {

    EnqueuedFileDownloadJsonMapper INSTANCE = Mappers.getMapper(EnqueuedFileDownloadJsonMapper.class);

    EnqueuedFileDownloadJson toJson(EnqueuedFileDownload domain);
}
