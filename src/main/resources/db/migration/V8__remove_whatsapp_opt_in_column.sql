-- Automated WhatsApp outreach (Tier 2 / Cloud API) has been dropped in
-- favor of tap-to-chat wa.me links (Tier 1), which need no consent flag
-- since a human sends every message. V6 already ran, so this drops the
-- column via a new migration rather than editing it.

ALTER TABLE leads
    DROP COLUMN whatsapp_opt_in;
