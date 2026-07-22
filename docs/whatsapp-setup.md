# WhatsApp Business Cloud API setup

The API sends an automated WhatsApp follow-up to prospects who supply a
number **and tick the opt-in checkbox** in the Start a Project flow.
Sending is disabled by default and controlled entirely by environment
variables — with `WHATSAPP_ENABLED=false` (the default) nothing changes.

## One-time Meta setup

1. **Meta Business verification** — verify the Carlsson Studio business
   at business.facebook.com (Settings → Business info). Required before
   messaging real customers at volume.
2. **Create the WhatsApp Business app** — developers.facebook.com →
   Create App → Business → add the *WhatsApp* product. This creates a
   WhatsApp Business Account (WABA) with a test number.
3. **Add a real phone number** — WhatsApp → API Setup → add the studio's
   dedicated number (it must NOT be registered on the regular WhatsApp /
   WA Business app; the Cloud API takes exclusive control of it). Note
   the **Phone number ID** (not the phone number itself).
4. **Permanent token** — Business Settings → Users → System users →
   create a system user (admin), assign the app + WABA with
   *whatsapp_business_messaging* permission, generate a token that never
   expires.
5. **Register the template** — WhatsApp Manager → Message templates →
   create:

   - Name: `lead_followup`
   - Category: **Utility**
   - Language: English (`en`) — add Indonesian (`id`) later if wanted
   - Body:

     ```
     Hi {{1}}, thanks for reaching out to Carlsson Studio. Based on your
     answers, {{2}} is the closest foundation to your needs ({{3}}% match).
     I will follow up within 24 hours — feel free to reply here with any
     questions.
     ```

   Wait for approval (usually minutes to a few hours for Utility).

## Environment variables

| Variable | Example | Notes |
| --- | --- | --- |
| `WHATSAPP_ENABLED` | `true` | Master switch; default `false` |
| `WHATSAPP_TOKEN` | `EAAG…` | Permanent system-user token |
| `WHATSAPP_PHONE_NUMBER_ID` | `123456789012345` | From API Setup, not the phone number |
| `WHATSAPP_TEMPLATE_NAME` | `lead_followup` | Default already `lead_followup` |
| `WHATSAPP_TEMPLATE_LANG` | `en` | Must match an approved language |
| `WHATSAPP_API_VERSION` | `v20.0` | Graph API version |

## Behavior

- Message is sent asynchronously after lead submission; failures are
  logged and never affect the lead or the email notification.
- Sent **only** when: feature enabled, token + phone number ID present,
  prospect provided a phone number **and** opted in, and at least one
  recommendation exists.
- Phone normalization: digits only; a leading `0` is rewritten to `62`
  (Indonesia); numbers under 8 digits are discarded.
- The lead notification email now includes a tap-to-chat `wa.me` link
  with the prospect's opt-in status, so manual follow-up works even
  while the Cloud API is disabled.

## Costs & policy notes

- Business-initiated conversations are billed per 24-hour conversation
  window (Utility category; rates vary by country — Indonesia is among
  the cheaper tiers).
- Business-initiated messages **must** use an approved template. Once
  the prospect replies, a 24-hour service window opens where free-form
  messages are allowed.
- The opt-in checkbox in the questionnaire is what satisfies Meta's
  opt-in requirement — do not send to leads with `whatsapp_opt_in = 0`.
