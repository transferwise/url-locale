package com.transferwise.urllocale

import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.FilterChain
import javax.servlet.RequestDispatcher
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class UrlLocaleExtractorFilterSpec extends Specification {
    private UrlLocaleExtractorFilter filter
    private HttpServletRequest request
    private FilterChain filterChain
    private Map<String, Locale> mapping = [:]

    def setup() {
        filter = new UrlLocaleExtractorFilter(mapping)
        request = Mock(HttpServletRequest)
        filterChain = Mock(FilterChain)
    }

    def 'it handles url locale'() {
        given:
        localeMappingExists('gb', Locale.UK)

        when:
        makeRequestTo('/gb/')

        then:
        interaction {
            storesLocaleInAttribute(Locale.UK)
            dispatchesRequestTo('/')
        }
    }

    @Unroll
    def 'it filters down the request when no mapping available for path #path'() {
        given:
        localeMappingExists('gb', Locale.UK)

        when:
        makeRequestTo(path)

        then:
        interaction {
            filtersDownTheRequest()
        }

        where:
        path << ['/gb', '/us/', '/us/home', '/', '/home']
    }

    def makeRequestTo(path) {
        request.getServletPath() >> path
        filter.doFilter(request, Mock(HttpServletResponse), filterChain)
    }

    private localeMappingExists(String value, Locale locale) {
        mapping[value] = locale
    }

    private storesLocaleInAttribute(Locale locale) {
        1 * request.setAttribute(UrlLocaleExtractorFilter.URL_LOCALE_ATTRIBUTE, locale)
    }

    private dispatchesRequestTo(String path) {
        1 * request.getRequestDispatcher(path) >> {
            def dispatcher = Mock(RequestDispatcher)
            1 * dispatcher.forward(_, _)
            dispatcher
        }
    }

    private filtersDownTheRequest() {
        1 * filterChain.doFilter(_, _)
    }
}
