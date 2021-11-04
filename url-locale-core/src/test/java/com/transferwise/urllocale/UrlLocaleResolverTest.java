package com.transferwise.urllocale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.transferwise.urllocale.UrlLocaleExtractorFilter.URL_LOCALE_ATTRIBUTE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class UrlLocaleResolverTest {

    @ParameterizedTest(name = "It should resolve url locale /{0}/ to locale [{1}]")
    @CsvSource({
        "zh-hk, zh-HK",
        "de, de-DE",
        "it, it-IT",
        ", en-GB",
    })
    void itShouldResolveLocale(String urlLocale, String expectedLocale) {
        Map<String, Locale> urlLocaleToLocaleMapping = new HashMap<>();
        urlLocaleToLocaleMapping.put("de", Locale.GERMANY);
        urlLocaleToLocaleMapping.put("it", Locale.ITALY);
        urlLocaleToLocaleMapping.put("zh-hk", new Locale("zh", "HK"));
        UrlLocaleResolver resolver = new UrlLocaleResolver(
            urlLocaleToLocaleMapping,
            Locale.UK,
            false
        );

        Locale actualLocale = resolver.resolveLocale(requestWithUrlLocaleAttribute(urlLocale));

        assertThat(actualLocale).isEqualTo(Locale.forLanguageTag(expectedLocale));
    }

    private HttpServletRequest requestWithUrlLocaleAttribute(String locale) {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setAttribute(URL_LOCALE_ATTRIBUTE, locale);
        return mockRequest;
    }

    @Test
    void itShouldNotSupportLocaleChange() {
        UrlLocaleResolver resolver = new UrlLocaleResolver(new HashMap<>(), Locale.UK, false);
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(
            () -> resolver.setLocale(new MockHttpServletRequest(), new MockHttpServletResponse(), new Locale("en"))
        );
    }

    @ParameterizedTest(name = "It should resolve lang parameter [{1}] for url locale /{0}/ to [{2}]")
    @CsvSource({
        "de, de, de",
        "de, es, es",
        "de, ES, es",
        "de, xx, de",  // invalid lang param
        "de, '', de",  // empty lang param
        "de, ,   de",  // null lang param
        "xx, ,   en",  // invalid url locale, fallback to default language
    })
    void itShouldResolveLangParameter(String urlLocale, String langParam, String expectedLanguage) {
        Map<String, Locale> urlLocaleToLocaleMapping = new HashMap<>();
        urlLocaleToLocaleMapping.put("de", new Locale("de", "DE"));
        urlLocaleToLocaleMapping.put("es", new Locale("es", "ES"));
        UrlLocaleResolver resolver = new UrlLocaleResolver(
            urlLocaleToLocaleMapping,
            new Locale("en"),
            true
        );

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setAttribute(URL_LOCALE_ATTRIBUTE, urlLocale);
        mockRequest.setParameter("lang", langParam);

        String actualLanguage = resolver.resolveLocale(mockRequest).getLanguage();

        assertThat(actualLanguage).isEqualTo(expectedLanguage);
    }

    @ParameterizedTest(name = "It should resolve lang parameter [{1}] for url locale /{0}/ to [{1}]")
    @CsvSource({
            "de, zh-hk, zh_hk",
            "de, zh_HK, zh_hk",
    })
    void itShouldResolveFiveCharacterLangParameters(String urlLocale, String langParam, String expectedLanguage) {
        Map<String, Locale> urlLocaleToLocaleMapping = new HashMap<>();
        urlLocaleToLocaleMapping.put("de", new Locale("de", "DE"));
        urlLocaleToLocaleMapping.put("es", new Locale("es", "ES"));
        urlLocaleToLocaleMapping.put("zh-hk", new Locale("zh", "HK"));
        UrlLocaleResolver resolver = new UrlLocaleResolver(
                urlLocaleToLocaleMapping,
                new Locale("en"),
                true
        );

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setAttribute(URL_LOCALE_ATTRIBUTE, urlLocale);
        mockRequest.setParameter("lang", langParam);

        String actualLanguage = resolver.resolveLocale(mockRequest).getLanguage();

        assertThat(actualLanguage).isEqualTo(expectedLanguage);
    }

    @Test
    void itShouldIgnoreLangParameterWhenNotEnabled() {
        Map<String, Locale> urlLocaleToLocaleMapping = new HashMap<>();
        urlLocaleToLocaleMapping.put("de", new Locale("de", "DE"));
        urlLocaleToLocaleMapping.put("es", new Locale("es", "ES"));
        UrlLocaleResolver resolver = new UrlLocaleResolver(
            urlLocaleToLocaleMapping,
            new Locale("en"),
            false
        );

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setAttribute(URL_LOCALE_ATTRIBUTE, "de");
        mockRequest.setParameter("lang", "es");

        String actualLanguage = resolver.resolveLocale(mockRequest).getLanguage();

        assertThat(actualLanguage).isEqualTo("de");
    }
}
