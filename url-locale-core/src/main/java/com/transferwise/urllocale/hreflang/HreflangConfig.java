package com.transferwise.urllocale.hreflang;

import java.util.Map;

public class HreflangConfig {
    private Map<Hreflang, String> hreflangToUrlLocaleMapping;
    private String xDefault;

    public HreflangConfig(Map<Hreflang, String> hreflangToUrlLocaleMap, String xDefault) {
        this.hreflangToUrlLocaleMapping = hreflangToUrlLocaleMap;
        this.xDefault = xDefault;
    }

    public Map<Hreflang, String> getHreflangToUrlLocaleMapping() {
        return hreflangToUrlLocaleMapping;
    }

    public String getxDefault() {
        return this.xDefault;
    }
}
