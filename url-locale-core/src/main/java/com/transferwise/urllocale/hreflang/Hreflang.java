package com.transferwise.urllocale.hreflang;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Hreflang {
    private String language;
    private String script;
    private String region;

    private static final Pattern PATTERN = Pattern.compile(
            "^((?<language>[a-z]{2})(-(?<script>[a-z]{4}))?(-(?<region>[a-z]{2}))?)$",
            Pattern.CASE_INSENSITIVE);

    private static final Set<String> VALID_LANGUAGES = new HashSet<>(Arrays.asList(Locale.getISOLanguages()));
    private static final Set<String> VALID_SCRIPTS = new HashSet<>(Arrays.asList(ISO15924LanguageScriptData.getISOScripts()));
    private static final Set<String> VALID_REGIONS = new HashSet<>(Arrays.asList(Locale.getISOCountries()));

    public String getValue() {
        if (language == null) {
            return "x-default";
        }
        return language
                + (script == null ? "" : "-" + script)
                + (region == null ? "" : "-" + region);
    }

    public static class Builder {
        private final String language;
        private String script = null;
        private String region = null;


        public Builder(String aLanguage) {
            String language = aLanguage.toLowerCase();
            if (!VALID_LANGUAGES.contains(language)) {
                throw new IllegalArgumentException("Invalid language: " + aLanguage);
            }
            this.language = language;
        }

        public Builder withScript(String aScript) {
            String script = aScript.substring(0,1).toUpperCase() + aScript.substring(1).toLowerCase();
            if (!VALID_SCRIPTS.contains(script)) {
                throw new IllegalArgumentException("Invalid script: " + aScript);
            }
            this.script = script;
            return this;
        }

        public Builder withRegion(String aRegion) {
            String region = aRegion.toUpperCase();
            if (!VALID_REGIONS.contains(region)) {
                throw new IllegalArgumentException("Invalid region: " + aRegion);
            }
            this.region = region;
            return this;
        }

        public Hreflang build() {
            return new Hreflang(language, script, region);
        }
    }

    private Hreflang(String language, String script, String region) {
        this.language = language;
        this.script = script;
        this.region = region;
    }

    private Hreflang() {
        this.language = null;
        this.script = null;
        this.region = null;
    }

    public static Hreflang fromString(String string) {
        if (string.equals("x-default")) {
            return new Hreflang();
        }
        Matcher matcher = PATTERN.matcher(string);
        if (matcher.matches()) {
            String language = matcher.group("language");
            String script = matcher.group("script");
            String region = matcher.group("region");
            Builder builder = new Builder(language);
            if (script != null && !script.equals("")) {
                builder = builder.withScript(script);
            }
            if (region != null && !region.equals("")) {
                builder = builder.withRegion(region);
            }
            return builder.build();
        } else {
            throw new IllegalArgumentException(String.format("Invalid Hreflang format: %s", string));
        }
    }

    @Override
    public String toString() {
        return String.format(
                "HreflangCode{language=%s,script=%s,region=%s}",
                this.language, this.script, this.region
        );
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Hreflang &&
                ((Hreflang) o).getValue().equals(this.getValue());
    }

    @Override
    public int hashCode() {
        return this.getValue().hashCode();
    }
}
