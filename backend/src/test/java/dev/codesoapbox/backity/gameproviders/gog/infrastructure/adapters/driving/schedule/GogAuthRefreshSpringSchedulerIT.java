package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.schedule;

import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.auth.GogAuthSpringService;
import dev.codesoapbox.backity.testing.scheduling.RegisteredSchedulers;
import dev.codesoapbox.backity.testing.scheduling.ScheduledMethod;
import dev.codesoapbox.backity.testing.scheduling.annotations.SpringSchedulerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@SpringSchedulerTest
class GogAuthRefreshSpringSchedulerIT {

    private static final ScheduledMethod SCHEDULED_METHOD =
            new ScheduledMethod(GogAuthRefreshSpringScheduler.class, "execute");

    @Autowired
    private GogAuthSpringService gogAuthSpringService;

    @Autowired
    private RegisteredSchedulers registeredSchedulers;

    @Test
    void shouldBeRegisteredAsScheduled() {
        assertThat(registeredSchedulers.scheduledMethods())
                .contains(SCHEDULED_METHOD);
    }

    @Test
    void shouldExecute() {
        registeredSchedulers.execute(SCHEDULED_METHOD);

        verify(gogAuthSpringService).refreshAccessTokenIfNeeded();
    }
}