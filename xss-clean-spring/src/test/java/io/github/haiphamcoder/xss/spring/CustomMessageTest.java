package io.github.haiphamcoder.xss.spring;

import io.github.haiphamcoder.xss.SpringXssCleanApplication;
import io.github.haiphamcoder.xss.annotation.NoXss;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = SpringXssCleanApplication.class)
@TestPropertySource(properties = {"xss.cleaner.enabled=true"})
class CustomMessageTest {

    @Autowired
    Validator validator;

    @Test
    void testCustomMessageWhenValidationFails() {
        // Test that custom message is returned when validation fails
        TestUser user = new TestUser();
        user.name = "<script>alert('XSS')</script>John"; // This should fail validation
        user.email = "john@example.com";

        Set<ConstraintViolation<TestUser>> violations = validator.validate(user);
        
        // Should have 1 violation
        assertEquals(1, violations.size());
        
        // Check that the custom message is used
        ConstraintViolation<TestUser> violation = violations.iterator().next();
        assertEquals("Custom XSS message", violation.getMessage());
        assertEquals("name", violation.getPropertyPath().toString());
    }

    @Test
    void testDefaultMessageWhenNoCustomMessage() {
        // Test that default message is used when no custom message is provided
        TestUserWithDefaultMessage user = new TestUserWithDefaultMessage();
        user.name = "<script>alert('XSS')</script>John"; // This should fail validation
        user.email = "john@example.com";

        Set<ConstraintViolation<TestUserWithDefaultMessage>> violations = validator.validate(user);
        
        // Should have 1 violation
        assertEquals(1, violations.size());
        
        // Check that the default message is used
        ConstraintViolation<TestUserWithDefaultMessage> violation = violations.iterator().next();
        assertEquals("Invalid content (XSS detected)", violation.getMessage());
        assertEquals("name", violation.getPropertyPath().toString());
    }

    @Test
    void testValidationPassesWithCleanContent() {
        // Test that validation passes with clean content
        TestUser user = new TestUser();
        user.name = "John Doe"; // Clean content
        user.email = "john@example.com";

        Set<ConstraintViolation<TestUser>> violations = validator.validate(user);
        
        // Should have no violations
        assertTrue(violations.isEmpty());
    }

    // Test helper classes
    static class TestUser {
        @NoXss(message = "Custom XSS message")
        public String name;

        public String email;
    }

    static class TestUserWithDefaultMessage {
        @NoXss
        public String name;

        public String email;
    }
}
