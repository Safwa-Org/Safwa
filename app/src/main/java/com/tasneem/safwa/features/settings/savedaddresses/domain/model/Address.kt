package com.tasneem.safwa.features.settings.savedaddresses.domain.model

data class Address(
    val id: String = "",           // Shopify GID (e.g. "gid://shopify/MailingAddress/123"). Blank = not yet created.
    val firstName: String = "",
    val lastName: String = "",
    val phone: String = "",
    val street: String = "",       // Shopify's address1
    val apartment: String = "",    // Shopify's address2 — optional
    val city: String = "",
    val province: String = "",     // Shopify's admin-area field — can stay blank where not applicable
    val zip: String = "",
    val countryName: String = "",  // full name — required as-is by Shopify's MailingAddressInput
    val countryCode: String = "",  // ISO alpha-2 — display/local logic only, not sent to Shopify
    val isDefault: Boolean = false
)