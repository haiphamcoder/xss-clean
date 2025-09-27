package io.github.haiphamcoder.xss.annotation;

import io.github.haiphamcoder.xss.CleanerService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.ObjectProvider;

/**
 * If the content is changed after cleaning -> reject.
 */
public class NoXssValidator implements ConstraintValidator<NoXss, String> {

    /**
     * The cleaner to use.
     */
    private final CleanerService cleaner;

    /**
     * Constructor.
     * 
     * @param cleanerProvider The cleaner provider.
     */
    public NoXssValidator(ObjectProvider<CleanerService> cleanerProvider) {
        this.cleaner = cleanerProvider.getIfAvailable();
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

        // If value is null or cleaner is null, skip validation
        if (value == null || cleaner == null) {
            return true;
        }

        // Clean the value
        String cleaned = cleaner.clean(value);
        return cleaned.equals(value);

    }
}
