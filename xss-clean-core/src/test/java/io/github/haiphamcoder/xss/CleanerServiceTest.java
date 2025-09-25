package io.github.haiphamcoder.xss;

import org.junit.jupiter.api.Test;

import io.github.haiphamcoder.xss.policy.JsoupCleanerService;
import io.github.haiphamcoder.xss.policy.OwaspCleanerService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CleanerServiceTest {

    @Test
    void testOwaspCleanerService() {
        CleanerService cleaner = new OwaspCleanerService();
        String raw = "<script>alert('XSS')</script><b>Hello</b>";
        String cleaned = cleaner.clean(raw);

        assertFalse(cleaned.contains("<script>"));
        assertTrue(cleaned.contains("Hello"));
    }

    @Test
    void testJsoupCleanerService() {
        CleanerService cleaner = new JsoupCleanerService();
        String raw = "<img src=x onerror=alert(1)><i>OK</i>";
        String cleaned = cleaner.clean(raw);

        assertEquals("OK", cleaned.replaceAll("<.*?>", ""));
    }

    @Test
    void testCleanObject() {
        CleanerService cleaner = new OwaspCleanerService();
        
        class TestObject {
            String name = "<script>alert('XSS')</script><b>Alice</b>";
            String email = "<script>alert('XSS')</script><b>alice@example.com</b>";
            String bio = "<script>alert('XSS')</script><b>Alice is a software engineer</b>";
        }

        TestObject testObject = new TestObject();
        cleaner.cleanObject(testObject);

        assertTrue(testObject.name.contains("Alice"));
        assertFalse(testObject.bio.contains("<script>"));
        assertTrue(testObject.email.contains("alice") && testObject.email.contains("example.com"));
    }
}
