package com.transferwise.urllocale

import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse

class UrlLocaleEncodingFilterSpec extends Specification {
    private UrlLocaleEncodingFilter.LocaleUrlWrappedResponse response
    private HttpServletResponse wrappedResponse

    def setup() {
        wrappedResponse = Mock(HttpServletResponse)
        wrappedResponse.encodeURL(_) >> { it[0] }
        response = new UrlLocaleEncodingFilter.LocaleUrlWrappedResponse(wrappedResponse, [(Locale.UK): 'gb'])
    }

    @Unroll
    def 'it should process #url to #expectedUrl'() {
        given:
            wrappedResponse.getLocale() >> Locale.UK

        when:
            def processedUrl = response.encodeURL(url)

        then:
            processedUrl == expectedUrl

        where:
            url                     | expectedUrl
            '/absolute'             | '/gb/absolute'
            'http://example.com'    | 'http://example.com'
            'relative/path'         | 'relative/path'
    }
}
