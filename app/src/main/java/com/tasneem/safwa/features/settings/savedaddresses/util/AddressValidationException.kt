package com.tasneem.safwa.features.settings.savedaddresses.util

enum class AddressFieldError {
    RECIPIENT_NAME_EMPTY,
    MOBILE_NUMBER_INVALID,
    ADDRESS_NOT_SELECTED
}

class AddressValidationException(
    val errors: List<AddressFieldError>
) : Exception("Address validation failed")