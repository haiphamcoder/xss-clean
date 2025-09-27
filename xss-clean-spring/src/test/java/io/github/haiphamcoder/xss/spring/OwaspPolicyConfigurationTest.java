package io.github.haiphamcoder.xss.spring;

import io.github.haiphamcoder.xss.CleanerService;
import io.github.haiphamcoder.xss.config.OwaspPolicy;
import io.github.haiphamcoder.xss.config.XssProperties;
import io.github.haiphamcoder.xss.policy.OwaspCleanerService;
import org.junit.jupiter.api.Test;
import org.owasp.html.PolicyFactory;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.Sanitizers;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OwaspPolicyConfigurationTest {

    @Test
    void testOwaspPolicyNone() {
        // Test policy that removes all HTML tags
        PolicyFactory nonePolicy = new HtmlPolicyBuilder().toFactory();
        CleanerService cleaner = new OwaspCleanerService(nonePolicy);
        
        String testHtml = "<script>alert('XSS')</script><b>Hello</b><p>World</p>";
        String cleaned = cleaner.clean(testHtml);
        
        // Should remove all HTML tags
        assertFalse(cleaned.contains("<script>"));
        assertFalse(cleaned.contains("<b>"));
        assertFalse(cleaned.contains("<p>"));
        assertTrue(cleaned.contains("Hello"));
        assertTrue(cleaned.contains("World"));
    }

    @Test
    void testOwaspPolicyBasic() {
        // Test policy with basic formatting and links
        PolicyFactory basicPolicy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);
        CleanerService cleaner = new OwaspCleanerService(basicPolicy);
        
        String testHtml = "<script>alert('XSS')</script><b>Hello</b><a href='http://example.com'>Link</a>";
        String cleaned = cleaner.clean(testHtml);
        
        // Should remove script but allow formatting and links
        assertFalse(cleaned.contains("<script>"));
        assertTrue(cleaned.contains("Hello"));
        assertTrue(cleaned.contains("Link"));
    }

    @Test
    void testXssPropertiesOwaspPolicies() {
        XssProperties props = new XssProperties();
        props.setStrategy("owasp");
        props.setOwaspPolicies(Arrays.asList(OwaspPolicy.NONE));
        
        // Test that the properties are set correctly
        assertEquals("owasp", props.getStrategy());
        assertEquals(1, props.getOwaspPolicies().size());
        assertEquals(OwaspPolicy.NONE, props.getOwaspPolicies().get(0));
        
        // Test default policy
        XssProperties defaultProps = new XssProperties();
        assertEquals(1, defaultProps.getOwaspPolicies().size());
        assertEquals(OwaspPolicy.NONE, defaultProps.getOwaspPolicies().get(0));
    }

    @Test
    void testMultipleOwaspPolicies() {
        // Test combining multiple policies
        List<OwaspPolicy> policies = Arrays.asList(OwaspPolicy.FORMATTING, OwaspPolicy.LINKS);
        PolicyFactory combinedPolicy = OwaspPolicy.createCombinedPolicyFactory(
            policies.toArray(new OwaspPolicy[0])
        );
        
        CleanerService cleaner = new OwaspCleanerService(combinedPolicy);
        
        String testHtml = "<script>alert('XSS')</script><b>Hello</b><a href='http://example.com'>Link</a>";
        String cleaned = cleaner.clean(testHtml);
        
        // Should remove script but allow formatting and links
        assertFalse(cleaned.contains("<script>"));
        assertTrue(cleaned.contains("Hello"));
        assertTrue(cleaned.contains("Link"));
    }

    @Test
    void testOwaspPolicyEnum() {
        // Test enum values
        assertEquals("none", OwaspPolicy.NONE.getValue());
        assertEquals("basic", OwaspPolicy.BASIC.getValue());
        assertEquals("formatting", OwaspPolicy.FORMATTING.getValue());
        
        // Test fromValue method
        assertEquals(OwaspPolicy.NONE, OwaspPolicy.fromValue("none"));
        assertEquals(OwaspPolicy.BASIC, OwaspPolicy.fromValue("basic"));
        assertEquals(OwaspPolicy.NONE, OwaspPolicy.fromValue("invalid")); // Should return NONE for invalid values
        assertEquals(OwaspPolicy.NONE, OwaspPolicy.fromValue(null)); // Should return NONE for null
    }
}
