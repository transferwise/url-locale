package com.transferwise.urllocale;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static com.transferwise.urllocale.LocaleUtils.language;
import static com.transferwise.urllocale.LocaleUtils.openGraphLocale;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LocaleUtilsTest {
	private static final Locale locale = new Locale("id", "ID");

	@Test
	void itShouldReturnISO639ValueForIndonesian() {
		String language = language(locale);
		assertEquals("id", language);
	}

	@Test
	void itShouldReturnLanguageWithUnderscoreDelimiter() {
		String openGraphLocale = openGraphLocale(locale);
		assertEquals("id_ID", openGraphLocale);
	}
}
