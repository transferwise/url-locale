package com.transferwise.urllocale

import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.FilterChain
import javax.servlet.RequestDispatcher
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper
import javax.servlet.http.HttpServletResponse

import static UrlLocaleExtractorFilter.*

class UrlLocaleExtractorFilterSpec extends Specification {
    private UrlLocaleExtractorFilter filter
    private FakeRequest request
    private FilterChain filterChain
    private Map<String, Locale> mapping = [:]

    def setup() {
        filter = new UrlLocaleExtractorFilter(mapping)
        filterChain = Mock(FilterChain)
        request = new FakeRequest(Mock(HttpServletRequest), Mock(RequestDispatcher))
    }

    def 'it handles url locale'() {
        given:
        localeMappingExists('gb', Locale.UK)

        when:
        makeRequestTo('/gb/')

        then:
            request.attributes[URL_LOCALE_MAPPING_ATTRIBUTE] == 'gb'
            request.attributes[URL_LOCALE_ATTRIBUTE] == Locale.UK
            request.dispatchedUrl == '/'
    }

    def 'it does not process url locale twice'() {
        given:
        localeMappingExists('gb', Locale.UK)

        when:
        makeRequestTo('/gb/gb/')
        makeRequestTo('/gb/')//simulate forwarding

        then:
        interaction {
            filtersDownTheRequest()
        }
        request.dispatchedUrl == '/gb/'
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
        request.servletPath = path

        filter.doFilter(request, Mock(HttpServletResponse), filterChain)
    }

    private localeMappingExists(String value, Locale locale) {
        mapping[value] = locale
    }

    private filtersDownTheRequest() {
        1 * filterChain.doFilter(_, _)
    }

    static class FakeRequest extends HttpServletRequestWrapper {
        RequestDispatcher dispatcher
        Map<String, Object> attributes = [:]
        String dispatchedUrl
        String servletPath

        FakeRequest(HttpServletRequest request, RequestDispatcher dispatcher) {
            super(request)
            this.dispatcher = dispatcher
        }

        @Override
        String getServletPath() {
            servletPath
        }

        @Override
        void setAttribute(String key, Object value) {
            attributes[key] = value
        }

        @Override
        Object getAttribute(String key) {
            attributes[key]
        }

        @Override
        RequestDispatcher getRequestDispatcher(String dispatchedUrl) {
            this.dispatchedUrl = dispatchedUrl
            dispatcher
        }
    }
}
