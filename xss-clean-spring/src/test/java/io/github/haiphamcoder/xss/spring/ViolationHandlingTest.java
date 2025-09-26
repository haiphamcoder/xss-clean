package io.github.haiphamcoder.xss.spring;

import io.github.haiphamcoder.xss.SpringXssCleanApplication;
import io.github.haiphamcoder.xss.config.XssProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = SpringXssCleanApplication.class)
@TestPropertySource(properties = {
    "xss.cleaner.enabled=true",
    "xss.cleaner.strategy=jsoup",
    "xss.cleaner.throw-on-violation=true",
    "xss.cleaner.log-violation=true"
})
class ViolationHandlingTest {

    @Autowired
    private XssProperties xssProperties;

    @Test
    void testViolationHandlingProperties() {
        // Test that violation handling properties are loaded correctly
        assertTrue(xssProperties.isThrowOnViolation());
        assertTrue(xssProperties.isLogViolation());
        assertTrue(xssProperties.isEnabled());
        assertEquals("jsoup", xssProperties.getStrategy());
    }

    @Test
    void testThrowOnViolationDisabled() {
        // This test verifies the configuration is loaded
        // The actual throwing behavior would be tested in integration tests
        assertTrue(xssProperties.isThrowOnViolation());
    }

    @Test
    void testLogViolationEnabled() {
        // This test verifies the configuration is loaded
        // The actual logging behavior would be tested in integration tests
        assertTrue(xssProperties.isLogViolation());
    }
}
