package com.transferwise.urllocale

import spock.lang.Specification

import javax.servlet.http.HttpServletRequest

class UrlLocaleResolverSpec extends Specification {
    private static final FALLBACK = Locale.UK

    private UrlLocaleResolver resolver
    private HttpServletRequest request

    def setup() {
        resolver = new UrlLocaleResolver(FALLBACK)
        request = Mock(HttpServletRequest)
    }

    def 'it picks locale from request attribute'() {
        given:
        storedLocaleAttributeIs(Locale.US)

        expect:
        resolvedLocaleIs(Locale.US)
    }

    def 'it fallbacks to default locale'() {
        expect:
        resolvedLocaleIs(Locale.UK)
    }

    private storedLocaleAttributeIs(Locale l) {
        request.getAttribute(UrlLocaleExtractorFilter.URL_LOCALE_ATTRIBUTE) >> l
    }

    private boolean resolvedLocaleIs(Locale l) {
        resolver.resolveLocale(request) == l
    }
}
