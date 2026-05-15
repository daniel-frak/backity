package dev.codesoapbox.backity.testing.jpa.extensions;

import org.junit.jupiter.api.extension.*;
import org.springframework.context.ApplicationContext;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class EntityAuditControlExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    private static final String STORE_KEY = "entityAuditControl";

    private ExtensionContext.Store getStore(ExtensionContext context) {
        return context.getStore(
                ExtensionContext.Namespace.create(EntityAuditControlExtension.class, context.getUniqueId()));
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        ApplicationContext appContext = SpringExtension.getApplicationContext(context);
        AuditingHandler auditingHandler = appContext.getBean(AuditingHandler.class);
        getStore(context).put(STORE_KEY, new EntityAuditControl(auditingHandler));
    }

    @Override
    public void afterEach(ExtensionContext context) {
        getStore(context).get(STORE_KEY, EntityAuditControl.class).enable();
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return EntityAuditControl.class.equals(parameterContext.getParameter().getType());
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return getStore(extensionContext).get(STORE_KEY, EntityAuditControl.class);
    }
}