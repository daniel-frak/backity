package dev.codesoapbox.backity.testing.messaging.extensions;

import dev.codesoapbox.backity.testing.messaging.TestMessageChannel;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class WebSocketEventListenerTestExtension implements BeforeAllCallback, AfterEachCallback {

    private TestMessageChannel testMessageChannel;

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        ApplicationContext springContext = SpringExtension.getApplicationContext(extensionContext);
        testMessageChannel = springContext.getBean(TestMessageChannel.class);
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) {
        testMessageChannel.clearMessages();
    }
}
