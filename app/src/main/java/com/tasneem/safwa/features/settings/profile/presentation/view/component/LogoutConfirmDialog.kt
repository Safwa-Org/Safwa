package com.tasneem.safwa.features.settings.profile.presentation.view.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.tasneem.safwa.R
import com.tasneem.safwa.core.shared_component.SafwaConfirmDialog

@Composable
fun LogoutConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    SafwaConfirmDialog(
        title = stringResource(R.string.logout_confirm_title),
        message = stringResource(R.string.logout_confirm_message),
        confirmText = stringResource(R.string.logout),
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}