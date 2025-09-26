package io.github.haiphamcoder.xss.config;

import io.github.haiphamcoder.xss.CleanerService;
import io.github.haiphamcoder.xss.advice.RequestBodySanitizerAdvice;
import io.github.haiphamcoder.xss.annotation.NoXssValidator;
import io.github.haiphamcoder.xss.policy.JsoupCleanerService;
import io.github.haiphamcoder.xss.policy.OwaspCleanerService;
import io.github.haiphamcoder.xss.web.XssFilter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
@ConditionalOnProperty(prefix = "xss", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(XssProperties.class)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
public class XssAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    CleanerService cleanerService(XssProperties props) {
        return "jsoup".equalsIgnoreCase(props.getStrategy())
                ? new JsoupCleanerService()
                : new OwaspCleanerService();
    }

    @Bean
    XssFilter xssFilter(CleanerService cleanerService) {
        return new XssFilter(cleanerService);
    }

    @Bean
    @ConditionalOnMissingBean
    RequestBodySanitizerAdvice requestBodySanitizerAdvice(CleanerService cleanerService) {
        return new RequestBodySanitizerAdvice(cleanerService);
    }

    @Bean
    @ConditionalOnMissingBean
    NoXssValidator noXssValidator(CleanerService cleanerService) {
        return new NoXssValidator(cleanerService);
    }
}
