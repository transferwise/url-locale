package com.transferwise.urllocale;

import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Map;

import static com.transferwise.urllocale.UrlLocaleExtractorFilter.URL_LOCALE_ATTRIBUTE;

public class UrlLocaleResolver implements LocaleResolver {
    private final Map<String, Locale> urlLocaleToLocaleMapping;
    private final Locale fallback;

    public UrlLocaleResolver(Map<String, Locale> urlLocaleToLocaleMapping) {
        this(urlLocaleToLocaleMapping, null);
    }

    public UrlLocaleResolver(Map<String, Locale> urlLocaleToLocaleMapping, Locale fallback) {
        this.urlLocaleToLocaleMapping = urlLocaleToLocaleMapping;
        this.fallback = fallback;
    }

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String locale = (String) request.getAttribute(URL_LOCALE_ATTRIBUTE);

        return urlLocaleToLocaleMapping.getOrDefault(locale, fallback);
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        throw new UnsupportedOperationException();
    }
}
