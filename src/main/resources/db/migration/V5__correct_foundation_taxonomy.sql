-- V4 seeded the foundations catalog with labels that under-described four
-- systems. V4 has already run against the deployed database, so the
-- corrections live here as UPDATEs instead of editing the V4 seed.

-- CommerceSystem is an ERP + POS + e-commerce suite, not just a POS.
UPDATE foundations SET
    build_types = '["POS","ERP","E-Commerce","Internal System","Customer Portal"]',
    features    = '["Authentication","Roles","Payments","Inventory","Reports","Dashboard","Invoices","API","Notifications"]',
    description = 'Integrates ERP, POS, and e-commerce storefront — sales, inventory, and procurement across multiple channels.',
    updated_at  = CURRENT_TIMESTAMP
WHERE slug = 'commerce-system';

-- RestoSystem is a full ERP with POS and public reservations.
UPDATE foundations SET
    build_types = '["POS","ERP","Booking","Internal System","Dashboard"]',
    features    = '["Authentication","Roles","Inventory","Dashboard","Reports","Notifications","AI","Scheduling","API"]',
    updated_at  = CURRENT_TIMESTAMP
WHERE slug = 'resto-system';

-- SpaSystem is ERP-grade: bookings + staff + inventory + payments.
UPDATE foundations SET
    build_types = '["Booking","ERP","Internal System","Customer Portal"]',
    features    = '["Authentication","Roles","Scheduling","Payments","Inventory","Notifications","Reports","Dashboard","Mobile"]',
    updated_at  = CURRENT_TIMESTAMP
WHERE slug = 'spa-system';

-- HumanDesign repositioned as recruitment/team assessment; the human design
-- chart is the candidate-facing hook.
UPDATE foundations SET
    industry           = 'Recruitment',
    related_industries = '["Recruitment","HR & Payroll","Professional Services","Consulting","Agencies","Education","AI"]',
    build_types        = '["AI Assistant","Customer Portal","Internal System","Dashboard"]',
    problems           = '["Hiring mismatches","No candidate assessment","Manual spreadsheets","No reporting","No dashboard","Duplicate work"]',
    description        = 'AI-powered human design assessment for recruitment and team composition — candidates get an engaging self-discovery experience, agencies get structured insight into fit and team dynamics.',
    updated_at         = CURRENT_TIMESTAMP
WHERE slug = 'human-design';
