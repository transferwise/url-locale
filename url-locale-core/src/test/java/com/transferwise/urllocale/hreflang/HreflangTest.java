package com.transferwise.urllocale.hreflang;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Value;

import static org.junit.jupiter.api.Assertions.*;

class HreflangTest {

    @Test
    void itShouldConstructTheXDefaultHreflang() {
        Hreflang hreflang = Hreflang.fromString("x-default");

        assertEquals(hreflang.getValue(), "x-default");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "en", "en-GB", "zh-Hant", "zh-Hant-HK"
    })
    void itShouldCreateFromValidHreflangString(String string) {
        Hreflang hreflang = Hreflang.fromString(string);

        assertEquals(hreflang.getValue(), string);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "gb", "Hant-HK", "US-en"
    })
    void itShouldNotCreateFromCommonHreflangErrors(String string) {
        assertThrows(IllegalArgumentException.class, () -> Hreflang.fromString(string));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "EN", "en-gb", "eN-Gb"
    })
    void itIgnoresCaseWhenCreatingFromHreflangString(String string) {
        Hreflang hreflang = Hreflang.fromString(string);

        assertEquals(hreflang.getValue().toLowerCase(), string.toLowerCase());
    }

    @Test
    void testEquals() {
        Hreflang hreflang1 = Hreflang.fromString("en-GB");
        Hreflang hreflang2 = Hreflang.fromString("en-gb");

        assertEquals(hreflang1, hreflang2);
    }

    @Test
    void testHashCode() {
        Hreflang hreflang = Hreflang.fromString("en-GB");

        assertEquals(hreflang.hashCode(), "en-GB".hashCode());
        assertEquals(hreflang.hashCode(), hreflang.getValue().hashCode());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "fr", "en", "de", "zh"
    })
    void itBuildsWithValidLanguage(String language) {
        Hreflang hreflang = new Hreflang.Builder(language).build();

        assertEquals(hreflang.getValue(), language);
    }

    @ParameterizedTest(name = "Language \"{0}\" should build with script \"{1}\"")
    @CsvSource({
            "zh, Hant",
            "zh, Hans",
    })
    void itBuildsWithValidLanguageAndScript(String language, String script) {
        Hreflang hreflang = new Hreflang.Builder(language).withScript(script).build();

        assertEquals(hreflang.getValue(), language + "-" + script);
    }

    @ParameterizedTest(name = "Language \"{0}\" should build with region \"{1}\"")
    @CsvSource({
            "en, GB",
            "zh, HK",
            "fr, FR"
    })
    void itBuildsWithValidLanguageAndRegion(String language, String region) {
        Hreflang hreflang = new Hreflang.Builder(language).withRegion(region).build();

        assertEquals(hreflang.getValue(), language + "-" + region);
    }

    @ParameterizedTest
    @CsvSource({
            "zh, Hant, HK",
            "zh, Hans, CN"
    })
    void itBuildsWithValidLanguageAndScriptAndRegion(String language, String script, String region) {
        Hreflang hreflang = new Hreflang.Builder(language).withScript(script).withRegion(region).build();

        assertEquals(hreflang.getValue(), language + "-" + script + "-" + region);
    }
}
