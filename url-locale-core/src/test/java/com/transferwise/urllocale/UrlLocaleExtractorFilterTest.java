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
        "/es/some/path, es",
    })
    void itShouldSetRequestUrlLocaleAttribute(String path, String expectedUrlLocale) {
        whenUrlLocaleMappingConfigured("gb");
        whenUrlLocaleMappingConfigured("es");
        whenPathIs(path);

        doFilter();

        assertUrlLocaleAttributeEquals(expectedUrlLocale);
    }

    @ParameterizedTest(name = "Path \"{0}\" should not set urlLocale attribute")
    @CsvSource({
        "/gb",
        "/esp"
    })
    void itShouldNotSetUrlLocaleAttribute(String path) {
        whenUrlLocaleMappingConfigured("gb");
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

    @ParameterizedTest
    @CsvSource({
        "/es/,,/gb/",
        "/es/,queryParam=1,/gb/?queryParam=1",
        "/es/path,,/gb/path",
        "/es/path,queryParam=1,/gb/path?queryParam=1",
        "/xx/,,/gb/",
        "/xx/,queryParam=1,/gb/?queryParam=1",
    })
    void itShouldRedirectUnrecognisedUrlLocale(String path, String queryParamsString, String expectedRedirectPath) {
        filter = new UrlLocaleExtractorFilter(supportedUrlLocales, "gb");
        whenPathWithQueryParamsIs(path, queryParamsString);

        doFilter();

        assertRedirected(expectedRedirectPath);
    }

    private void assertNotFoundErrorIsSent() {
        try {
            verify(response, times(1)).sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (IOException e) {
            fail();
        }
    }

    private void assertRedirected(String expectedRedirectPath) {
        verify(response, times(1)).setStatus(301);
        verify(response, times(1)).setHeader("Location", expectedRedirectPath);
    }

    private void whenUrlLocaleMappingConfigured(String mapping) {
        supportedUrlLocales.add(mapping);
    }

    private void whenPathIs(String path) {
        whenPathWithQueryParamsIs(path, null);
    }

    private void whenPathWithQueryParamsIs(String pathWithoutQueryParameters, String queryParametersString) {
        when(request.getServletPath()).thenReturn(pathWithoutQueryParameters);
        when(request.getQueryString()).thenReturn(queryParametersString);
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
    }

    private void assertUrlLocaleAttributeIsNotSet() {
        verify(request, times(0)).setAttribute(any(), any());
    }
}
