CREATE TABLE foundations (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    slug               VARCHAR(50) NOT NULL UNIQUE,
    name               VARCHAR(100) NOT NULL,
    industry           VARCHAR(50) NOT NULL,
    related_industries JSON NOT NULL,
    build_types        JSON NOT NULL,
    problems           JSON NOT NULL,
    features           JSON NOT NULL,
    description        TEXT,
    is_active          TINYINT(1) NOT NULL DEFAULT 1,
    created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                       ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO foundations
(slug, name, industry, related_industries, build_types, problems, features, description, is_active)
VALUES
('commerce-system', 'CommerceSystem', 'Retail',
 '["Retail","E-Commerce","Wholesale","Fashion"]',
 '["POS","Internal System","Customer Portal"]',
 '["Manual spreadsheets","No inventory","No reporting","Duplicate work","No dashboard"]',
 '["Authentication","Roles","Payments","Reports","Dashboard","Invoices","API","Notifications"]',
 'Automates retail sales, inventory, and storefront across multiple channels.', 1),

('resto-system', 'RestoSystem', 'Restaurant',
 '["Restaurant","Cafe","Food & Beverage","Catering"]',
 '["POS","Internal System","Dashboard"]',
 '["Manual spreadsheets","No reporting","No inventory","No booking","Duplicate work","No dashboard"]',
 '["Authentication","Roles","Dashboard","Reports","Notifications","AI","Scheduling","API"]',
 'Combines POS, reservations, kitchen workflow, and analytics into one platform.', 1),

('urus-properti', 'UrusProperti', 'Property',
 '["Property","Real Estate","Construction","Leasing"]',
 '["Internal System","Customer Portal","Dashboard"]',
 '["Manual spreadsheets","No reporting","Duplicate work","No dashboard","WhatsApp chaos"]',
 '["Authentication","Roles","Dashboard","Reports","Notifications","AI","API","Invoices"]',
 'Manages property listings, tenant records, and lease tracking end-to-end.', 1),

('insurance-portal', 'InsurancePortal', 'Insurance',
 '["Insurance","Finance","Banking","Financial Services"]',
 '["Internal System","Customer Portal","Dashboard"]',
 '["Manual spreadsheets","No reporting","Duplicate work","No dashboard","WhatsApp chaos"]',
 '["Authentication","Roles","Dashboard","Reports","AI","API","Notifications","Mobile"]',
 'Centralizes policy, claim, and customer management for insurance agencies.', 1),

('spa-system', 'SpaSystem', 'Wellness',
 '["Wellness","Spa","Beauty","Healthcare","Fitness"]',
 '["Booking","Internal System","Customer Portal"]',
 '["No booking","Manual spreadsheets","No inventory","WhatsApp chaos","No reporting"]',
 '["Authentication","Roles","Scheduling","Payments","Notifications","Reports","Dashboard","Mobile"]',
 'Covers bookings, staff scheduling, inventory, and payments for wellness businesses.', 1),

('payroll-agent', 'Payroll Agent', 'HR & Payroll',
 '["HR & Payroll","Manufacturing","Professional Services","Education","Healthcare"]',
 '["Internal System","ERP","Dashboard"]',
 '["Manual spreadsheets","No HR","Duplicate work","No reporting","No dashboard"]',
 '["Authentication","Roles","Reports","Dashboard","API","Notifications","AI","Invoices"]',
 'Automates payroll calculation, tax compliance, and salary disbursement.', 1),

('human-design', 'HumanDesign', 'AI',
 '["AI","Professional Services","Consulting","Education","HR & Payroll"]',
 '["AI Assistant","Customer Portal","Dashboard"]',
 '["No reporting","No dashboard","Duplicate work","Manual spreadsheets"]',
 '["Authentication","AI","API","Reports","Dashboard","Mobile"]',
 'Generates personality analysis and business compatibility reports using AI.', 1),

('quoteplot-agent', 'QuotePlot Agent', 'AI',
 '["AI","Finance","Investment","Banking","Professional Services"]',
 '["AI Assistant","Dashboard","Internal System"]',
 '["No reporting","No dashboard","Manual spreadsheets","Duplicate work"]',
 '["Authentication","AI","API","Dashboard","Reports","Notifications"]',
 'Delivers real-time stock intelligence through natural language queries.', 1);