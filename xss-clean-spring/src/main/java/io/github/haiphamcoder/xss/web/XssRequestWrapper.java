package io.github.haiphamcoder.xss.web;

import io.github.haiphamcoder.xss.CleanerService;
import io.github.haiphamcoder.xss.config.XssProperties;
import io.github.haiphamcoder.xss.exception.XssViolationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Wrapper to sanitize request parameters.
 */
public class XssRequestWrapper extends HttpServletRequestWrapper {

    private static final Logger logger = LoggerFactory.getLogger(XssRequestWrapper.class);
    private static final String PARAMETER_PREFIX = "parameter[";
    private static final String HEADER_PREFIX = "header[";
    private static final String SUFFIX = "]";

    /**
     * The cleaner to use.
     */
    private final CleanerService cleaner;

    /**
     * The XSS properties.
     */
    private final XssProperties properties;

    /**
     * Constructs a new XssRequestWrapper.
     * 
     * @param request    The request to wrap.
     * @param cleaner    The cleaner to use.
     * @param properties The XSS properties.
     */
    public XssRequestWrapper(HttpServletRequest request, CleanerService cleaner, XssProperties properties) {
        super(request);
        this.cleaner = cleaner;
        this.properties = properties;
    }

    /**
     * Cleans a value with logging and exception handling.
     * 
     * @param value   The value to clean.
     * @param context The context for logging (e.g., parameter name).
     * @return The cleaned value.
     */
    private String cleanValue(String value, String context) {
        if (value == null) {
            return null;
        }

        String cleaned = cleaner.clean(value);

        // Check if content was changed (XSS detected)
        if (!value.equals(cleaned)) {
            // Log violation if enabled
            if (properties.isLogViolation()) {
                logger.warn("XSS violation detected in {}: '{}' -> '{}'", context, value, cleaned);
            }

            // Throw exception if enabled
            if (properties.isThrowOnViolation()) {
                throw new XssViolationException(context, value, cleaned);
            }
        }

        return cleaned;
    }

    /**
     * Gets the parameter value.
     * 
     * @param name The name of the parameter.
     * @return The parameter value.
     */
    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        return cleanValue(value, PARAMETER_PREFIX + name + SUFFIX);
    }

    /**
     * Gets the parameter values.
     * 
     * @param name The name of the parameter.
     * @return The parameter values.
     */
    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) {
            // Preserve servlet contract: return null when parameter is absent
            return null;
        }
        return Arrays.stream(values)
                .map(value -> cleanValue(value, PARAMETER_PREFIX + name + SUFFIX))
                .toArray(String[]::new);
    }

    /**
     * Gets the parameter map.
     * 
     * @return The parameter map.
     */
    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> map = super.getParameterMap();
        Map<String, String[]> cleaned = new HashMap<>();
        for (Map.Entry<String, String[]> e : map.entrySet()) {
            String[] newVals = Arrays.stream(e.getValue())
                    .map(value -> cleanValue(value, PARAMETER_PREFIX + e.getKey() + SUFFIX))
                    .toArray(String[]::new);
            cleaned.put(e.getKey(), newVals);
        }
        return cleaned;
    }

    /**
     * Gets the header value.
     * 
     * @param name The name of the header.
     * @return The header value.
     */
    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        return cleanValue(value, HEADER_PREFIX + name + SUFFIX);
    }
}
