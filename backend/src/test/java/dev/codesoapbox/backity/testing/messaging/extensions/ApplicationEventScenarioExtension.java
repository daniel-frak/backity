package dev.codesoapbox.backity.testing.messaging.extensions;


import dev.codesoapbox.backity.testing.messaging.application.ApplicationEventScenario;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

/// JUnit Jupiter `@Extension` for testing application event listeners.
public class ApplicationEventScenarioExtension implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
                                     ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().equals(ApplicationEventScenario.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext,
                                   ExtensionContext extensionContext) {
        return new ApplicationEventScenario();
    }
}