package io.github.haiphamcoder.xss.policy;

/**
 * The SanitizerStrategy enum represents the different strategies for sanitizing
 * input data.
 */
public enum SanitizerStrategy {
    /**
     * The OWASP strategy uses the OWASP Java HTML Sanitizer library to sanitize
     * input data.
     */
    OWASP,
    /**
     * The JSOUP strategy uses the JSoup library to sanitize input data.
     */
    JSOUP
}
