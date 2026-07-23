package com.carlssonstudio.api.recommendation;

import java.util.Locale;
import java.util.Map;

/**
 * Bahasa Indonesia display labels for foundation option values (industry,
 * build type, problem, feature), used only when composing a localized
 * {@link RecommendationEngine} match reason.
 *
 * This mirrors the frontend's lib/id/questionnaireOptions.ts — same
 * canonical values, same Indonesian labels. The two are intentionally
 * separate catalogs (this one authors full sentences server-side; the
 * frontend one labels standalone buttons), so keep both in sync by hand
 * when a foundation gains a new industry/problem/feature value.
 *
 * Any locale other than "id" — and any "id" request for a value with no
 * entry yet — passes the raw English value through unchanged, so an
 * untranslated new option degrades to English instead of breaking.
 */
final class OptionLabels {

    private OptionLabels() {}

    static String industry(String value, Locale locale) {
        return translate(INDUSTRY, value, locale);
    }

    static String buildType(String value, Locale locale) {
        return translate(BUILD_TYPE, value, locale);
    }

    static String problem(String value, Locale locale) {
        return translate(PROBLEM, value, locale);
    }

    static String feature(String value, Locale locale) {
        return translate(FEATURE, value, locale);
    }

    private static String translate(Map<String, String> dictionary, String value, Locale locale) {
        if (value == null) return null;
        if (!"id".equalsIgnoreCase(locale.getLanguage())) return value;
        return dictionary.getOrDefault(value, value);
    }

    private static final Map<String, String> INDUSTRY = Map.ofEntries(
        Map.entry("AI", "Kecerdasan Buatan (AI)"),
        Map.entry("Agencies", "Agensi"),
        Map.entry("Banking", "Perbankan"),
        Map.entry("Beauty", "Kecantikan"),
        Map.entry("Cafe", "Kafe"),
        Map.entry("Catering", "Katering"),
        Map.entry("Construction", "Konstruksi"),
        Map.entry("Consulting", "Konsultan"),
        Map.entry("E-Commerce", "Toko Online"),
        Map.entry("Education", "Pendidikan"),
        Map.entry("Fashion", "Fashion"),
        Map.entry("Finance", "Keuangan"),
        Map.entry("Financial Services", "Jasa Keuangan"),
        Map.entry("Fitness", "Kebugaran"),
        Map.entry("Food & Beverage", "Makanan & Minuman"),
        Map.entry("HR & Payroll", "SDM & Penggajian"),
        Map.entry("Healthcare", "Kesehatan"),
        Map.entry("Insurance", "Asuransi"),
        Map.entry("Investment", "Investasi"),
        Map.entry("Leasing", "Leasing"),
        Map.entry("Manufacturing", "Manufaktur"),
        Map.entry("Professional Services", "Jasa Profesional"),
        Map.entry("Property", "Properti"),
        Map.entry("Real Estate", "Real Estat"),
        Map.entry("Recruitment", "Rekrutmen"),
        Map.entry("Restaurant", "Restoran"),
        Map.entry("Retail", "Retail"),
        Map.entry("Spa", "Spa"),
        Map.entry("Wellness", "Wellness"),
        Map.entry("Wholesale", "Grosir")
    );

    private static final Map<String, String> BUILD_TYPE = Map.ofEntries(
        Map.entry("AI Assistant", "Asisten AI"),
        Map.entry("Booking", "Sistem Booking / Reservasi"),
        Map.entry("Customer Portal", "Portal Pelanggan"),
        Map.entry("Dashboard", "Dashboard Ringkasan Data"),
        Map.entry("E-Commerce", "Toko Online"),
        Map.entry("ERP", "Sistem Manajemen Bisnis (ERP)"),
        Map.entry("Internal System", "Sistem Internal Perusahaan"),
        Map.entry("POS", "Sistem Kasir (POS)")
    );

    private static final Map<String, String> PROBLEM = Map.ofEntries(
        Map.entry("Duplicate work", "Input data berulang-ulang"),
        Map.entry("Hiring mismatches", "Salah merekrut karyawan"),
        Map.entry("Manual spreadsheets", "Masih pakai Excel / catatan manual"),
        Map.entry("No HR", "Belum punya sistem SDM"),
        Map.entry("No booking", "Belum ada sistem booking / reservasi"),
        Map.entry("No candidate assessment", "Belum ada penilaian kandidat"),
        Map.entry("No dashboard", "Belum ada ringkasan data bisnis"),
        Map.entry("No inventory", "Belum ada pencatatan stok"),
        Map.entry("No reporting", "Belum ada laporan otomatis"),
        Map.entry("WhatsApp chaos", "Pesanan/chat berantakan di WhatsApp")
    );

    private static final Map<String, String> FEATURE = Map.ofEntries(
        Map.entry("AI", "Kecerdasan Buatan (AI)"),
        Map.entry("API", "Integrasi dengan sistem lain (API)"),
        Map.entry("Authentication", "Login & Keamanan Akun"),
        Map.entry("Dashboard", "Dashboard Ringkasan Data"),
        Map.entry("Inventory", "Manajemen Stok"),
        Map.entry("Invoices", "Invoice / Faktur"),
        Map.entry("Mobile", "Aplikasi Mobile"),
        Map.entry("Notifications", "Notifikasi"),
        Map.entry("Payments", "Pembayaran Online"),
        Map.entry("Reports", "Laporan"),
        Map.entry("Roles", "Hak Akses Pengguna"),
        Map.entry("Scheduling", "Penjadwalan")
    );
}
