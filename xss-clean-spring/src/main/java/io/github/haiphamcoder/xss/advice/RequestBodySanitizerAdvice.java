package io.github.haiphamcoder.xss.advice;

import io.github.haiphamcoder.xss.CleanerService;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.lang.reflect.Type;

/**
 * Sanitize the entire JSON body after deserialization into an object.
 */
public class RequestBodySanitizerAdvice extends RequestBodyAdviceAdapter {

    /**
     * The cleaner to use.
     */
    private final CleanerService cleaner;

    /**
     * Constructs a new RequestBodySanitizerAdvice.
     * 
     * @param cleaner The cleaner to use.
     */
    public RequestBodySanitizerAdvice(CleanerService cleaner) {
        this.cleaner = cleaner;
    }

    /**
     * Checks if the advice supports the given method parameter.
     * 
     * @param methodParameter The method parameter.
     * @param targetType      The target type.
     * @param converterType   The converter type.
     * @return True if the advice supports the given method parameter, false
     *         otherwise.
     */
    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType,
            Class<? extends HttpMessageConverter<?>> converterType) {
        return true; // apply to all @RequestBody
    }

    /**
     * Sanitizes the given body after deserialization.
     * 
     * @param body          The body to sanitize.
     * @param inputMessage  The input message.
     * @param parameter     The method parameter.
     * @param targetType    The target type.
     * @param converterType The converter type.
     * @return The sanitized body.
     */
    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage,
            MethodParameter parameter, Type targetType,
            Class<? extends HttpMessageConverter<?>> converterType) {
        cleaner.cleanObject(body);
        return body;
    }
}
