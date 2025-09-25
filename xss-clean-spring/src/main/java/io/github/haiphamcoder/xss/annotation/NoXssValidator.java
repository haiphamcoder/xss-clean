package io.github.haiphamcoder.xss.annotation;

import io.github.haiphamcoder.xss.CleanerService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * If the content is changed after cleaning -> reject.
 */
public class NoXssValidator implements ConstraintValidator<NoXss, String> {

    /**
     * The cleaner to use.
     */
    private final CleanerService cleaner;

    /**
     * Constructs a new NoXssValidator.
     * 
     * @param cleaner The cleaner to use.
     */
    public NoXssValidator(CleanerService cleaner) {
        this.cleaner = cleaner;
    }

    /**
     * Checks if the value is valid.
     * 
     * @param value   The value to check.
     * @param context The context.
     * @return True if the value is valid, false otherwise.
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null)
            return true;
        String cleaned = cleaner.clean(value);
        return cleaned.equals(value);
    }
}
