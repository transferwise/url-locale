package com.transferwise.urllocale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.transferwise.urllocale.UrlLocaleExtractorFilter.LOCALE_ATTRIBUTE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UrlLocaleResolverTest {

    private static final Locale FALLBACK = Locale.UK;
    private static final Map<String, Locale> LOCALE_MAPPING = new HashMap<String, Locale>() {{
        put("de", Locale.GERMANY);
        put("it", Locale.ITALY);
    }};

    private UrlLocaleResolver urlLocaleResolver;

    @BeforeEach
    void setUp() {
        urlLocaleResolver = new UrlLocaleResolver(LOCALE_MAPPING, FALLBACK);
    }

    @ParameterizedTest(name = "Mapping \"{0}\" should match locale \"{1}\"")
    @CsvSource({
        "de, de-DE",
        "it, it-IT",
        "xx, en-GB",
    })
    void itShouldResolveLocale(String locale, String expectedLocale) {
        Locale resolvedLocale = urlLocaleResolver.resolveLocale(requestWithLocaleAttribute(locale));

        assertEquals(Locale.forLanguageTag(expectedLocale), resolvedLocale);
    }

    private HttpServletRequest requestWithLocaleAttribute(String locale) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute(LOCALE_ATTRIBUTE)).thenReturn(locale);
        return request;
    }

    @Test
    void itShouldNotSupportLocaleChange() {
        assertThrows(UnsupportedOperationException.class, () -> urlLocaleResolver.setLocale(mock(HttpServletRequest.class), mock(HttpServletResponse.class), Locale.UK));
    }
}
