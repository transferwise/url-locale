package com.transferwise.urllocale.hreflang;

import java.util.List;
import java.util.stream.Collectors;

public class LocalisedLinkFactory {
    private final String domain;
    private final HreflangConfig hreflangConfig;

    public LocalisedLinkFactory(String domain, HreflangConfig hreflangConfig) {
        this.domain = domain;
        this.hreflangConfig = hreflangConfig;
    }

    public List<LocalisedLink> linksForResource(String resource, String queryString) {

        List<LocalisedLink> list = hreflangConfig.getHreflangToUrlLocaleMapping().entrySet().stream()
                .map(e -> new LocalisedLink(e.getKey(), domain, e.getValue(), resource, queryString))
                .sorted()
                .collect(Collectors.toList());

        String xDefault = hreflangConfig.getxDefault();
        if (xDefault != null) {
            list.add(new LocalisedLink(Hreflang.fromString("x-default"), domain, xDefault, resource, queryString));
        }
        return list;
    }
}
