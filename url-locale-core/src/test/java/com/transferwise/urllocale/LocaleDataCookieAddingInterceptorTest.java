package com.transferwise.urllocale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LocaleDataCookieAddingInterceptorTest {

    private static final String COOKIE_NAME = "localeData";
    private static final int COOKIE_MAX_AGE = 31557600;

    private LocaleDataCookieAddingInterceptor localeDataCookieAddingInterceptor;
    private final Controller handler = mock(Controller.class);
    private final HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);

    @BeforeEach
    void setUp() {
        localeDataCookieAddingInterceptor = new LocaleDataCookieAddingInterceptor(COOKIE_NAME, COOKIE_MAX_AGE);
    }

    @ParameterizedTest(name = "Language tag \"{0}\" should match locale \"{1}\"")
    @CsvSource({
        "zh-hk, zh_HK",
        "de-DE, de_DE",
        "it-IT, it_IT"
    })
    void itAddsCookieIfOneDoesNotExist(String localeContextLanguageTag, String expectedCookieValue) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        CookieAwareHttpServletResponse response = new CookieAwareHttpServletResponse(httpServletResponse);
        LocaleContextHolder.setLocale(Locale.forLanguageTag(localeContextLanguageTag));

        localeDataCookieAddingInterceptor.preHandle(request, response, handler);
        assertEquals(expectedCookieValue, response.getCookies().get(0).getValue());
    }

    @Test
    void itDoesNotAddCookieIfOneExistsAlready() {
        Cookie[] requestCookies = new Cookie[]{new Cookie(COOKIE_NAME, "irrelevant")};
        LocaleContextHolder.setLocale(Locale.ENGLISH);

    	HttpServletRequest request = mock(HttpServletRequest.class);
        CookieAwareHttpServletResponse response = new CookieAwareHttpServletResponse(httpServletResponse);
    	when(request.getCookies()).thenReturn(requestCookies);

    	localeDataCookieAddingInterceptor.preHandle(request, response, handler);
    	assertEquals(0, response.getCookies().size());
    }

    @Test
    void itDoesNotAddCookieIfLocaleIsNotProperlyResolved() {
        LocaleContextHolder.setLocale(null);

        HttpServletRequest request = mock(HttpServletRequest.class);
        CookieAwareHttpServletResponse response = new CookieAwareHttpServletResponse(httpServletResponse);

        localeDataCookieAddingInterceptor.preHandle(request, response, handler);
        assertEquals(0, response.getCookies().size());
    }

    private static class CookieAwareHttpServletResponse extends HttpServletResponseWrapper {

        private List<Cookie> cookies = new ArrayList<>();

        CookieAwareHttpServletResponse (HttpServletResponse aResponse) {
            super (aResponse);
        }

        @Override
        public void addCookie (Cookie aCookie) {
            cookies.add(aCookie);
            super.addCookie(aCookie);
        }

        public List<Cookie> getCookies () {
            return cookies;
        }
    }
}
