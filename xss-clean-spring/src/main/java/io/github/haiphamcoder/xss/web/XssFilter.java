package io.github.haiphamcoder.xss.web;

import io.github.haiphamcoder.xss.CleanerService;
import io.github.haiphamcoder.xss.config.XssProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter to sanitize query params + headers.
 */
public class XssFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(XssFilter.class);

    private final CleanerService cleaner;
    private final XssProperties properties;

    public XssFilter(CleanerService cleaner, XssProperties properties) {
        this.cleaner = cleaner;
        this.properties = properties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        XssRequestWrapper wrapped = new XssRequestWrapper(request, cleaner, properties);
        filterChain.doFilter(wrapped, response);
    }
}
