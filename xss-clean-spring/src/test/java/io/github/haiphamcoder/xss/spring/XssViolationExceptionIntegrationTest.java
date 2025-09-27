package io.github.haiphamcoder.xss.spring;

import io.github.haiphamcoder.xss.CleanerService;
import io.github.haiphamcoder.xss.config.XssProperties;
import io.github.haiphamcoder.xss.exception.XssViolationException;
import io.github.haiphamcoder.xss.policy.OwaspCleanerService;
import io.github.haiphamcoder.xss.web.XssRequestWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;

class XssViolationExceptionIntegrationTest {

    @Test
    void testXssViolationExceptionThrown() {
        // Setup
        XssProperties properties = new XssProperties();
        properties.setThrowOnViolation(true);
        properties.setLogViolation(true);
        
        CleanerService cleaner = new OwaspCleanerService();
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("content", "<script>alert('XSS')</script>Hello");
        
        XssRequestWrapper wrapper = new XssRequestWrapper(request, cleaner, properties);
        
        // Test that exception is thrown when XSS is detected
        XssViolationException exception = assertThrows(XssViolationException.class, () -> {
            wrapper.getParameter("content");
        });
        
        // Verify exception details
        assertTrue(exception.hasDetailedInfo());
        assertEquals("parameter[content]", exception.getContext());
        assertTrue(exception.getOriginalValue().contains("<script>"));
        assertFalse(exception.getCleanedValue().contains("<script>"));
    }

    @Test
    void testNoExceptionWhenThrowOnViolationDisabled() {
        // Setup
        XssProperties properties = new XssProperties();
        properties.setThrowOnViolation(false);
        properties.setLogViolation(true);
        
        CleanerService cleaner = new OwaspCleanerService();
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("content", "<script>alert('XSS')</script>Hello");
        
        XssRequestWrapper wrapper = new XssRequestWrapper(request, cleaner, properties);
        
        // Test that no exception is thrown when throw-on-violation is disabled
        assertDoesNotThrow(() -> {
            String result = wrapper.getParameter("content");
            assertNotNull(result);
            assertFalse(result.contains("<script>"));
        });
    }

    @Test
    void testExceptionWithMultipleParameters() {
        // Setup
        XssProperties properties = new XssProperties();
        properties.setThrowOnViolation(true);
        properties.setLogViolation(true);
        
        CleanerService cleaner = new OwaspCleanerService();
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("name", "John");
        request.addParameter("content", "<script>alert('XSS')</script>Hello");
        request.addParameter("description", "Normal text");
        
        XssRequestWrapper wrapper = new XssRequestWrapper(request, cleaner, properties);
        
        // Test that exception is thrown for the parameter with XSS
        XssViolationException exception = assertThrows(XssViolationException.class, () -> {
            wrapper.getParameter("content");
        });
        
        assertEquals("parameter[content]", exception.getContext());
        
        // Test that other parameters work fine
        assertDoesNotThrow(() -> {
            assertEquals("John", wrapper.getParameter("name"));
            assertEquals("Normal text", wrapper.getParameter("description"));
        });
    }

    @Test
    void testExceptionWithHeaders() {
        // Setup
        XssProperties properties = new XssProperties();
        properties.setThrowOnViolation(true);
        properties.setLogViolation(true);
        
        CleanerService cleaner = new OwaspCleanerService();
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("user-agent", "Mozilla/5.0");
        request.addHeader("x-custom", "<script>alert('XSS')</script>");
        
        XssRequestWrapper wrapper = new XssRequestWrapper(request, cleaner, properties);
        
        // Test that exception is thrown for the header with XSS
        XssViolationException exception = assertThrows(XssViolationException.class, () -> {
            wrapper.getHeader("x-custom");
        });
        
        assertEquals("header[x-custom]", exception.getContext());
        
        // Test that other headers work fine
        assertDoesNotThrow(() -> {
            assertEquals("Mozilla/5.0", wrapper.getHeader("user-agent"));
        });
    }

    @Test
    void testExceptionWithParameterValues() {
        // Setup
        XssProperties properties = new XssProperties();
        properties.setThrowOnViolation(true);
        properties.setLogViolation(true);
        
        CleanerService cleaner = new OwaspCleanerService();
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("tags", "normal");
        request.addParameter("tags", "<script>alert('XSS')</script>");
        request.addParameter("tags", "another");
        
        XssRequestWrapper wrapper = new XssRequestWrapper(request, cleaner, properties);
        
        // Test that exception is thrown when processing parameter values
        XssViolationException exception = assertThrows(XssViolationException.class, () -> {
            wrapper.getParameterValues("tags");
        });
        
        assertEquals("parameter[tags]", exception.getContext());
    }
}
