package dev.codesoapbox.backity.testing.messaging.extensions;


import dev.codesoapbox.backity.testing.async.TrackingTaskExecutor;
import dev.codesoapbox.backity.testing.messaging.inmemory.InMemoryEventScenario;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/// JUnit Jupiter `@Extension` for testing in-memory event listeners.
public class InMemoryEventScenarioExtension implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
                                     ExtensionContext extensionContext) {

        return parameterContext.getParameter().getType().equals(InMemoryEventScenario.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext,
                                   ExtensionContext extensionContext) {

        ApplicationContext ctx = SpringExtension.getApplicationContext(extensionContext);

        return new InMemoryEventScenario(
                ctx,
                ctx.getBean(TrackingTaskExecutor.class)
        );
    }
}