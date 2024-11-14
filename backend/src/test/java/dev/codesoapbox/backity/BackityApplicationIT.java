package dev.codesoapbox.backity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureTestDatabase
class BackityApplicationIT {

    @SuppressWarnings("squid:S2699")
    @Test
    void contextLoads() {
        // Intentionally empty - we're only testing that the application starts up
    }
}
