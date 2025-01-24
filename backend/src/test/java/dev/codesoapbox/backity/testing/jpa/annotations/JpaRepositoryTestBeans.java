package dev.codesoapbox.backity.testing.jpa.annotations;

import dev.codesoapbox.backity.core.game.config.GameJpaRepositoryBeanConfig;
import dev.codesoapbox.backity.core.gamefile.config.GameFileJpaRepositoryBeanConfig;
import dev.codesoapbox.backity.infrastructure.config.jpa.SharedJpaRepositoryBeanConfig;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import dev.codesoapbox.backity.testing.time.config.FakeTimeBeanConfig;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({
        // Common
        SharedJpaRepositoryBeanConfig.class,
        FakeTimeBeanConfig.class,

        // Specific
        GameJpaRepositoryBeanConfig.class,
        GameFileJpaRepositoryBeanConfig.class
})
@MockitoBean(types = DomainEventPublisher.class)
public @interface JpaRepositoryTestBeans {
}
