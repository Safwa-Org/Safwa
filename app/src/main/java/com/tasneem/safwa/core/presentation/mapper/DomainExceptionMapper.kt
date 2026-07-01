package com.tasneem.safwa.core.presentation.mapper

import com.tasneem.safwa.R
import com.tasneem.safwa.core.exception.DomainException
import com.tasneem.safwa.core.presentation.model.UiError

fun DomainException.toUiError(): UiError = when (this) {
    is DomainException.NoInternet -> UiError(
        titleRes = R.string.error_no_internet,
        descriptionRes = R.string.error_no_internet_desc,
    )
    is DomainException.NetworkError -> UiError(
        titleRes = R.string.error_network,
        descriptionRes = R.string.error_network_desc,
    )
    is DomainException.NotFound -> UiError(
        titleRes = R.string.error_not_found,
        descriptionRes = R.string.error_not_found_desc,
    )
    is DomainException.ServerError, is DomainException.Unknown -> UiError(
        titleRes = R.string.something_went_wrong,
        descriptionRes = R.string.error_generic_desc,
    )
}
