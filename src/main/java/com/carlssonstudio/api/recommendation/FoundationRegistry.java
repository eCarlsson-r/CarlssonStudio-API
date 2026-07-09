package com.carlssonstudio.api.recommendation;

import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class FoundationRegistry {

    public List<Foundation> getAll() {
        return List.of(

            Foundation.builder()
                .slug("commerce-system")
                .name("CommerceSystem")
                .industry("Retail")
                .relatedIndustries(List.of(
                    "Retail", "E-Commerce", "Wholesale", "Fashion"))
                .buildTypes(List.of(
                    "POS", "Internal System", "Customer Portal"))
                .problems(List.of(
                    "Manual spreadsheets",
                    "No inventory",
                    "No reporting",
                    "Duplicate work",
                    "No dashboard"))
                .features(List.of(
                    "Authentication", "Roles", "Payments",
                    "Reports", "Dashboard", "Invoices",
                    "API", "Notifications"))
                .description(
                    "Automates retail sales, inventory, and " +
                    "storefront across multiple channels.")
                .build(),

            Foundation.builder()
                .slug("resto-system")
                .name("RestoSystem")
                .industry("Restaurant")
                .relatedIndustries(List.of(
                    "Restaurant", "Cafe", "Food & Beverage",
                    "Catering"))
                .buildTypes(List.of(
                    "POS", "Internal System", "Dashboard"))
                .problems(List.of(
                    "Manual spreadsheets",
                    "No reporting",
                    "No inventory",
                    "No booking",
                    "Duplicate work",
                    "No dashboard"))
                .features(List.of(
                    "Authentication", "Roles", "Dashboard",
                    "Reports", "Notifications", "AI",
                    "Scheduling", "API"))
                .description(
                    "Combines POS, reservations, kitchen " +
                    "workflow, and analytics into one platform.")
                .build(),

            Foundation.builder()
                .slug("urus-properti")
                .name("UrusProperti")
                .industry("Property")
                .relatedIndustries(List.of(
                    "Property", "Real Estate", "Construction",
                    "Leasing"))
                .buildTypes(List.of(
                    "Internal System", "Customer Portal",
                    "Dashboard"))
                .problems(List.of(
                    "Manual spreadsheets",
                    "No reporting",
                    "Duplicate work",
                    "No dashboard",
                    "WhatsApp chaos"))
                .features(List.of(
                    "Authentication", "Roles", "Dashboard",
                    "Reports", "Notifications", "AI",
                    "API", "Invoices"))
                .description(
                    "Manages property listings, tenant records, " +
                    "and lease tracking end-to-end.")
                .build(),

            Foundation.builder()
                .slug("insurance-portal")
                .name("InsurancePortal")
                .industry("Insurance")
                .relatedIndustries(List.of(
                    "Insurance", "Finance", "Banking",
                    "Financial Services"))
                .buildTypes(List.of(
                    "Internal System", "Customer Portal",
                    "Dashboard"))
                .problems(List.of(
                    "Manual spreadsheets",
                    "No reporting",
                    "Duplicate work",
                    "No dashboard",
                    "WhatsApp chaos"))
                .features(List.of(
                    "Authentication", "Roles", "Dashboard",
                    "Reports", "AI", "API",
                    "Notifications", "Mobile"))
                .description(
                    "Centralizes policy, claim, and customer " +
                    "management for insurance agencies.")
                .build(),

            Foundation.builder()
                .slug("spa-system")
                .name("SpaSystem")
                .industry("Wellness")
                .relatedIndustries(List.of(
                    "Wellness", "Spa", "Beauty", "Healthcare",
                    "Fitness"))
                .buildTypes(List.of(
                    "Booking", "Internal System",
                    "Customer Portal"))
                .problems(List.of(
                    "No booking",
                    "Manual spreadsheets",
                    "No inventory",
                    "WhatsApp chaos",
                    "No reporting"))
                .features(List.of(
                    "Authentication", "Roles", "Scheduling",
                    "Payments", "Notifications", "Reports",
                    "Dashboard", "Mobile"))
                .description(
                    "Covers bookings, staff scheduling, " +
                    "inventory, and payments for wellness " +
                    "businesses.")
                .build(),

            Foundation.builder()
                .slug("payroll-agent")
                .name("Payroll Agent")
                .industry("HR & Payroll")
                .relatedIndustries(List.of(
                    "HR & Payroll", "Manufacturing",
                    "Professional Services", "Education",
                    "Healthcare"))
                .buildTypes(List.of(
                    "Internal System", "ERP", "Dashboard"))
                .problems(List.of(
                    "Manual spreadsheets",
                    "No HR",
                    "Duplicate work",
                    "No reporting",
                    "No dashboard"))
                .features(List.of(
                    "Authentication", "Roles", "Reports",
                    "Dashboard", "API", "Notifications",
                    "AI", "Invoices"))
                .description(
                    "Automates payroll calculation, tax " +
                    "compliance, and salary disbursement.")
                .build(),

            Foundation.builder()
                .slug("human-design")
                .name("HumanDesign")
                .industry("AI")
                .relatedIndustries(List.of(
                    "AI", "Professional Services",
                    "Consulting", "Education", "HR & Payroll"))
                .buildTypes(List.of(
                    "AI Assistant", "Customer Portal",
                    "Dashboard"))
                .problems(List.of(
                    "No reporting",
                    "No dashboard",
                    "Duplicate work",
                    "Manual spreadsheets"))
                .features(List.of(
                    "Authentication", "AI", "API",
                    "Reports", "Dashboard", "Mobile"))
                .description(
                    "Generates personality analysis and " +
                    "business compatibility reports using AI.")
                .build(),

            Foundation.builder()
                .slug("quoteplot-agent")
                .name("QuotePlot Agent")
                .industry("AI")
                .relatedIndustries(List.of(
                    "AI", "Finance", "Investment",
                    "Banking", "Professional Services"))
                .buildTypes(List.of(
                    "AI Assistant", "Dashboard",
                    "Internal System"))
                .problems(List.of(
                    "No reporting",
                    "No dashboard",
                    "Manual spreadsheets",
                    "Duplicate work"))
                .features(List.of(
                    "Authentication", "AI", "API",
                    "Dashboard", "Reports", "Notifications"))
                .description(
                    "Delivers real-time stock intelligence " +
                    "through natural language queries.")
                .build()
        );
    }
}