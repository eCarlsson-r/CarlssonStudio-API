-- Email is no longer the only way to reach a prospect now that WhatsApp
-- (V6) is available. The app layer enforces "at least one of email or
-- phone" via LeadRequest#isContactProvided(); the column itself must
-- allow NULL to store leads that supplied only a phone number.

ALTER TABLE leads
    MODIFY COLUMN email VARCHAR(150) NULL;
