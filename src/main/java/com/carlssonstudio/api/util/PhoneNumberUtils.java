package com.carlssonstudio.api.util;

/**
 * Shared phone normalization for anything that talks to WhatsApp or
 * Meta by number: the tap-to-chat wa.me links in the notification email
 * and the Conversions API's hashed phone match parameter.
 */
public final class PhoneNumberUtils {

    private PhoneNumberUtils() {
    }

    /**
     * wa.me and Meta's Graph API both expect digits only, in
     * international format without a leading '+'. A leading '0' is
     * treated as an Indonesian local number and rewritten to country
     * code 62.
     */
    public static String normalizeToWhatsAppNumber(String raw) {
        if (raw == null) return null;
        String digits = raw.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) return null;
        if (digits.startsWith("0")) {
            digits = "62" + digits.substring(1);
        }
        // Anything shorter than 8 digits cannot be a routable number
        return digits.length() < 8 ? null : digits;
    }
}
