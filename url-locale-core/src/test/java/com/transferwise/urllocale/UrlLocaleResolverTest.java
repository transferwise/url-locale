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

    @ParameterizedTest(name = "Mapping \"{0}\" should match locale \"{1}\"")
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
        UrlLocaleResolver resolver = new UrlLocaleResolver(urlLocaleToLocaleMapping, Locale.UK, false);

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
            () -> resolver.setLocale(new MockHttpServletRequest(), new MockHttpServletResponse(), Locale.UK)
        );
    }

    @ParameterizedTest
    @CsvSource({
        "de, ,   de",
        "de, es, es",
        "xx, ,   en",  // invalid url locale, fallback to default language
        "de, xx, de",  // invalid lang param, fallback to url locale language
        "de, '', de",  // empty lang param
        "de, ,   de",  // null lang param
    })
    void itShouldResolveLangParameter(String urlLocale, String langParam, String expectedLanguage) {
        Map<String, Locale> urlLocaleToLocaleMapping = new HashMap<>();
        urlLocaleToLocaleMapping.put("de", new Locale("de", "DE"));
        urlLocaleToLocaleMapping.put("es", new Locale("es", "ES"));
        UrlLocaleResolver resolver = new UrlLocaleResolver(urlLocaleToLocaleMapping, Locale.UK, true);

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setAttribute(URL_LOCALE_ATTRIBUTE, urlLocale);
        mockRequest.setParameter("lang", langParam);

        String actualLanguage = resolver.resolveLocale(mockRequest).getLanguage();

        assertThat(actualLanguage).isEqualTo(expectedLanguage);
    }
}
