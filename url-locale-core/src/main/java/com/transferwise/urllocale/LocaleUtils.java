package com.transferwise.urllocale;

import java.util.Locale;

public class LocaleUtils {

    private LocaleUtils() {
    }

    /**
     * For compatibility reasons {@link Locale#getLanguage()} and {@link Locale#toString()} do not return the correct
     * iso-639-1 language code for some locales. {@link Locale#toLanguageTag()} can be used to get a valid IETF BCP 47
     * compliant language tag. However, it it often useful to get the language without the country (region).
     *
     * @see <a href="https://bugs.java.com/bugdatabase/view_bug.do?bug_id=6457127">JDK-6457127 : Indonesian Locale does not comply with ISO 639</a>
     * @see <a href="https://stackoverflow.com/q/55955641/1427295">Correct locale for Indonesia( “id_ID” Vs “in_ID” )?</a>
     *
     * @param locale
     * @return
     */
    public static String language(Locale locale) {
        return new Locale(locale.getLanguage(), "").toLanguageTag();
    }

    /**
     * @see <a href="https://developers.facebook.com/docs/sharing/webmasters#basic">Open Graph Markup</a>
     * @see <a href="https://developers.facebook.com/docs/internationalization#locales">Locales and Languages Supported by Facebook</a>
     * @param locale
     * @return
     */
    public static String openGraphLocale(Locale locale) {
        return locale.toLanguageTag().replace('-', '_');
    }
}
