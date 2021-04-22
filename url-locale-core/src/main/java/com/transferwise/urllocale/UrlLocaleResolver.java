package com.transferwise.urllocale;

import static com.transferwise.urllocale.UrlLocaleExtractorFilter.URL_LOCALE_ATTRIBUTE;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.LocaleResolver;

public class UrlLocaleResolver implements LocaleResolver {

    private final Locale fallback;
    private final Map<String, Locale> urlLocaleToLocaleMapping;
    private final boolean langParameterEnabled;
    private final Set<String> supportedLanguages;

    public UrlLocaleResolver(Map<String, Locale> urlLocaleToLocaleMapping, Locale fallback, boolean langParameterEnabled) {
        this.fallback = fallback;
        this.urlLocaleToLocaleMapping = urlLocaleToLocaleMapping;
        this.supportedLanguages = urlLocaleToLocaleMapping.values()
            .stream()
            .map(Locale::getLanguage)
            .collect(Collectors.toSet());
        this.langParameterEnabled = langParameterEnabled;
    }

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String urlLocaleStr = (String) request.getAttribute(URL_LOCALE_ATTRIBUTE);
        Locale urlLocale = urlLocaleToLocaleMapping.getOrDefault(urlLocaleStr, fallback);

        String lang = resolveLangParameter(request);
        if (lang != null) {
            return new Locale(lang, urlLocale.getCountry());
        }

        return urlLocale;
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        throw new UnsupportedOperationException();
    }

    private String resolveLangParameter(HttpServletRequest request) {
        if (!langParameterEnabled) {
            return null;
        }

        String lang = request.getParameter("lang");

        if (lang == null) {
            return null;
        }

        if (!supportedLanguages.contains(lang.toLowerCase())) {
            return null;
        }

        return lang;
    }
}
