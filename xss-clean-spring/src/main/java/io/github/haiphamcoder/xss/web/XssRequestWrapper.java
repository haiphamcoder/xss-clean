package io.github.haiphamcoder.xss.web;

import io.github.haiphamcoder.xss.CleanerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.*;

/**
 * Wrapper to sanitize request parameters.
 */
public class XssRequestWrapper extends HttpServletRequestWrapper {

    /**
     * The cleaner to use.
     */
    private final CleanerService cleaner;

    /**
     * Constructs a new XssRequestWrapper.
     * 
     * @param request The request to wrap.
     * @param cleaner The cleaner to use.
     */
    public XssRequestWrapper(HttpServletRequest request, CleanerService cleaner) {
        super(request);
        this.cleaner = cleaner;
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
        return cleaner.clean(value);
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
            return new String[0];
        }
        return Arrays.stream(values).map(cleaner::clean).toArray(String[]::new);
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
            String[] newVals = Arrays.stream(e.getValue()).map(cleaner::clean).toArray(String[]::new);
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
        return cleaner.clean(value);
    }
}
