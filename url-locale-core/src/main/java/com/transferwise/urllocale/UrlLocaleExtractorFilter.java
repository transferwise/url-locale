package com.transferwise.urllocale;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlLocaleExtractorFilter implements Filter {
    static final String LOCALE_ATTRIBUTE = "locale";
    private static final Pattern URL_PATTERN = Pattern.compile("^/([a-z]{2})/.*$");

    private Set<String> allowedLocaleMappings;

    public UrlLocaleExtractorFilter(Set<String> allowedLocaleMappings) {
        this.allowedLocaleMappings = allowedLocaleMappings;
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

        Matcher matcher = URL_PATTERN.matcher(req.getServletPath());
        if (matcher.matches()) {
            String mapping = matcher.group(1);
            if (allowedLocaleMappings.contains(mapping)) {
                request.setAttribute(LOCALE_ATTRIBUTE, mapping);
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
