package io.github.haiphamcoder.xss.config;

import io.github.haiphamcoder.xss.CleanerService;
import io.github.haiphamcoder.xss.policy.JsoupCleanerService;
import io.github.haiphamcoder.xss.policy.OwaspCleanerService;
import io.github.haiphamcoder.xss.web.XssFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "xss", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(XssProperties.class)
public class XssAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CleanerService cleanerService(XssProperties props) {
        return "jsoup".equalsIgnoreCase(props.getStrategy())
                ? new JsoupCleanerService()
                : new OwaspCleanerService();
    }

    @Bean
    public XssFilter xssFilter(CleanerService cleanerService) {
        return new XssFilter(cleanerService);
    }
}
