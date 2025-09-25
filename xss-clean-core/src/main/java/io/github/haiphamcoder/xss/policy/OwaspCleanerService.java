package io.github.haiphamcoder.xss.policy;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

import io.github.haiphamcoder.xss.CleanerService;
import io.github.haiphamcoder.xss.ReflectionCleaner;

/**
 * The OwaspCleanerService class implements the CleanerService interface and
 * provides methods for cleaning and sanitizing input data using the OWASP Java
 * HTML Sanitizer library.
 */
public class OwaspCleanerService implements CleanerService {

    private final PolicyFactory policy;

    /**
     * Constructs a new OwaspCleanerService with the default policy.
     */
    public OwaspCleanerService() {
        this.policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);
    }

    /**
     * Constructs a new OwaspCleanerService with the given policy.
     *
     * @param policy The policy to use for sanitizing input data.
     */
    public OwaspCleanerService(PolicyFactory policy) {
        this.policy = policy;
    }

    /**
     * Cleans a raw string input by applying the OWASP Java HTML Sanitizer library.
     *
     * @param raw The raw string input to be cleaned.
     * @return The cleaned string.
     */
    @Override
    public String clean(String raw) {
        if (raw == null) {
            return null;
        }
        return policy.sanitize(raw);
    }

    /**
     * Cleans an object by applying the OWASP Java HTML Sanitizer library to its
     * fields.
     *
     * @param object The object to be cleaned.
     */
    @Override
    public void cleanObject(Object object) {
        ReflectionCleaner.clean(object, this::clean);
    }

}
