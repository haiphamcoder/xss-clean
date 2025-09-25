package io.github.haiphamcoder.xss;

/**
 * The CleanerService interface provides methods for cleaning and sanitizing
 * input data.
 * It offers a way to apply XSS protection policies to different types of data.
 */
public interface CleanerService {

    /**
     * Cleans a raw string input by applying XSS protection policies.
     *
     * @param raw The raw string input to be cleaned.
     * @return The cleaned string.
     */
    String clean(String raw);

    /**
     * Cleans an object by applying XSS protection policies to its fields.
     *
     * @param object The object to be cleaned.
     */
    void cleanObject(Object object);

}
