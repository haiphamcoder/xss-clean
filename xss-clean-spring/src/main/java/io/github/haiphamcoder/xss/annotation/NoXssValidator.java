package io.github.haiphamcoder.xss.annotation;

import io.github.haiphamcoder.xss.CleanerService;
import io.github.haiphamcoder.xss.policy.OwaspCleanerService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * If the content is changed after cleaning -> reject.
 */
public class NoXssValidator implements ConstraintValidator<NoXss, String> {

    /**
     * The cleaner to use.
     */
    private CleanerService cleaner;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Initialize the validator.
     */
    @Override
    public void initialize(NoXss constraintAnnotation) {
        // Try to get CleanerService from Spring context
        try {
            this.cleaner = applicationContext.getBean(CleanerService.class);
        } catch (Exception e) {
            // Fallback to default implementation if no bean found
            this.cleaner = new OwaspCleanerService();
        }
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
        
        // If cleaner is not available, skip validation
        if (cleaner == null) {
            return true;
        }
            
        String cleaned = cleaner.clean(value);
        return cleaned.equals(value);
    }
}
