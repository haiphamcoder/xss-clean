package io.github.haiphamcoder.xss.config;

import io.github.haiphamcoder.xss.CleanerService;
import io.github.haiphamcoder.xss.advice.RequestBodySanitizerAdvice;
import io.github.haiphamcoder.xss.policy.JsoupCleanerService;
import io.github.haiphamcoder.xss.policy.OwaspCleanerService;
import io.github.haiphamcoder.xss.web.XssFilter;
import org.jsoup.safety.Safelist;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

@Configuration
@ConditionalOnProperty(prefix = "xss", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(XssProperties.class)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
public class XssAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    CleanerService cleanerService(XssProperties props) {
        if ("jsoup".equalsIgnoreCase(props.getStrategy())) {
            // Create JsoupCleanerService with profile-based safelist
            Safelist safelist = createSafelistFromProfile(props);
            return new JsoupCleanerService(safelist);
        } else {
            return new OwaspCleanerService();
        }
    }

    /**
     * Creates a Safelist based on the configured profile.
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

    @Bean
    XssFilter xssFilter(CleanerService cleanerService, XssProperties properties) {
        return new XssFilter(cleanerService, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    RequestBodySanitizerAdvice requestBodySanitizerAdvice(CleanerService cleanerService) {
        return new RequestBodySanitizerAdvice(cleanerService);
    }

}
