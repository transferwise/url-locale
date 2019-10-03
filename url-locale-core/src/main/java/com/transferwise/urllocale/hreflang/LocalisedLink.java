package com.transferwise.urllocale.hreflang;

import java.net.URI;
import java.net.URISyntaxException;

public class LocalisedLink implements Comparable<LocalisedLink> {
    private String hreflang;
    private String href;

    public String getHreflang() {
        return hreflang;
    }

    public String getHref() {
        return href;
    }

    LocalisedLink(Hreflang hreflang, String domain, String urlLocale, String resource) {
        this.hreflang = hreflang.getValue();
        this.href = parseHref(domain, urlLocale, resource);
    }

    private String parseHref(String domain, String urlLocale, String resource) {
        try {
            if (!resource.startsWith("/")) {
                resource = "/" + resource;
            }
            String[] split = domain.split("://");
            String protocol = split[0];
            String hostname = split[1];
            String path = "/" + urlLocale + resource;
            URI uri = new URI(protocol, null, hostname, -1, path, null, null);
            return uri.toString();

        } catch (URISyntaxException exc) {
            throw new IllegalArgumentException(exc.getMessage());
        } catch (IndexOutOfBoundsException exc) {
            throw new IllegalArgumentException(String.format("Invalid domain: %s", domain));
        }
    }

    public String toString() {
        return String.format("LocalisedLink{hreflang=%s,href=%s", this.hreflang, this.href);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof LocalisedLink &&
                ((LocalisedLink) o).hreflang.equalsIgnoreCase(hreflang) &&
                ((LocalisedLink) o).href.equalsIgnoreCase(href);
    }

    @Override
    public int compareTo(LocalisedLink o) {
        return String.CASE_INSENSITIVE_ORDER.compare(hreflang, o.hreflang);
    }
}
