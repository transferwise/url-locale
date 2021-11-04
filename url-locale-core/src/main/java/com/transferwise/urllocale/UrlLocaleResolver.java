package com.transferwise.urllocale;

import static com.transferwise.urllocale.UrlLocaleExtractorFilter.URL_LOCALE_ATTRIBUTE;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

    private static final Map<String, String> fiveCharacterLanguages;

    static {
        fiveCharacterLanguages  = new HashMap<>();
        fiveCharacterLanguages.put("hk", "zh_HK");
    }

    public UrlLocaleResolver(Map<String, Locale> urlLocaleToLocaleMapping, Locale fallback, boolean langParameterEnabled) {
        this.fallback = fallback;
        this.urlLocaleToLocaleMapping = urlLocaleToLocaleMapping;
        this.supportedLanguages = urlLocaleToLocaleMapping.values()
            .stream()
            .map(locale -> getLanguages(locale))
            .collect(Collectors.toSet());
        this.langParameterEnabled = langParameterEnabled;
    }

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String urlLocaleStr = (String) request.getAttribute(URL_LOCALE_ATTRIBUTE);
        Locale locale = urlLocaleToLocaleMapping.getOrDefault(urlLocaleStr, fallback);

        String lang = resolveLangParameter(request);
        if (lang != null) {
            locale = new Locale(lang, locale.getCountry());
        }

        return locale;
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

        lang = lang.replace("-", "_");

        if (!supportedLanguages.contains(lang.toLowerCase())) {
            return null;
        }

        return lang;
    }

    private String getLanguages(Locale locale) {
        String localeCountry = locale.getCountry().toLowerCase();
        if (fiveCharacterLanguages.containsKey(localeCountry)) {
            return fiveCharacterLanguages.get(localeCountry).toLowerCase();
        }
        return locale.getLanguage();
    }
}
