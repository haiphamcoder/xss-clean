package io.github.haiphamcoder.xss.exception;

/**
 * Exception thrown when an XSS (Cross-Site Scripting) violation is detected.
 * This exception is thrown when the XSS cleaner detects potentially malicious content
 * and the throw-on-violation configuration is enabled.
 */
public class XssViolationException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    private final String context;
    private final String originalValue;
    private final String cleanedValue;

    /**
     * Constructs a new XssViolationException with the specified detail message.
     *
     * @param message the detail message
     */
    public XssViolationException(String message) {
        super(message);
        this.context = null;
        this.originalValue = null;
        this.cleanedValue = null;
    }

    /**
     * Constructs a new XssViolationException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public XssViolationException(String message, Throwable cause) {
        super(message, cause);
        this.context = null;
        this.originalValue = null;
        this.cleanedValue = null;
    }

    /**
     * Constructs a new XssViolationException with context and value information.
     *
     * @param context        the context where the violation occurred
     * @param originalValue  the original value that caused the violation
     * @param cleanedValue   the cleaned value after sanitization
     */
    public XssViolationException(String context, String originalValue, String cleanedValue) {
        super(String.format("XSS violation detected in %s: %s", context, originalValue));
        this.context = context;
        this.originalValue = originalValue;
        this.cleanedValue = cleanedValue;
    }

    /**
     * Constructs a new XssViolationException with context, value information, and cause.
     *
     * @param context        the context where the violation occurred
     * @param originalValue  the original value that caused the violation
     * @param cleanedValue   the cleaned value after sanitization
     * @param cause          the cause
     */
    public XssViolationException(String context, String originalValue, String cleanedValue, Throwable cause) {
        super(String.format("XSS violation detected in %s: %s", context, originalValue), cause);
        this.context = context;
        this.originalValue = originalValue;
        this.cleanedValue = cleanedValue;
    }

    /**
     * Gets the context where the violation occurred.
     *
     * @return the context
     */
    public String getContext() {
        return context;
    }

    /**
     * Gets the original value that caused the violation.
     *
     * @return the original value
     */
    public String getOriginalValue() {
        return originalValue;
    }

    /**
     * Gets the cleaned value after sanitization.
     *
     * @return the cleaned value
     */
    public String getCleanedValue() {
        return cleanedValue;
    }

    /**
     * Checks if this exception contains detailed violation information.
     *
     * @return true if detailed information is available
     */
    public boolean hasDetailedInfo() {
        return context != null && originalValue != null;
    }

    @Override
    public String toString() {
        if (hasDetailedInfo()) {
            return String.format("XssViolationException{context='%s', originalValue='%s', cleanedValue='%s'}", 
                context, originalValue, cleanedValue);
        }
        return super.toString();
    }
}
