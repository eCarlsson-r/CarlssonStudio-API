-- WhatsApp outreach: optional prospect phone number plus an explicit
-- opt-in flag. Meta policy requires opt-in before any business-initiated
-- template message is sent.

ALTER TABLE leads
    ADD COLUMN phone VARCHAR(25) NULL AFTER email,
    ADD COLUMN whatsapp_opt_in TINYINT(1) NOT NULL DEFAULT 0 AFTER phone;
