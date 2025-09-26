package io.github.haiphamcoder.xss.annotation;

import io.github.haiphamcoder.xss.CleanerService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * If the content is changed after cleaning -> reject.
 */
public class NoXssValidator implements ConstraintValidator<NoXss, String> {

    /**
     * The cleaner to use.
     */
    private final CleanerService cleaner;

    /**
     * Custom message from annotation.
     */
    private String message;

    @Autowired
    public NoXssValidator(ObjectProvider<CleanerService> cleanerProvider) {
        this.cleaner = cleanerProvider.getIfAvailable();
    }

    /**
     * Initialize the validator.
     */
    @Override
    public void initialize(NoXss constraintAnnotation) {
        // Store the custom message from annotation
        this.message = constraintAnnotation.message();
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
        boolean isValid = cleaned.equals(value);
        
        // If validation fails, set the custom message
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                   .addConstraintViolation();
        }
        
        return isValid;
    }
}
