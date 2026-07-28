-- Adds a lightweight cross-industry signal to each foundation, derived
-- from the real modules-export.json catalogs already extracted from each
-- project's own codebase. Unlike related_industries (curated by hand),
-- module_industries is the flattened, deduplicated union of every
-- module's reusable_for_industries tags for that foundation - used by
-- RecommendationEngine as a weaker fallback signal: "this foundation
-- wasn't built for your industry, but it has a proven, portable module
-- that already serves it."
--
-- Only 4 of 8 foundations have real module exports as of this migration
-- (commerce-system, insurance-portal, payroll-agent, spa-system); the
-- other 4 get an empty array until their own modules-export.json lands -
-- honest rather than fabricated.

ALTER TABLE foundations ADD COLUMN module_industries JSON NULL;

UPDATE foundations SET module_industries = JSON_ARRAY();

UPDATE foundations SET module_industries = '["apparel", "art-galleries", "automotive-parts", "boutiques", "coffee-shops", "digital-asset-management", "ecommerce", "electronics", "enterprise-ai", "fashion", "fintech", "fitness-clubs", "flooring-tile", "food-beverage", "franchises", "furniture-retail", "grocery", "hardware-stores", "healthcare", "healthcare-clinics", "home-decor", "hospitality", "interior-design", "local-delivery", "logistics", "manufacturing", "marketing-agencies", "multi-store-retail", "paint-coatings", "pharmaceuticals", "pharmacy", "publishing", "real-estate", "restaurant-chains", "restaurants", "retail", "saas", "salons-spas", "service-centers", "telecommunications", "warehousing", "wholesale"]'
WHERE slug = 'commerce-system';

UPDATE foundations SET module_industries = '["automotive", "corporate-finance", "corporate-sales", "e-commerce", "education", "entertainment", "finance", "fitness", "healthcare", "legal", "logistics", "marketing", "multi-level-marketing", "real-estate", "retail", "saas", "telecommunications", "travel", "utilities", "wealth-management"]'
WHERE slug = 'insurance-portal';

UPDATE foundations SET module_industries = '["CI/CD dashboards", "ETL/data-pipeline monitoring", "IoT ingestion", "accounting", "agent orchestration platforms", "any Indonesian payroll/fintech product", "any agentic system needing human sign-off", "any document-of-record generation", "any payroll/vendor-payment context", "any staged review workflow", "any system exposing an internal API to bots/agents/cron jobs", "any web app needing engagement/alerting", "any workforce SaaS", "banking statements", "booking reminders", "call centers", "claims processing", "construction (site logs)", "content moderation", "content moderation pipelines", "customer support automation oversight", "e-commerce order updates", "education", "education (student-teacher)", "education (transcripts/certificates)", "expense management", "field services", "finance ops automation", "franchise operations", "fraud review", "freelancer marketplaces", "gig/field work", "healthcare", "healthcare staffing", "helpdesk", "hospitality", "insurance certificates", "insurance claims", "internal HR tools", "invoicing", "loan underwriting", "logistics", "logistics (proof of delivery)", "manufacturing", "medical coding review", "order fulfillment", "partner integrations", "procurement approvals", "professional services", "property management (landlord disbursements)", "property management (tenant-landlord)", "retail", "royalty payouts", "sales organizations", "template for other jurisdictions'' bracket-based deduction engines"]'
WHERE slug = 'payroll-agent';

UPDATE foundations SET module_industries = '["accounting_firm", "amusement_parks", "app_review_analytics", "auto_service_history", "automotive_dealerships", "automotive_repair", "automotive_services", "banking", "beauty_salons", "brand_monitoring", "business_intelligence", "cafes_and_restaurants", "car_detailing", "car_maintenance", "class_passes", "cleaning_services", "co-working_spaces", "commission_sales", "conference_room_booking", "construction", "consulting_services", "content_streaming", "course_catalog", "customer_service", "customer_support", "dental_clinics", "e_commerce", "e_commerce_alerts", "e_commerce_checkout", "event_checkin", "event_ticketing", "event_updates", "event_venues", "events_ticketing", "fast_casual_dining", "field_service", "field_services", "field_surveys", "fintech_notifications", "fitness_coaching", "fitness_gyms", "fitness_studios", "fitness_tracking", "fleet_dispatch", "food_delivery", "franchise_management", "gift_card_issuance", "gift_cards", "healthcare", "healthcare_clinics", "healthcare_helpdesk", "healthcare_patient_feedback", "healthcare_patient_portal", "healthcare_visits", "healthcare_wellness", "home_services", "hospitality", "hospitality_concierge", "hospitality_resorts", "hospitals_and_clinics", "hotels_and_hostels", "logistics", "logistics_delivery", "loyalty_programs", "manufacturing", "medical_appointments", "medical_scheduling", "medical_spa", "membership_clubs", "mobile_forms", "mobile_retail", "news_and_media", "personal_training", "pet_boarding", "pet_care", "pet_grooming", "property_management", "real_estate", "real_estate_brokerages", "remote_health", "restaurant_dining", "restaurant_menus", "restaurants", "retail", "retail_management", "retail_pos", "retail_shopping_assistant", "retail_stores", "ride_hailing", "salon_and_beauty", "salon_chains", "salon_up-selling", "salons", "service_centers", "small_business_erp", "spa_wellness", "tattoo_studios", "theme_parks", "travel_booking", "warehouse_management", "warehousing", "wellness_and_fitness"]'
WHERE slug = 'spa-system';

ALTER TABLE foundations MODIFY module_industries JSON NOT NULL;
