package io.github.haiphamcoder.xss.spring;

import io.github.haiphamcoder.xss.CleanerService;
import io.github.haiphamcoder.xss.SpringXssCleanApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = SpringXssCleanApplication.class)
class XssIntegrationTest {

    @Autowired
    CleanerService cleaner;

    @Test
    void testClean() {
        String raw = "<script>alert(1)</script><b>Hi</b>";
        String cleaned = cleaner.clean(raw);

        assertFalse(cleaned.contains("script"));
        assertTrue(cleaned.contains("Hi"));
    }
}

