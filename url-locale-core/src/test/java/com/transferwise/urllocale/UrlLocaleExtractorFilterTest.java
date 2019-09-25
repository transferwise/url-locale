package com.transferwise.urllocale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static com.transferwise.urllocale.UrlLocaleExtractorFilter.*;
import static com.transferwise.urllocale.UrlLocaleExtractorFilter.URL_LOCALE_ATTRIBUTE;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UrlLocaleExtractorFilterTest {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain chain;
    private UrlLocaleExtractorFilter filter;
    private Set<String> supportedUrlLocales = new HashSet<>();

    @BeforeEach
    void setUp() {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        chain = mock(FilterChain.class);
        filter = new UrlLocaleExtractorFilter(supportedUrlLocales);
    }

    @ParameterizedTest(name = "With path \"{0}\" locale should be \"{1}\"")
    @CsvSource({
        "/gb/path, gb",
        "/gb/, gb",
        "/zh-hk/, zh-hk",
        "/es/some/path, es",
    })
    void itShouldSetRequestUrlLocaleAttribute(String path, String expectedUrlLocale) {
        whenUrlLocaleMappingConfigured("gb");
        whenUrlLocaleMappingConfigured("es");
        whenUrlLocaleMappingConfigured("zh-hk");

        whenPathIs(path);

        doFilter();

        assertUrlLocaleAttributeEquals(expectedUrlLocale);
    }

    @ParameterizedTest(name = "Path \"{0}\" should not set urlLocale attribute")
    @CsvSource({
        "/gb",
        "/esp",
        "/z/",
        "/zhhk/",
        "/zhhhk/",
    })
    void itShouldNotSetUrlLocaleAttribute(String path) {
        whenUrlLocaleMappingConfigured("gb");
        whenUrlLocaleMappingConfigured("zh-hk");
        whenPathIs(path);

        doFilter();

        assertUrlLocaleAttributeIsNotSet();
    }

    @ParameterizedTest
    @CsvSource({
        "/es/",
        "/es/path",
        "/xx/",
        "/gb/",
    })
    void itShouldFailToMapUnrecognisedUrlLocale(String path) {
        whenPathIs(path);

        doFilter();

        assertNotFoundErrorIsSent();
    }

    private void assertNotFoundErrorIsSent() {
        try {
            verify(response, times(1)).sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (IOException e) {
            fail();
        }
    }

    private void whenUrlLocaleMappingConfigured(String mapping) {
        supportedUrlLocales.add(mapping);
    }

    private void whenPathIs(String path) {
        when(request.getServletPath()).thenReturn(path);
    }

    private void doFilter() {
        try {
            filter.doFilter(request, response, chain);
        } catch (IOException | ServletException e) {
            fail();
        }
    }

    private void assertUrlLocaleAttributeEquals(String urlLocale) {
        verify(request, times(1)).setAttribute(URL_LOCALE_ATTRIBUTE, urlLocale);
        verify(request, times(1)).setAttribute(LEGACY_LOCALE_ATTRIBUTE, urlLocale);
    }

    private void assertUrlLocaleAttributeIsNotSet() {
        verify(request, times(0)).setAttribute(any(), any());
    }
}
