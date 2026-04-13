package dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.spring;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.core.EventSerializer;
import tools.jackson.databind.json.JsonMapper;

import java.util.function.Supplier;

// @TODO Test
@RequiredArgsConstructor
public class CustomEventSerializer implements EventSerializer {

    @NonNull
    private final Supplier<JsonMapper> mapper;

    // @TODO START HERE - Write custom ser/des based on strategy pattern,
    //       perhaps with annotation like @OutboxEvent(for=OriginalDomainEvent.class).
    @Override
    public Object serialize(Object event) {
        return mapper.get().writeValueAsString(event);
    }

    @Override
    public <T> T deserialize(Object serialized, Class<T> type) {
        return mapper.get().readerFor(type).readValue(serialized.toString());
    }
}
