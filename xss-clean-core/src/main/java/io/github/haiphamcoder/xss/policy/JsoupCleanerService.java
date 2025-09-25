package io.github.haiphamcoder.xss.policy;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import io.github.haiphamcoder.xss.CleanerService;
import io.github.haiphamcoder.xss.ReflectionCleaner;

/**
 * The JsoupCleanerService class implements the CleanerService interface and
 * provides methods for cleaning and sanitizing input data using the JSoup
 * library.
 */
public class JsoupCleanerService implements CleanerService {

    private final Safelist safelist;

    /**
     * Constructs a new JsoupCleanerService with the default safelist.
     */
    public JsoupCleanerService() {
        this.safelist = Safelist.none();
    }

    /**
     * Constructs a new JsoupCleanerService with the given safelist.
     *
     * @param safelist The safelist to use for sanitizing input data.
     */
    public JsoupCleanerService(Safelist safelist) {
        this.safelist = safelist;
    }

    /**
     * Cleans a raw string input by applying the JSoup library.
     *
     * @param raw The raw string input to be cleaned.
     * @return The cleaned string.
     */
    @Override
    public String clean(String raw) {
        if (raw == null) {
            return null;
        }
        return Jsoup.clean(raw, safelist);
    }

    /**
     * Cleans an object by applying the JSoup library to its fields.
     *
     * @param object The object to be cleaned.
     */
    @Override
    public void cleanObject(Object object) {
        ReflectionCleaner.clean(object, this::clean);
    }

}
