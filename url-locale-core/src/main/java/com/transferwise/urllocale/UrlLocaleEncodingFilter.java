package com.transferwise.urllocale;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

import static com.transferwise.urllocale.UrlLocaleExtractorFilter.URL_LOCALE_MAPPING_ATTRIBUTE;

public class UrlLocaleEncodingFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        chain.doFilter(request, new LocaleUrlWrappedResponse(
                (HttpServletRequest) request,
                (HttpServletResponse) response
        ));
    }

    @Override
    public void destroy() {
    }

    private static class LocaleUrlWrappedResponse extends HttpServletResponseWrapper {
        private final HttpServletRequest request;
        private final HttpServletResponse response;

        LocaleUrlWrappedResponse(HttpServletRequest request, HttpServletResponse response) {
            super(response);
            this.request = request;
            this.response = response;
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
            String mapping = (String) request.getAttribute(URL_LOCALE_MAPPING_ATTRIBUTE);
            if (url.startsWith("/") && mapping != null) {
                return response.encodeURL("/" + mapping + url);
            }

            return response.encodeURL(url);
        }
    }
}
