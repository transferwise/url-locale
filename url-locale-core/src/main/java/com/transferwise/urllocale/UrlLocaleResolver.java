package com.transferwise.urllocale;

import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Map;

import static com.transferwise.urllocale.UrlLocaleExtractorFilter.LOCALE_ATTRIBUTE;

public class UrlLocaleResolver implements LocaleResolver {
    private final Map<String, Locale> localeMapping;
    private final Locale fallback;

    public UrlLocaleResolver(Map<String, Locale> localeMapping, Locale fallback) {
        this.localeMapping = localeMapping;
        this.fallback = fallback;
    }

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String locale = (String) request.getAttribute(LOCALE_ATTRIBUTE);

        return localeMapping.getOrDefault(locale, fallback);
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        throw new UnsupportedOperationException();
    }
}
