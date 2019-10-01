package com.transferwise.urllocale.hreflang;

import java.util.Map;

public class HreflangConfig {
    private Map<Hreflang, String> mapping;
    private String xDefault;

    public HreflangConfig(Map<Hreflang, String> hreflangMap, String xDefault) {
        this.mapping = hreflangMap;
        this.xDefault = xDefault;
    }

    public Map<Hreflang, String> getMapping() {
        return mapping;
    }

    public String getxDefault() {
        return this.xDefault;
    }
}
