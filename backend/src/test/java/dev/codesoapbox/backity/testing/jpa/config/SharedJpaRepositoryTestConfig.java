package dev.codesoapbox.backity.testing.jpa.config;

import dev.codesoapbox.backity.core.game.config.GameJpaRepositoryBeanConfig;
import dev.codesoapbox.backity.core.gamefile.config.GameFileJpaRepositoryBeanConfig;
import dev.codesoapbox.backity.infrastructure.config.jpa.SharedJpaRepositoryBeanConfig;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import dev.codesoapbox.backity.testing.TemporaryMockBean;
import dev.codesoapbox.backity.testing.time.config.FakeTimeBeanConfig;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

/**
 * <h1>Motivation for the class</h1>
 * <p>
 * If we don't provide exactly the same configuration to every repository test, the Spring Boot application will not get
 * reused, creating a small army of cached contexts, all with open DB connections. At some point, these cached
 * connections will overwhelm the database, making it return an exception:
 * <p>
 * "PSQLException: FATAL: sorry, too many clients already"
 */
@TestConfiguration
@Import({
        // Common
        SharedJpaRepositoryBeanConfig.class,
        FakeTimeBeanConfig.class,

        // Specific
        GameJpaRepositoryBeanConfig.class,
        GameFileJpaRepositoryBeanConfig.class
})
@TemporaryMockBean(DomainEventPublisher.class)
public class SharedJpaRepositoryTestConfig {
}
