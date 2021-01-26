package com.transferwise.urllocale.hreflang;

import java.util.List;
import java.util.stream.Collectors;

public class LocalisedLinkFactory {
    private final HreflangConfig hreflangConfig;

    public LocalisedLinkFactory(HreflangConfig hreflangConfig) {
        this.hreflangConfig = hreflangConfig;
    }

    public List<LocalisedLink> linksForResource(String domain, String resource) {
        return linksForResource(domain, resource, null);
    }

    public List<LocalisedLink> linksForResource(String domain, String resource, String queryString) {

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
