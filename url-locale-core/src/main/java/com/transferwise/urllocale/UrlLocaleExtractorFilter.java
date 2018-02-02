package com.transferwise.urllocale;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlLocaleExtractorFilter implements Filter {
    static final String URL_LOCALE_ATTRIBUTE = "urlLocale";
    static final String URL_LOCALE_MAPPING_ATTRIBUTE = "urlLocaleMapping";
    private static final Pattern URL_PATTERN = Pattern.compile("^/([a-z]{2})(/.*)$");

    private final Map<String, Locale> localeMapping;

    public UrlLocaleExtractorFilter(Map<String, Locale> localeMapping) {
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
        HttpServletRequest req = (HttpServletRequest) request;

        Matcher matcher = URL_PATTERN.matcher(req.getServletPath());
        if (matcher.matches()) {
            String mapping = matcher.group(1);
            if (!localeMapping.containsKey(mapping)) {
                chain.doFilter(request, response);
                return;
            }
            request.setAttribute(URL_LOCALE_MAPPING_ATTRIBUTE, mapping);
            request.setAttribute(URL_LOCALE_ATTRIBUTE, localeMapping.get(mapping));
            RequestDispatcher dispatcher = request.getRequestDispatcher(matcher.group(2));
            dispatcher.forward(request, response);
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
    }
}
