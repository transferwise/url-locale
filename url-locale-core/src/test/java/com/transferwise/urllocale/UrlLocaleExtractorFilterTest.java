package com.transferwise.urllocale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static com.transferwise.urllocale.UrlLocaleExtractorFilter.LOCALE_ATTRIBUTE;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

class UrlLocaleExtractorFilterTest {
    private HttpServletRequest request;
    private FilterChain chain;
    private UrlLocaleExtractorFilter filter;
    private Set<String> allowedLocaleMappings = new HashSet<>();

    @BeforeEach
    void setUp() {
        request = mock(HttpServletRequest.class);
        chain = mock(FilterChain.class);
        filter = new UrlLocaleExtractorFilter(allowedLocaleMappings);
    }

    @ParameterizedTest(name = "With path \"{0}\" locale should be \"{1}\"")
    @CsvSource({
        "/gb/path, gb",
        "/gb/, gb",
        "/es/some/path, es",
    })
    void itShouldSetRequestLocaleAttribute(String path, String expectedLocale) {
        whenLocaleMappingConfigured("gb");
        whenLocaleMappingConfigured("es");
        whenPathIs(path);

        doFilter();

        assertLocaleAttributeEquals(expectedLocale);
    }

    @ParameterizedTest(name = "Path \"{0}\" should not set locale attribute")
    @CsvSource({
        "/gb",
        "/esp",
        "/es/",
        "/es/path",
    })
    void itShouldNotSetLocaleAttribute(String path) {
        whenLocaleMappingConfigured("gb");
        whenPathIs(path);

        doFilter();

        assertLocaleAttributeIsNotSet();
    }

    private void whenLocaleMappingConfigured(String mapping) {
        allowedLocaleMappings.add(mapping);
    }

    private void whenPathIs(String path) {
        when(request.getServletPath()).thenReturn(path);
    }

    private void doFilter() {
        try {
            filter.doFilter(request, mock(ServletResponse.class), chain);
        } catch (IOException | ServletException e) {
            fail();
        }
    }

    private void assertLocaleAttributeEquals(String locale) {
        verify(request, times(1)).setAttribute(LOCALE_ATTRIBUTE, locale);
    }

    private void assertLocaleAttributeIsNotSet() {
        verify(request, times(0)).setAttribute(any(), any());
    }
}
