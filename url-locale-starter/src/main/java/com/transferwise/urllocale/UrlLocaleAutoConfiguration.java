package com.transferwise.urllocale;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@AutoConfigureBefore(WebMvcAutoConfiguration.class)
@Configuration
@EnableConfigurationProperties(UrlLocaleAutoConfiguration.UrlLocaleProperties.class)
public class UrlLocaleAutoConfiguration {
    @ConfigurationProperties(prefix = "url-locale")
    static class UrlLocaleProperties {
        private Map<String, String> mapping = new HashMap<String, String>() {{
            put("gb", "en-GB");
        }};
        private String fallback = "en-gb";

        public Map<String, String> getMapping() {
            return mapping;
        }

        public void setMapping(Map<String, String> mapping) {
            this.mapping = mapping;
        }

        public String getFallback() {
            return fallback;
        }

        public void setFallback(String fallback) {
            this.fallback = fallback;
        }
    }

    @Bean
    public Map<String, Locale> urlLocaleToLocaleMapping(UrlLocaleProperties config) {
        return config.getMapping().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> Locale.forLanguageTag(e.getValue())));
    }

    @Bean(name = DispatcherServlet.LOCALE_RESOLVER_BEAN_NAME)
    public LocaleResolver localeResolver(UrlLocaleProperties config, Map<String, Locale> urlLocaleToLocaleMapping) {
        Locale fallback = Locale.forLanguageTag(config.getFallback());
        if (!urlLocaleToLocaleMapping.containsValue(fallback)) {
            throw new RuntimeException("No mapping defined for fallback \"" + config.getFallback() + "\"");
        }
        return new UrlLocaleResolver(urlLocaleToLocaleMapping, fallback);
    }

    @Bean
    public Filter urlLocaleExtractorFilter(Map<String, Locale> localeMapping) {
        return new UrlLocaleExtractorFilter(localeMapping.keySet());
    }
}
