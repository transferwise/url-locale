package com.transferwise.urllocale.hreflang;

import java.net.URI;
import java.net.URISyntaxException;

public class LocalisedLink implements Comparable<LocalisedLink> {
    private String hreflang;
    private String href;

    public LocalisedLink(Hreflang hreflang, String domain, String urlLocale, String resource) {
        this(hreflang, domain, urlLocale, resource, null);
    }

    public LocalisedLink(Hreflang hreflang, String domain, String urlLocale, String resource, String queryString) {
        this.hreflang = hreflang.getValue();
        this.href = parseHref(domain, urlLocale, resource, queryString);
    }

    public String getHreflang() {
        return hreflang;
    }

    public String getHref() {
        return href;
    }

    private String parseHref(String domain, String urlLocale, String resource, String queryString) {
        try {
            if (!resource.startsWith("/")) {
                resource = "/" + resource;
            }
            String[] splitForProtocol = domain.split("://");
            String protocol = splitForProtocol[0];
            String[] splitForHostname = splitForProtocol[1].split(":");
            String hostname = splitForHostname[0];
            int port = "".equals(splitForHostname[1]) ? -1 : Integer.parseInt(splitForHostname[1]);

            String path = "/" + urlLocale + resource;
            URI uri = new URI(protocol, null, hostname, port, path, queryString, null);
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
