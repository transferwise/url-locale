package com.transferwise.urllocale.hreflang;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LocalisedLinkTest {

    private static final Hreflang mockHreflang = mock(Hreflang.class);
    private static final String DOMAIN = "https://example.test";

    @BeforeEach
    void setUp() {
    }

    @Test
    void linksAreEqualIfTheyShareHreflangAndHrefAttributes() {
        when(mockHreflang.getValue()).thenReturn("en-GB");
        LocalisedLink link1 = new LocalisedLink(mockHreflang, DOMAIN, "gb", "/path", null);
        LocalisedLink link2 = new LocalisedLink(mockHreflang, DOMAIN, "gb", "/path", null);

        assertEquals(link1, link2);
    }

    @Test
    void equalityIsCaseInsensitive() {
        when(mockHreflang.getValue()).thenReturn("en-GB");
        LocalisedLink link1 = new LocalisedLink(mockHreflang, DOMAIN, "gb", "/path", null);
        LocalisedLink link2 = new LocalisedLink(mockHreflang, DOMAIN, "gb", "/PATH", null);

        assertEquals(link1, link2);
    }

    @Test
    void linksSortAlphabeticallyByHreflang() {
        when(mockHreflang.getValue()).thenReturn("en-GB");
        LocalisedLink link1 = new LocalisedLink(mockHreflang, DOMAIN, "gb", "/path", null);

        when(mockHreflang.getValue()).thenReturn("fr-FR");
        LocalisedLink link2 = new LocalisedLink(mockHreflang, DOMAIN, "fr", "/PATH", null);

        List<LocalisedLink> list = Stream.of(link2, link1).sorted().collect(Collectors.toList());

        assertEquals(list, Arrays.asList(link1, link2));
    }

    @Test
    void itThrowsExceptionIfDomainDoesNotIncludeProtocol() {
        when(mockHreflang.getValue()).thenReturn("en-GB");
        assertThrows(IllegalArgumentException.class,
                () -> new LocalisedLink(mockHreflang, "example.test", "gb", "/path", null));
    }

    @Test
    void itThrowsExceptionWhenDomainProtocolIsRelative() {
        when(mockHreflang.getValue()).thenReturn("en-GB");
        assertThrows(IllegalArgumentException.class,
                () -> new LocalisedLink(mockHreflang, "//example.test", "gb", "/path", null));
    }

    @Test
    void itThrowsExceptionWhenUrlLocaleIncludesLeadingSlash() {
        when(mockHreflang.getValue()).thenReturn("en-GB");
        assertThrows(IllegalArgumentException.class,
                () -> new LocalisedLink(mockHreflang, "//example.test", "/gb", "/path", null));
    }

    @Test
    void itThrowsExceptionWhenUrlLocaleIncludesTrailingSlash() {
        when(mockHreflang.getValue()).thenReturn("en-GB");
        assertThrows(IllegalArgumentException.class,
                () -> new LocalisedLink(mockHreflang, "//example.test", "gb/", "/path", null));
    }

    @Test
    void itPreservesQueryString() {
        when(mockHreflang.getValue()).thenReturn("en-GB");
        LocalisedLink link = new LocalisedLink(mockHreflang, "https://example.test", "gb", "/path", "a=1&b=2");
        assertEquals(link.getHref(), "https://example.test/gb/path?a=1&b=2");
    }
}
