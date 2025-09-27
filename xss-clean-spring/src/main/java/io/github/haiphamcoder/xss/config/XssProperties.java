package io.github.haiphamcoder.xss.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "xss.cleaner")
public class XssProperties {

    /**
     * Whether XSS cleaning is enabled.
     */
    private boolean enabled = true;

    /**
     * The strategy to use for XSS cleaning.
     */
    private String strategy = "owasp";

    /**
     * Whether to throw an exception when a violation is detected.
     */
    private boolean throwOnViolation = false;

    /**
     * Whether to log a violation.
     */
    private boolean logViolation = true;

    /**
     * The default profile to use for XSS cleaning.
     */
    private String defaultProfile = "simple";

    /**
     * The profiles to use for XSS cleaning.
     */
    private Map<String, Profile> profiles = new HashMap<>();

    /**
     * The OWASP policies for XSS cleaning.
     * Only applicable when strategy is "owasp".
     * Default: [NONE] - removes all HTML tags
     */
    private List<OwaspPolicy> owaspPolicies = new ArrayList<>();
    
    /**
     * Constructor to initialize default OWASP policy.
     */
    public XssProperties() {
        this.owaspPolicies.add(OwaspPolicy.NONE);
    }

    /**
     * Gets whether XSS cleaning is enabled.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets whether XSS cleaning is enabled.
     * 
     * @param enabled Whether XSS cleaning is enabled.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Gets the strategy to use for XSS cleaning.
     */
    public String getStrategy() {
        return strategy;
    }

    /**
     * Sets the strategy to use for XSS cleaning.
     * 
     * @param strategy The strategy to use for XSS cleaning.
     */
    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    /**
     * Gets whether to throw an exception when a violation is detected.
     * 
     * @return Whether to throw an exception when a violation is detected.
     */
    public boolean isThrowOnViolation() {
        return throwOnViolation;
    }

    /**
     * Sets whether to throw an exception when a violation is detected.
     * 
     * @param throwOnViolation Whether to throw an exception when a violation is
     *                         detected.
     */
    public void setThrowOnViolation(boolean throwOnViolation) {
        this.throwOnViolation = throwOnViolation;
    }

    /**
     * Gets whether to log a violation.
     * 
     * @return Whether to log a violation.
     */
    public boolean isLogViolation() {
        return logViolation;
    }

    /**
     * Sets whether to log a violation.
     * 
     * @param logViolation Whether to log a violation.
     */
    public void setLogViolation(boolean logViolation) {
        this.logViolation = logViolation;
    }

    /**
     * Gets the default profile to use for XSS cleaning.
     * 
     * @return The default profile to use for XSS cleaning.
     */
    public String getDefaultProfile() {
        return defaultProfile;
    }

    /**
     * Sets the default profile to use for XSS cleaning.
     * 
     * @param defaultProfile The default profile to use for XSS cleaning.
     */
    public void setDefaultProfile(String defaultProfile) {
        this.defaultProfile = defaultProfile;
    }

    /**
     * Gets the profiles to use for XSS cleaning.
     * 
     * @return The profiles to use for XSS cleaning.
     */
    public Map<String, Profile> getProfiles() {
        return profiles;
    }

    /**
     * Sets the profiles to use for XSS cleaning.
     * 
     * @param profiles The profiles to use for XSS cleaning.
     */
    public void setProfiles(Map<String, Profile> profiles) {
        this.profiles = profiles;
    }

    /**
     * Gets the OWASP policies for XSS cleaning.
     * 
     * @return The OWASP policies list.
     */
    public List<OwaspPolicy> getOwaspPolicies() {
        return owaspPolicies;
    }

    /**
     * Sets the OWASP policies for XSS cleaning.
     * 
     * @param owaspPolicies The OWASP policies list.
     */
    public void setOwaspPolicies(List<OwaspPolicy> owaspPolicies) {
        this.owaspPolicies = owaspPolicies;
    }

    /**
     * The profile to use for XSS cleaning.
     */
    public static class Profile {

        /**
         * The allowed tags to use for XSS cleaning.
         */
        private String allowedTags;

        /**
         * The allowed attributes to use for XSS cleaning.
         */
        private String allowedAttributes;

        /**
         * Gets the allowed tags to use for XSS cleaning.
         * 
         * @return The allowed tags to use for XSS cleaning.
         */
        public String getAllowedTags() {
            return allowedTags;
        }

        /**
         * Sets the allowed tags to use for XSS cleaning.
         * 
         * @param allowedTags The allowed tags to use for XSS cleaning.
         */
        public void setAllowedTags(String allowedTags) {
            this.allowedTags = allowedTags;
        }

        /**
         * Gets the allowed attributes to use for XSS cleaning.
         * 
         * @return The allowed attributes to use for XSS cleaning.
         */
        public String getAllowedAttributes() {
            return allowedAttributes;
        }

        /**
         * Sets the allowed attributes to use for XSS cleaning.
         * 
         * @param allowedAttributes The allowed attributes to use for XSS cleaning.
         */
        public void setAllowedAttributes(String allowedAttributes) {
            this.allowedAttributes = allowedAttributes;
        }
    }

}
