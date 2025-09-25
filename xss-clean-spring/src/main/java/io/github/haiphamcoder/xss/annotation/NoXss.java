package io.github.haiphamcoder.xss.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * If the content is changed after cleaning -> reject.
 */
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = NoXssValidator.class)
public @interface NoXss {

    /**
     * The message to use if the content is invalid.
     * 
     * @return The message to use if the content is invalid.
     */
    String message() default "Invalid content (XSS detected)";

    /**
     * The groups to use if the content is invalid.
     * 
     * @return The groups to use if the content is invalid.
     */
    Class<?>[] groups() default {};

    /**
     * The payload to use if the content is invalid.
     * 
     * @return The payload to use if the content is invalid.
     */
    Class<? extends Payload>[] payload() default {};
}
