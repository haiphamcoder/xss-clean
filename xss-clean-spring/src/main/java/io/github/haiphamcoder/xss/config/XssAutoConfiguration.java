package io.github.haiphamcoder.xss.config;

import io.github.haiphamcoder.xss.CleanerService;
import io.github.haiphamcoder.xss.advice.RequestBodySanitizerAdvice;
import io.github.haiphamcoder.xss.policy.JsoupCleanerService;
import io.github.haiphamcoder.xss.policy.OwaspCleanerService;
import io.github.haiphamcoder.xss.policy.SanitizerStrategy;
import io.github.haiphamcoder.xss.web.XssFilter;
import org.jsoup.safety.Safelist;
import org.owasp.html.PolicyFactory;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@ConditionalOnProperty(prefix = "xss.cleaner", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(XssProperties.class)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
public class XssAutoConfiguration {

    /**
     * Creates a CleanerService based on the configured strategy.
     * 
     * @param props The XSS properties.
     * @return The CleanerService.
     */
    @Bean
    @ConditionalOnMissingBean
    CleanerService cleanerService(XssProperties props) {
        if (SanitizerStrategy.JSOUP.name().equalsIgnoreCase(props.getStrategy())) {
            // Create JsoupCleanerService with profile-based safelist
            Safelist safelist = createSafelistFromProfile(props);
            return new JsoupCleanerService(safelist);
        } else {
            // Create OwaspCleanerService with configured policy
            PolicyFactory policy = createPolicyFromConfiguration(props);
            return new OwaspCleanerService(policy);
        }
    }

    /**
     * Creates a Safelist based on the configured profile.
     * 
     * @param props The XSS properties.
     * @return The Safelist.
     */
    private Safelist createSafelistFromProfile(XssProperties props) {
        String profileName = props.getDefaultProfile();
        XssProperties.Profile profile = props.getProfiles().get(profileName);

        if (profile == null) {
            // Use default safelist if profile not found
            return Safelist.none();
        }

        Safelist safelist = Safelist.none();
        Set<String> addedTags = new HashSet<>();

        // Add allowed tags first
        if (StringUtils.hasText(profile.getAllowedTags())) {
            String[] tags = profile.getAllowedTags().split(",");
            for (String tag : tags) {
                String trimmedTag = tag.trim();
                safelist.addTags(trimmedTag);
                addedTags.add(trimmedTag);
            }
        }

        // Add allowed attributes only if we have tags
        if (StringUtils.hasText(profile.getAllowedAttributes()) && !addedTags.isEmpty()) {
            String[] attributes = profile.getAllowedAttributes().split(",");
            for (String attr : attributes) {
                String trimmedAttr = attr.trim();
                // Add attributes to all allowed tags
                for (String tag : addedTags) {
                    safelist.addAttributes(tag, trimmedAttr);
                }
            }
        }

        return safelist;
    }

    /**
     * Creates a PolicyFactory based on the configured OWASP policy.
     * 
     * @param props The XSS properties.
     * @return The PolicyFactory.
     */
    private PolicyFactory createPolicyFromConfiguration(XssProperties props) {
        List<OwaspPolicy> policies = props.getOwaspPolicies();

        if (policies == null || policies.isEmpty()) {
            // Default to NONE policy if no policies configured
            return OwaspPolicy.NONE.createPolicyFactory();
        }

        // Create combined policy from all configured policies
        return OwaspPolicy.createCombinedPolicyFactory(
                policies.toArray(new OwaspPolicy[0]));
    }

    /**
     * Creates a XssFilter.
     * 
     * @param cleanerService The CleanerService.
     * @param properties     The XSS properties.
     * @return The XssFilter.
     */
    @Bean
    XssFilter xssFilter(CleanerService cleanerService, XssProperties properties) {
        return new XssFilter(cleanerService, properties);
    }

    /**
     * Creates a RequestBodySanitizerAdvice.
     * 
     * @param cleanerService The CleanerService.
     * @return The RequestBodySanitizerAdvice.
     */
    @Bean
    @ConditionalOnMissingBean
    RequestBodySanitizerAdvice requestBodySanitizerAdvice(CleanerService cleanerService) {
        return new RequestBodySanitizerAdvice(cleanerService);
    }

}
