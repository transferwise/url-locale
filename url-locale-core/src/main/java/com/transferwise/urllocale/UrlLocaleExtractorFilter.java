package com.transferwise.urllocale;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static jakarta.servlet.http.HttpServletResponse.*;

public class UrlLocaleExtractorFilter implements Filter {

    @Deprecated // Prefer URL_LOCALE_ATTRIBUTE. Not removing as likely to be used in templates that may not be caught in dev.
    static final String LEGACY_LOCALE_ATTRIBUTE = "locale";
    public static final String URL_LOCALE_ATTRIBUTE = "urlLocale";
    private static final Pattern PATH_PATTERN = Pattern.compile("^/(api/)?([a-z]{2}(-[a-z]{2})?)/.*$");

    private Set<String> supportedUrlLocales;

    public UrlLocaleExtractorFilter(Set<String> supportedUrlLocales) {
        this.supportedUrlLocales = supportedUrlLocales;
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
            String urlLocale = matcher.group(2);
            if (!supportedUrlLocales.contains(urlLocale)) {
                ((HttpServletResponse) response).sendError(SC_NOT_FOUND);
                return;
            }
            request.setAttribute(URL_LOCALE_ATTRIBUTE, urlLocale);
            request.setAttribute(LEGACY_LOCALE_ATTRIBUTE, urlLocale);
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
