package io.github.haiphamcoder.xss;

import org.junit.jupiter.api.Test;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.jsoup.safety.Safelist;

import io.github.haiphamcoder.xss.policy.JsoupCleanerService;
import io.github.haiphamcoder.xss.policy.OwaspCleanerService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void testCleanNullInput() {
        CleanerService cleaner = new OwaspCleanerService();
        assertNull(cleaner.clean(null));
        
        CleanerService jsoupCleaner = new JsoupCleanerService();
        assertNull(jsoupCleaner.clean(null));
    }

    @Test
    void testCleanEmptyString() {
        CleanerService cleaner = new OwaspCleanerService();
        assertEquals("", cleaner.clean(""));
        
        CleanerService jsoupCleaner = new JsoupCleanerService();
        assertEquals("", jsoupCleaner.clean(""));
    }

    @Test
    void testCleanObjectWithNull() {
        CleanerService cleaner = new OwaspCleanerService();
        cleaner.cleanObject(null); // Should not throw exception
    }

    @Test
    void testCleanObjectWithCollections() {
        CleanerService cleaner = new OwaspCleanerService();
        
        List<String> list = new ArrayList<>();
        list.add("<script>alert('XSS')</script>Hello");
        list.add("<img src=x onerror=alert(1)>World");
        
        Map<String, String> map = new HashMap<>();
        map.put("key1", "<script>alert('XSS')</script>Value1");
        map.put("key2", "<img src=x onerror=alert(1)>Value2");
        
        TestObjectWithCollections testObject = new TestObjectWithCollections();
        testObject.list = list;
        testObject.map = map;
        
        cleaner.cleanObject(testObject);
        
        assertFalse(testObject.list.get(0).contains("<script>"));
        assertTrue(testObject.list.get(0).contains("Hello"));
        assertFalse(testObject.map.get("key1").contains("<script>"));
        assertTrue(testObject.map.get("key1").contains("Value1"));
    }

    @Test
    void testCleanObjectWithArrays() {
        CleanerService cleaner = new OwaspCleanerService();
        
        String[] array = {"<script>alert('XSS')</script>Hello", "<img src=x onerror=alert(1)>World"};
        
        TestObjectWithArray testObject = new TestObjectWithArray();
        testObject.array = array;
        
        cleaner.cleanObject(testObject);
        
        assertFalse(testObject.array[0].contains("<script>"));
        assertTrue(testObject.array[0].contains("Hello"));
    }

    @Test
    void testOwaspCleanerServiceWithCustomPolicy() {
        PolicyFactory customPolicy = Sanitizers.FORMATTING.and(Sanitizers.LINKS).and(Sanitizers.BLOCKS);
        CleanerService cleaner = new OwaspCleanerService(customPolicy);
        
        String raw = "<script>alert('XSS')</script><p>Hello <a href='http://example.com'>World</a></p>";
        String cleaned = cleaner.clean(raw);
        
        assertFalse(cleaned.contains("<script>"));
        assertTrue(cleaned.contains("Hello"));
        assertTrue(cleaned.contains("World"));
    }

    @Test
    void testJsoupCleanerServiceWithCustomSafelist() {
        Safelist customSafelist = Safelist.basic();
        CleanerService cleaner = new JsoupCleanerService(customSafelist);
        
        String raw = "<script>alert('XSS')</script><p>Hello <b>World</b></p>";
        String cleaned = cleaner.clean(raw);
        
        assertFalse(cleaned.contains("<script>"));
        assertTrue(cleaned.contains("Hello"));
        assertTrue(cleaned.contains("World"));
    }

    @Test
    void testCircularReferenceHandling() {
        CleanerService cleaner = new OwaspCleanerService();
        
        CircularRefObject obj1 = new CircularRefObject();
        CircularRefObject obj2 = new CircularRefObject();
        
        obj1.name = "<script>alert('XSS')</script>Object1";
        obj2.name = "<script>alert('XSS')</script>Object2";
        
        obj1.ref = obj2;
        obj2.ref = obj1; // Circular reference
        
        cleaner.cleanObject(obj1); // Should not cause infinite loop
        
        assertFalse(obj1.name.contains("<script>"));
        assertTrue(obj1.name.contains("Object1"));
        assertFalse(obj2.name.contains("<script>"));
        assertTrue(obj2.name.contains("Object2"));
    }

    // Test helper classes
    static class TestObjectWithCollections {
        List<String> list;
        Map<String, String> map;
    }

    static class TestObjectWithArray {
        String[] array;
    }

    static class CircularRefObject {
        String name;
        CircularRefObject ref;
    }
}
