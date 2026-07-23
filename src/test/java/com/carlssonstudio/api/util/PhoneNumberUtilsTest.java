package com.carlssonstudio.api.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PhoneNumberUtilsTest {

    @Test
    void normalizesIndonesianLocalNumbers() {
        assertEquals("6281234567890",
            PhoneNumberUtils.normalizeToWhatsAppNumber("081234567890"));
        assertEquals("6281234567890",
            PhoneNumberUtils.normalizeToWhatsAppNumber("0812-3456-7890"));
    }

    @Test
    void keepsInternationalNumbersAndStripsFormatting() {
        assertEquals("6281234567890",
            PhoneNumberUtils.normalizeToWhatsAppNumber("+62 812 3456 7890"));
        assertEquals("14155552671",
            PhoneNumberUtils.normalizeToWhatsAppNumber("+1 (415) 555-2671"));
    }

    @Test
    void rejectsUnusableNumbers() {
        assertNull(PhoneNumberUtils.normalizeToWhatsAppNumber(null));
        assertNull(PhoneNumberUtils.normalizeToWhatsAppNumber("   "));
        assertNull(PhoneNumberUtils.normalizeToWhatsAppNumber("abc"));
        assertNull(PhoneNumberUtils.normalizeToWhatsAppNumber("12345"));
    }
}
