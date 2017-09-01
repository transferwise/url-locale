package com.transferwise.urllocale;

import com.transferwise.cable.UrlRewriteFilter;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableConfigurationProperties(UrlLocaleAutoConfiguration.UrlLocaleProperties.class)
public class UrlLocaleAutoConfiguration {
    @Data
    @ConfigurationProperties(prefix = "url-locale")
    static class UrlLocaleProperties {
        private Map<String, String> mapping = new HashMap<String, String>() {{
            put("gb", "en-GB");
        }};
        private String fallback = "en-gb";
        private String rewrite;
    }

    @Bean
    public Map<String, Locale> localeMapping(UrlLocaleProperties config) {
        return config.getMapping().entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> Locale.forLanguageTag(e.getValue())));
    }

    @Bean
    public Map<Locale, String> reverseLocaleMapping(Map<String, Locale> localeMapping) {
        return localeMapping.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }

    @Bean
    public LocaleResolver localeResolver(UrlLocaleProperties config, Map<Locale, String> reverseLocaleMapping) {
        Locale fallback = Locale.forLanguageTag(config.getFallback());
        if (!reverseLocaleMapping.containsKey(fallback)) {
            throw new RuntimeException("No mapping defined for fallback \"" + config.getFallback() + "\"");
        }
        return new UrlLocaleResolver(fallback);
    }

    @Bean
    public FilterRegistrationBean urlLocaleExtractorFilter(Map<String, Locale> localeMapping) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new UrlLocaleExtractorFilter(localeMapping));
        registration.setOrder(10);
        return registration;
    }

    @Bean
    public FilterRegistrationBean urlLocaleEncodingFilter(Map<Locale, String> reverseLocaleMapping) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new UrlLocaleEncodingFilter(reverseLocaleMapping));
        registration.setOrder(20);
        return registration;
    }

    @Bean
    @ConditionalOnProperty("url-locale.rewrite")
    public UrlRewriteFilter urlRewriteFilter(UrlLocaleProperties config) {
        return new UrlRewriteFilter()
            .rewrite("^/([a-z]{2})/(.*)$", config.getRewrite());
    }
}
