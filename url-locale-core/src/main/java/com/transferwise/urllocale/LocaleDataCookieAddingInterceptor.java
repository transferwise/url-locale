package com.transferwise.urllocale;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Component
public class LocaleDataCookieAddingInterceptor implements HandlerInterceptor {

    private final String cookieName;
    private final int cookieMaxAge;

    private final String FIVE_CHARACTER_LANGUAGE = "zh_HK";

    public LocaleDataCookieAddingInterceptor(String cookieName, int cookieMaxAge){
        this.cookieName = cookieName;
        this.cookieMaxAge = cookieMaxAge;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (hasLocaleDataCookie(request)) {
            return true;
        }
        LocaleContext localeContext = LocaleContextHolder.getLocaleContext();
        if (localeContext != null && localeContext.getLocale() != null) {
            String language = getLanguageFromLocaleContext(localeContext);
            Cookie localeDataCookie = new Cookie(cookieName, language);
            localeDataCookie.setMaxAge(cookieMaxAge);
            localeDataCookie.setPath("/");
            response.addCookie(localeDataCookie);
        }
        return true;
    }

    private String getLanguageFromLocaleContext(LocaleContext localeContext) {
        if (localeContext.getLocale().toLanguageTag().replace("-", "_").equalsIgnoreCase(FIVE_CHARACTER_LANGUAGE)) {
            return FIVE_CHARACTER_LANGUAGE;
        } else {
            return localeContext.getLocale().getLanguage();
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

    private boolean hasLocaleDataCookie(HttpServletRequest request) {
        return request.getCookies() != null && Arrays.stream(request.getCookies())
            .anyMatch(cookie -> cookieName.equals(cookie.getName()));
    }

}
