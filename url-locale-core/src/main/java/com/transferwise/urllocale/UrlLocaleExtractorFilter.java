package com.transferwise.urllocale;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

public class UrlLocaleExtractorFilter implements Filter {

    static final int DEFAULT_FALLBACK_STATUS_CODE = 301;
    static final String URL_LOCALE_ATTRIBUTE = "urlLocale";
    private static final Pattern PATH_PATTERN = Pattern.compile("^/([a-z]{2})/.*$");

    private Set<String> supportedUrlLocales;
    private String fallback;
    private int fallbackStatusCode;

    public UrlLocaleExtractorFilter(Set<String> supportedUrlLocales) {
        this(supportedUrlLocales, null);
    }

    public UrlLocaleExtractorFilter(Set<String> supportedUrlLocales, String fallback) {
        this(supportedUrlLocales, fallback, DEFAULT_FALLBACK_STATUS_CODE);
    }

    public UrlLocaleExtractorFilter(Set<String> supportedUrlLocales, String fallback, int fallbackStatusCode) {
        this.supportedUrlLocales = supportedUrlLocales;
        this.fallback = fallback;
        this.fallbackStatusCode = fallbackStatusCode;
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;

        Matcher matcher = PATH_PATTERN.matcher(req.getServletPath());
        if (matcher.matches()) {
            String urlLocale = matcher.group(1);
            if (!supportedUrlLocales.contains(urlLocale)) {
                HttpServletResponse res = (HttpServletResponse) response;
                if (fallback != null) {
                    String redirectUrl = generateRedirectUrl(req.getServletPath(), req.getQueryString(), matcher.start(1), matcher.end(1));
                    res.setStatus(fallbackStatusCode);
                    res.setHeader("Location", redirectUrl);
                    return;
                } else {
                    res.sendError(SC_NOT_FOUND);
                    return;
                }
            }

            request.setAttribute(URL_LOCALE_ATTRIBUTE, urlLocale);
        }

        chain.doFilter(request, response);
    }

    private String generateRedirectUrl(String requestPath, String queryString, int urlLocaleStartIndex, int urlLocaleEndIndex) {
        String redirectUrl = requestPath.substring(0, urlLocaleStartIndex) + fallback + requestPath.substring(urlLocaleEndIndex);
        if (queryString != null) {
            return redirectUrl + "?" + queryString;
        }
        return redirectUrl;
    }

    @Override
    public void destroy() {
    }
}
