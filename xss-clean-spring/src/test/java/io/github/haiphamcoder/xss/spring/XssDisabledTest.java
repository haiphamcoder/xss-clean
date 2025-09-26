package io.github.haiphamcoder.xss.spring;

import io.github.haiphamcoder.xss.annotation.NoXss;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestPropertySource(properties = {
    "xss.enabled=false"
})
class XssDisabledTest {

    @Autowired
    private Validator validator;

    @Test
    void testNoXssValidationWhenDisabled() {
        // Test that @NoXss validation is skipped when xss.enabled=false
        TestUser user = new TestUser();
        user.name = "<script>alert('XSS')</script>John"; // This should pass when XSS is disabled
        user.email = "john@example.com";

        Set<ConstraintViolation<TestUser>> violations = validator.validate(user);
        // Should pass because XSS validation is disabled
        assertTrue(violations.isEmpty());
    }

    // Test helper class
    static class TestUser {
        @NoXss
        public String name;
        
        public String email;
    }
}
