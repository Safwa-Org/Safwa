package com.tasneem.safwa.core.presentation.model

import androidx.annotation.StringRes

data class UiError(
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
)
