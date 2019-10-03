package com.transferwise.urllocale;

import com.transferwise.urllocale.hreflang.Hreflang;
import com.transferwise.urllocale.hreflang.HreflangConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableConfigurationProperties(HreflangAutoConfiguration.HreflangProperties.class)
public class HreflangAutoConfiguration {
    @ConfigurationProperties(prefix = "hreflang")
    static class HreflangProperties {
        private Map<String, String> hreflangToUrlLocale = new HashMap<>();
        private String xDefault = "gb";

        public Map<String, String> getHreflangToUrlLocale() {
            return hreflangToUrlLocale;
        }

        public void setHreflangToUrlLocale(Map<String, String> hreflangToUrlLocale) {
            this.hreflangToUrlLocale = hreflangToUrlLocale;
        }

        public String getxDefault() {
            return xDefault;
        }

        public void setxDefault(String xDefault) {
            this.xDefault = xDefault;
        }

   }

    @Bean
    public HreflangConfig hreflangToUrlLocaleMapping(HreflangProperties properties) {
        Map<Hreflang, String> map = properties.getHreflangToUrlLocale().entrySet().stream()
                .collect(Collectors.toMap(e -> Hreflang.fromString(e.getKey()), Map.Entry::getValue));
        return new HreflangConfig(map, properties.getxDefault());
   }
}
