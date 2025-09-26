package io.github.haiphamcoder.xss.spring;

import io.github.haiphamcoder.xss.CleanerService;
import io.github.haiphamcoder.xss.advice.RequestBodySanitizerAdvice;
import io.github.haiphamcoder.xss.config.XssAutoConfiguration;
import io.github.haiphamcoder.xss.config.XssProperties;
import io.github.haiphamcoder.xss.web.XssFilter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class AutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(XssAutoConfiguration.class));

    @Test
    void testAutoConfigurationWithDefaultProperties() {
        this.contextRunner
                .withPropertyValues("xss.cleaner.enabled=true")
                .run(context -> {
                    assertThat(context).hasSingleBean(CleanerService.class);
                    assertThat(context).hasSingleBean(XssFilter.class);
                    assertThat(context).hasSingleBean(RequestBodySanitizerAdvice.class);
                    assertThat(context).hasSingleBean(XssProperties.class);
                });
    }

    @Test
    void testAutoConfigurationWhenDisabled() {
        this.contextRunner
                .withPropertyValues("xss.cleaner.enabled=false")
                .run(context -> {
                    // When disabled, no beans should be created
                    assertThat(context).doesNotHaveBean(CleanerService.class);
                    assertThat(context).doesNotHaveBean(XssFilter.class);
                    assertThat(context).doesNotHaveBean(RequestBodySanitizerAdvice.class);
                    // XssProperties is not created when disabled
                    assertThat(context).doesNotHaveBean(XssProperties.class);
                });
    }

    @Test
    void testAutoConfigurationWithJsoupStrategy() {
        this.contextRunner
                .withPropertyValues("xss.cleaner.enabled=true", "xss.cleaner.strategy=jsoup")
                .run(context -> {
                    assertThat(context).hasSingleBean(CleanerService.class);
                    CleanerService cleanerService = context.getBean(CleanerService.class);
                    assertThat(cleanerService).isNotNull();
                });
    }

    @Test
    void testAutoConfigurationWithOwaspStrategy() {
        this.contextRunner
                .withPropertyValues("xss.cleaner.enabled=true", "xss.cleaner.strategy=owasp")
                .run(context -> {
                    assertThat(context).hasSingleBean(CleanerService.class);
                    CleanerService cleanerService = context.getBean(CleanerService.class);
                    assertThat(cleanerService).isNotNull();
                });
    }
}
