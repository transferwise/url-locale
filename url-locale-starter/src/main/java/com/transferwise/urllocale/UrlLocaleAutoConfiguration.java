package com.transferwise.urllocale;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableConfigurationProperties(UrlLocaleAutoConfiguration.UrlLocaleProperties.class)
public class UrlLocaleAutoConfiguration {
    @ConfigurationProperties(prefix = "url-locale")
    static class UrlLocaleProperties {
        private Map<String, String> mapping = new HashMap<String, String>() {{
            put("gb", "en-GB");
        }};
        private FallbackConfig fallback;

        public Map<String, String> getMapping() {
            return mapping;
        }

        public void setMapping(Map<String, String> mapping) {
            this.mapping = mapping;
        }

        public FallbackConfig getFallback() {
            return fallback;
        }

        public void setFallback(FallbackConfig fallback) {
            this.fallback = fallback;
        }

        private class FallbackConfig {
            private String value;
            private int redirectStatusCode = UrlLocaleExtractorFilter.DEFAULT_FALLBACK_STATUS_CODE;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }

            public int getRedirectStatusCode() {
                return redirectStatusCode;
            }

            public void setRedirectStatusCode(int redirectStatusCode) {
                this.redirectStatusCode = redirectStatusCode;
            }
        }

    }

    @Bean
    public Map<String, Locale> urlLocaleToLocaleMapping(UrlLocaleProperties config) {
        return config.getMapping().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> Locale.forLanguageTag(e.getValue())));
    }

    @Bean
    public LocaleResolver localeResolver(UrlLocaleProperties config, Map<String, Locale> urlLocaleToLocaleMapping) {
        Locale fallbackLocale = null;
        if (config.getFallback() != null) {
            if (!urlLocaleToLocaleMapping.containsKey(config.getFallback().getValue())) {
                throw new RuntimeException("No mapping defined for fallback url locale \"" + config.getFallback() + "\"");
            }
                fallbackLocale = urlLocaleToLocaleMapping.get(config.getFallback().getValue());
            }
        return new UrlLocaleResolver(urlLocaleToLocaleMapping, fallbackLocale);
    }

    @Bean
    public Filter urlLocaleExtractorFilter(UrlLocaleProperties config, Map<String, Locale> localeMapping) {
        if (config.getFallback() != null) {
            return new UrlLocaleExtractorFilter(localeMapping.keySet(), config.getFallback().getValue(), config.getFallback().getRedirectStatusCode());
        } else {
            return new UrlLocaleExtractorFilter(localeMapping.keySet());
        }
    }
}
