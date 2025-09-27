package io.github.haiphamcoder.xss.config;

import org.owasp.html.PolicyFactory;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.Sanitizers;

/**
 * Enum representing different OWASP policy types for XSS sanitization.
 */
public enum OwaspPolicy {
    
    /**
     * Remove all HTML tags - most restrictive policy.
     */
    NONE("none", "Remove all HTML tags") {
        @Override
        public PolicyFactory createPolicyFactory() {
            return new HtmlPolicyBuilder().toFactory();
        }
    },
    
    /**
     * Allow basic formatting and links.
     */
    BASIC("basic", "Allow formatting and links") {
        @Override
        public PolicyFactory createPolicyFactory() {
            return Sanitizers.FORMATTING.and(Sanitizers.LINKS);
        }
    },
    
    /**
     * Allow only formatting tags (bold, italic, etc.).
     */
    FORMATTING("formatting", "Allow only formatting tags") {
        @Override
        public PolicyFactory createPolicyFactory() {
            return Sanitizers.FORMATTING;
        }
    },
    
    /**
     * Allow only link tags.
     */
    LINKS("links", "Allow only link tags") {
        @Override
        public PolicyFactory createPolicyFactory() {
            return Sanitizers.LINKS;
        }
    },
    
    /**
     * Allow block elements (paragraphs, headings, etc.).
     */
    BLOCKS("blocks", "Allow block elements") {
        @Override
        public PolicyFactory createPolicyFactory() {
            return Sanitizers.BLOCKS;
        }
    },
    
    /**
     * Allow style elements.
     */
    STYLES("styles", "Allow style elements") {
        @Override
        public PolicyFactory createPolicyFactory() {
            return Sanitizers.STYLES;
        }
    },
    
    /**
     * Allow table elements.
     */
    TABLES("tables", "Allow table elements") {
        @Override
        public PolicyFactory createPolicyFactory() {
            return Sanitizers.TABLES;
        }
    },
    
    /**
     * Allow image elements.
     */
    IMAGES("images", "Allow image elements") {
        @Override
        public PolicyFactory createPolicyFactory() {
            return Sanitizers.IMAGES;
        }
    };
    
    private final String value;
    private final String description;
    
    OwaspPolicy(String value, String description) {
        this.value = value;
        this.description = description;
    }
    
    /**
     * Gets the string value of the policy.
     * 
     * @return The string value.
     */
    public String getValue() {
        return value;
    }
    
    /**
     * Gets the description of the policy.
     * 
     * @return The description.
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Creates a PolicyFactory for this policy type.
     * 
     * @return The PolicyFactory.
     */
    public abstract PolicyFactory createPolicyFactory();
    
    /**
     * Creates a combined PolicyFactory from multiple policies.
     * 
     * @param policies The policies to combine.
     * @return The combined PolicyFactory.
     */
    public static PolicyFactory createCombinedPolicyFactory(OwaspPolicy... policies) {
        if (policies == null || policies.length == 0) {
            return NONE.createPolicyFactory();
        }
        
        PolicyFactory combined = policies[0].createPolicyFactory();
        for (int i = 1; i < policies.length; i++) {
            combined = combined.and(policies[i].createPolicyFactory());
        }
        return combined;
    }
    
    /**
     * Gets the policy by its string value.
     * 
     * @param value The string value.
     * @return The OwaspPolicy, or NONE if not found.
     */
    public static OwaspPolicy fromValue(String value) {
        if (value == null) {
            return NONE;
        }
        
        for (OwaspPolicy policy : values()) {
            if (policy.value.equalsIgnoreCase(value)) {
                return policy;
            }
        }
        return NONE;
    }
}
