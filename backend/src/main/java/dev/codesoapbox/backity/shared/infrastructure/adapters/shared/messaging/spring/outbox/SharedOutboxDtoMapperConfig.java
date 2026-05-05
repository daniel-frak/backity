package dev.codesoapbox.backity.shared.infrastructure.adapters.shared.messaging.spring.outbox;

import org.mapstruct.MapperConfig;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@MapperConfig(
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface SharedOutboxDtoMapperConfig {
}
