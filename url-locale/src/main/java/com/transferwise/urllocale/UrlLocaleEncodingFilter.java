package com.transferwise.urllocale;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

public class UrlLocaleEncodingFilter implements Filter {
    private final Map<Locale, String> localeMapping;

    UrlLocaleEncodingFilter(Map<Locale, String> localeMapping) {
        this.localeMapping = localeMapping;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(
        ServletRequest request,
        ServletResponse response,
        FilterChain chain
    ) throws IOException, ServletException {
        chain.doFilter(request, new LocaleUrlWrappedResponse((HttpServletResponse) response, localeMapping));
    }

    @Override
    public void destroy() {
    }

    static class LocaleUrlWrappedResponse extends HttpServletResponseWrapper {
        private final HttpServletResponse response;
        private final Map<Locale, String> localeMapping;

        LocaleUrlWrappedResponse(HttpServletResponse response, Map<Locale, String> localeMapping) {
            super(response);
            this.response = response;
            this.localeMapping = localeMapping;
        }

        @Override
        @SuppressWarnings("deprecation")
        public String encodeUrl(String url) {
            return prefixMapping(url);
        }

        @Override
        public String encodeURL(String url) {
            return prefixMapping(url);
        }

        private String prefixMapping(String url) {
            if (url.startsWith("/")) {
                String mapping = localeMapping.get(getResponse().getLocale());
                return response.encodeURL("/" + mapping + url);
            }

            return response.encodeURL(url);
        }
    }
}
