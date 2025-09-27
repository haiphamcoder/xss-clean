package io.github.haiphamcoder.xss.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class XssViolationExceptionTest {

    @Test
    void testBasicConstructor() {
        String message = "XSS violation detected";
        XssViolationException exception = new XssViolationException(message);
        
        assertEquals(message, exception.getMessage());
        assertNull(exception.getContext());
        assertNull(exception.getOriginalValue());
        assertNull(exception.getCleanedValue());
        assertFalse(exception.hasDetailedInfo());
    }

    @Test
    void testConstructorWithCause() {
        String message = "XSS violation detected";
        RuntimeException cause = new RuntimeException("Test cause");
        XssViolationException exception = new XssViolationException(message, cause);
        
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertNull(exception.getContext());
        assertNull(exception.getOriginalValue());
        assertNull(exception.getCleanedValue());
        assertFalse(exception.hasDetailedInfo());
    }

    @Test
    void testDetailedConstructor() {
        String context = "parameter[name]";
        String originalValue = "<script>alert('XSS')</script>";
        String cleanedValue = "alert('XSS')";
        
        XssViolationException exception = new XssViolationException(context, originalValue, cleanedValue);
        
        assertEquals("XSS violation detected in parameter[name]: <script>alert('XSS')</script>", exception.getMessage());
        assertEquals(context, exception.getContext());
        assertEquals(originalValue, exception.getOriginalValue());
        assertEquals(cleanedValue, exception.getCleanedValue());
        assertTrue(exception.hasDetailedInfo());
    }

    @Test
    void testDetailedConstructorWithCause() {
        String context = "header[user-agent]";
        String originalValue = "<img src=x onerror=alert(1)>";
        String cleanedValue = "";
        RuntimeException cause = new RuntimeException("Test cause");
        
        XssViolationException exception = new XssViolationException(context, originalValue, cleanedValue, cause);
        
        assertEquals("XSS violation detected in header[user-agent]: <img src=x onerror=alert(1)>", exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals(context, exception.getContext());
        assertEquals(originalValue, exception.getOriginalValue());
        assertEquals(cleanedValue, exception.getCleanedValue());
        assertTrue(exception.hasDetailedInfo());
    }

    @Test
    void testToStringWithDetailedInfo() {
        String context = "parameter[content]";
        String originalValue = "<b>Hello</b>";
        String cleanedValue = "Hello";
        
        XssViolationException exception = new XssViolationException(context, originalValue, cleanedValue);
        String toString = exception.toString();
        
        assertTrue(toString.contains("XssViolationException"));
        assertTrue(toString.contains("context='parameter[content]'"));
        assertTrue(toString.contains("originalValue='<b>Hello</b>'"));
        assertTrue(toString.contains("cleanedValue='Hello'"));
    }

    @Test
    void testToStringWithoutDetailedInfo() {
        String message = "Simple XSS violation";
        XssViolationException exception = new XssViolationException(message);
        String toString = exception.toString();
        
        assertTrue(toString.contains("XssViolationException"));
        assertTrue(toString.contains(message));
    }

    @Test
    void testNullValues() {
        XssViolationException exception = new XssViolationException(null, null, null);
        
        assertNull(exception.getContext());
        assertNull(exception.getOriginalValue());
        assertNull(exception.getCleanedValue());
        assertFalse(exception.hasDetailedInfo());
    }

    @Test
    void testEmptyValues() {
        XssViolationException exception = new XssViolationException("", "", "");
        
        assertEquals("", exception.getContext());
        assertEquals("", exception.getOriginalValue());
        assertEquals("", exception.getCleanedValue());
        assertTrue(exception.hasDetailedInfo());
    }
}
