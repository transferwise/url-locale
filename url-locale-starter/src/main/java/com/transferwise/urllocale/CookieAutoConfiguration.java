package com.transferwise.urllocale;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(CookieAutoConfiguration.CookieProperties.class)
public class CookieAutoConfiguration {
    @ConfigurationProperties(prefix = "locale-cookie")
    static class CookieProperties {
        private String cookieName = "localeData";
        private int maxAge = 31557600;

        public String getCookieName() {
            return cookieName;
        }

        public void setCookieName(String cookieName) {
            this.cookieName = cookieName;
        }

        public int getMaxAge() {
            return maxAge;
        }
        public void setMaxAge(int maxAge) {
            this.maxAge = maxAge;
        }
    }

    @Bean
    public LocaleDataCookieAddingInterceptor localeDataCookieAddingInterceptor(CookieProperties cookieProperties) {
        return new LocaleDataCookieAddingInterceptor(cookieProperties.getCookieName(), cookieProperties.getMaxAge());
    }

}
