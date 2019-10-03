package com.transferwise.urllocale.hreflang;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LocalisedLinkFactoryTest {

    private static final String DOMAIN = "https://example.test";
    private static final HreflangConfig HREFLANG_CONFIG = mock(HreflangConfig.class);
    private static Map<Hreflang, String> hreflangToUrlLocaleMap = new HashMap<>();

    static {
        hreflangToUrlLocaleMap.put(Hreflang.fromString("fr"), "fr");
        hreflangToUrlLocaleMap.put(Hreflang.fromString("en-GB"), "gb");
        hreflangToUrlLocaleMap.put(Hreflang.fromString("zh-Hant-HK"), "zh-hk");
    }

    @Test
    void itReturnsAnEmptyCollectionIfNoHreflangMappingsAreProvided() {
        LocalisedLinkFactory factory = new LocalisedLinkFactory(DOMAIN, HREFLANG_CONFIG);

        when(HREFLANG_CONFIG.getHreflangToUrlLocaleMapping()).thenReturn(Collections.emptyMap());
        when(HREFLANG_CONFIG.getxDefault()).thenReturn(null);

        assertEquals(factory.linksForResource("/").size(), 0);
    }

    @Test
    void itCreatesXDefaultLink() {
        LocalisedLinkFactory factory = new LocalisedLinkFactory(DOMAIN, HREFLANG_CONFIG);

        when(HREFLANG_CONFIG.getHreflangToUrlLocaleMapping()).thenReturn(Collections.emptyMap());
        when(HREFLANG_CONFIG.getxDefault()).thenReturn("gb");

        assertEquals(factory.linksForResource("/").size(), 1);
    }

    @Test
    void itGeneratesLinksFromHreflangToUrlLocaleMapping() {

        LocalisedLinkFactory factory = new LocalisedLinkFactory(DOMAIN, HREFLANG_CONFIG);
        when(HREFLANG_CONFIG.getHreflangToUrlLocaleMapping()).thenReturn(hreflangToUrlLocaleMap);
        when(HREFLANG_CONFIG.getxDefault()).thenReturn(null);

        List<LocalisedLink> links = factory.linksForResource("/path");
        assertEquals(links.size(), 3);
        assertEquals(links.get(0).getHreflang(), "en-GB");
        assertEquals(links.get(1).getHreflang(), "fr");
        assertEquals(links.get(2).getHreflang(), "zh-Hant-HK");
    }

}

