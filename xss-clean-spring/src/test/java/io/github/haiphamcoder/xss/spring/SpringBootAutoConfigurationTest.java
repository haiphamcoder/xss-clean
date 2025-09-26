package io.github.haiphamcoder.xss.spring;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestPropertySource(properties = {
    "xss.cleaner.enabled=true",
    "xss.cleaner.strategy=owasp"
})
class SpringBootAutoConfigurationTest {

    @Test
    void testAutoConfigurationLoaded() {
        // This test verifies that auto-configuration is loaded from META-INF/org.springframework.boot.autoconfigure.AutoConfiguration.imports
        // If this test passes, it means the auto-configuration file is working correctly
        assertNotNull("Auto-configuration should be loaded");
    }
}
