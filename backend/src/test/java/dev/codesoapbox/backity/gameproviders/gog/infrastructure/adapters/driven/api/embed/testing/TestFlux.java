package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.embed.testing;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicInteger;

@Accessors(fluent = true)
@RequiredArgsConstructor
public class TestFlux<T> extends Flux<T> {

    private final Flux<T> innerFlux;

    @Getter
    private final String data;

    private final AtomicInteger attemptsTracker;

    @Override
    public void subscribe(CoreSubscriber<? super T> coreSubscriber) {
        innerFlux.subscribe(coreSubscriber);
    }

    public int writeAttempts() {
        return attemptsTracker.get();
    }
}
