package com.transferwise.urllocale;

import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

import static com.transferwise.urllocale.UrlLocaleExtractorFilter.URL_LOCALE_ATTRIBUTE;

public class UrlLocaleResolver implements LocaleResolver {
    private final Locale fallback;

    public UrlLocaleResolver(Locale fallback) {
        this.fallback = fallback;
    }

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        Locale locale = (Locale) request.getAttribute(URL_LOCALE_ATTRIBUTE);
        return locale != null ? locale : fallback;
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        request.setAttribute(URL_LOCALE_ATTRIBUTE, locale);
    }
}
