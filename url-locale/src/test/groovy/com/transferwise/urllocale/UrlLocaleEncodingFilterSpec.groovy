package com.transferwise.urllocale

import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class UrlLocaleEncodingFilterSpec extends Specification {
    private UrlLocaleEncodingFilter.LocaleUrlWrappedResponse response
    private HttpServletResponse wrappedResponse
    private HttpServletRequest request

    def setup() {
        wrappedResponse = Mock(HttpServletResponse)
        wrappedResponse.encodeURL(_) >> { it[0] }

        request = Mock(HttpServletRequest)
        response = new UrlLocaleEncodingFilter.LocaleUrlWrappedResponse(request, wrappedResponse)
    }

    @Unroll
    def 'it should process #url to #expectedUrl'() {
        given:
            request.getAttribute(UrlLocaleExtractorFilter.URL_LOCALE_MAPPING_ATTRIBUTE) >> 'gb'

        when:
            def processedUrl = response.encodeURL(url)

        then:
            processedUrl == expectedUrl

        where:
            url                  | expectedUrl
            '/absolute'          | '/gb/absolute'
            'http://example.com' | 'http://example.com'
            'relative/path'      | 'relative/path'
    }

    def 'it should not process request URLs without mapping'() {
        expect:
        response.encodeURL('/gb/url') == '/gb/url'
    }
}
