package com.transferwise.urllocale;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.DispatcherType;
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
    }

    @Bean
    public Map<String, Locale> localeMapping(UrlLocaleProperties config) {
        return config.getMapping().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> Locale.forLanguageTag(e.getValue())));
    }

    @Bean
    public LocaleResolver localeResolver(UrlLocaleProperties config, Map<String, Locale> localeMapping) {
        Locale fallback = Locale.forLanguageTag(config.getFallback());
        if (!localeMapping.values().contains(fallback)) {
            throw new RuntimeException("No mapping defined for fallback \"" + config.getFallback() + "\"");
        }
        return new UrlLocaleResolver(fallback);
    }

    @Bean
    public FilterRegistrationBean urlLocaleExtractorFilter(Map<String, Locale> localeMapping) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new UrlLocaleExtractorFilter(localeMapping));
        registration.setOrder(10);
        registration.setDispatcherTypes(
                DispatcherType.FORWARD,
                DispatcherType.INCLUDE,
                DispatcherType.REQUEST,
                DispatcherType.ASYNC,
                DispatcherType.ERROR
        );
        return registration;
    }

    @Bean
    public FilterRegistrationBean urlLocaleEncodingFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new UrlLocaleEncodingFilter());
        registration.setOrder(20);
        registration.setDispatcherTypes(
                DispatcherType.FORWARD,
                DispatcherType.INCLUDE,
                DispatcherType.REQUEST,
                DispatcherType.ASYNC,
                DispatcherType.ERROR
        );
        return registration;
    }

}
