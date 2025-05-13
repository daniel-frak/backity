package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.embed.model.remote;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GogGameDetailsApiResponse(
        String title,
        String backgroundImage,
        String cdKey,
        String textInformation,
        List<List<Object>> downloads,
        List<Object> extras,
        String changelog
) {
}
