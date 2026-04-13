package dev.codesoapbox.backity.testing.messaging.extensions;


import dev.codesoapbox.backity.testing.async.TrackingTaskExecutor;
import dev.codesoapbox.backity.testing.messaging.outbox.OutboxEventScenario;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.springframework.context.ApplicationContext;
import org.springframework.modulith.events.core.EventSerializer;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;

/// JUnit Jupiter `@Extension` for testing outbox event listeners.
public class OutboxEventScenarioExtension implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
                                     ExtensionContext extensionContext) {

        return parameterContext.getParameter().getType().equals(OutboxEventScenario.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext,
                                   ExtensionContext extensionContext) {

        ApplicationContext ctx = SpringExtension.getApplicationContext(extensionContext);

        return new OutboxEventScenario(
                ctx,
                ctx.getBean(PlatformTransactionManager.class),
                ctx.getBean(EventSerializer.class),
                ctx.getBean(TrackingTaskExecutor.class)
        );
    }
}