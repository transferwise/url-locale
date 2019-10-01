package com.transferwise.urllocale.hreflang;

import java.util.List;
import java.util.stream.Collectors;

public class LocalisedLinkFactory {
    private final String domain = "https://transferwise.com";
    private final HreflangConfig hreflangConfig;

    public LocalisedLinkFactory(HreflangConfig hreflangConfig) {
        this.hreflangConfig = hreflangConfig;
    }

    public List<LocalisedLink> linksForResource(String resource) {

        List<LocalisedLink> list = hreflangConfig.getMapping().entrySet().stream()
                .map(e -> new LocalisedLink(e.getKey(), domain, e.getValue(), resource))
                .sorted()
                .collect(Collectors.toList());

        String xDefault = hreflangConfig.getxDefault();
        if (xDefault != null) {
            list.add(new LocalisedLink(Hreflang.fromString("x-default"), domain, xDefault, resource));
        }
        return list;
    }
}
