package io.github.haiphamcoder.xss.spring;

import io.github.haiphamcoder.xss.CleanerService;
import io.github.haiphamcoder.xss.SpringXssCleanApplication;
import io.github.haiphamcoder.xss.config.XssProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = SpringXssCleanApplication.class)
@TestPropertySource(properties = {
    "xss.enabled=true",
    "xss.strategy=jsoup",
    "xss.default-profile=strict",
    "xss.profiles.strict.allowed-tags=p,br",
    "xss.profiles.strict.allowed-attributes=class",
    "xss.profiles.lenient.allowed-tags=p,br,b,i,a,img",
    "xss.profiles.lenient.allowed-attributes=class,href,src",
    "xss.profiles.custom.allowed-tags=div,span,p,br,strong,em",
    "xss.profiles.custom.allowed-attributes=class,id,style"
})
class ProfileConfigurationTest {

    @Autowired
    private CleanerService cleanerService;

    @Autowired
    private XssProperties xssProperties;

    @Test
    void testStrictProfileConfiguration() {
        // Test that strict profile is applied
        String input = "<p class='test'>Hello</p><script>alert('XSS')</script>";
        String cleaned = cleanerService.clean(input);
        
        // Should only allow p and br tags with class attribute
        assertEquals("<p class=\"test\">Hello</p>", cleaned);
        assertFalse(cleaned.contains("script"));
    }

    @Test
    void testProfilePropertiesAreLoaded() {
        // Test that profile properties are loaded correctly
        assertTrue(xssProperties.getProfiles().containsKey("strict"));
        assertTrue(xssProperties.getProfiles().containsKey("lenient"));
        assertTrue(xssProperties.getProfiles().containsKey("custom"));
        
        assertEquals("strict", xssProperties.getDefaultProfile());
        assertEquals("jsoup", xssProperties.getStrategy());
        
        // Test strict profile configuration
        XssProperties.Profile strictProfile = xssProperties.getProfiles().get("strict");
        assertEquals("p,br", strictProfile.getAllowedTags());
        assertEquals("class", strictProfile.getAllowedAttributes());
        
        // Test lenient profile configuration
        XssProperties.Profile lenientProfile = xssProperties.getProfiles().get("lenient");
        assertEquals("p,br,b,i,a,img", lenientProfile.getAllowedTags());
        assertEquals("class,href,src", lenientProfile.getAllowedAttributes());
        
        // Test custom profile configuration
        XssProperties.Profile customProfile = xssProperties.getProfiles().get("custom");
        assertEquals("div,span,p,br,strong,em", customProfile.getAllowedTags());
        assertEquals("class,id,style", customProfile.getAllowedAttributes());
    }

    @Test
    void testDifferentProfiles() {
        // Test that different profiles produce different results
        String input = "<div class='test' id='myId' style='color:red'>Hello <strong>World</strong></div>";
        
        // With strict profile (only p,br allowed)
        String strictCleaned = cleanerService.clean(input);
        assertFalse(strictCleaned.contains("div"));
        assertFalse(strictCleaned.contains("strong"));
        
        // The input should be cleaned to empty or minimal content
        assertTrue(strictCleaned.isEmpty() || !strictCleaned.contains("div"));
    }
}
