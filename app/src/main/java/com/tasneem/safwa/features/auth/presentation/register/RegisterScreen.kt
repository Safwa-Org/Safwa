package com.tasneem.safwa.features.auth.presentation.register

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tasneem.safwa.R
import com.tasneem.safwa.core.shared_component.CustomButon
import com.tasneem.safwa.core.shared_component.SafwaLogo
import com.tasneem.safwa.core.theme.SafwaTheme
import com.tasneem.safwa.features.auth.presentation.components.CustomTextField
import com.tasneem.safwa.features.auth.presentation.components.ErrorText

@Composable
fun RegisterScreen(
    state: RegisterState,
    onEvent: (RegisterEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .imePadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            SafwaLogo(
                size = 28.dp,
                containerColor = Color.Transparent,
                iconColor = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            )
        }

        Spacer(modifier = Modifier.height(64.dp))

        Text(
            text = stringResource(id = R.string.create_account_title),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            fontFamily = FontFamily.Serif
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.sign_up_desc),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(48.dp))

        CustomTextField(
            label = stringResource(id = R.string.first_name),
            value = state.firstName,
            onValueChange = { onEvent(RegisterEvent.FirstNameChanged(it)) }
        )
        if (state.firstNameErrorResId != null) {
            ErrorText(stringResource(state.firstNameErrorResId))
        }
        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            label = stringResource(id = R.string.last_name),
            value = state.lastName,
            onValueChange = { onEvent(RegisterEvent.LastNameChanged(it)) }
        )
        if (state.lastNameErrorResId != null) {
            ErrorText(stringResource(state.lastNameErrorResId))
        }
        Spacer(modifier = Modifier.height(24.dp))

        CustomTextField(
            label = stringResource(id = R.string.email),
            value = state.email,
            onValueChange = { onEvent(RegisterEvent.EmailChanged(it)) },
            keyboardType = KeyboardType.Email
        )
        if (state.emailErrorResId != null) {
            ErrorText(stringResource(state.emailErrorResId))
        }
        Spacer(modifier = Modifier.height(24.dp))

        CustomTextField(
            label = stringResource(id = R.string.phone),
            value = state.phone,
            onValueChange = { onEvent(RegisterEvent.PhoneChanged(it)) },
            keyboardType = KeyboardType.Phone
        )
        if (state.phoneErrorResId != null) {
            ErrorText(stringResource(state.phoneErrorResId))
        }
        Spacer(modifier = Modifier.height(24.dp))

        CustomTextField(
            label = stringResource(id = R.string.password),
            value = state.password,
            onValueChange = { onEvent(RegisterEvent.PasswordChanged(it)) },
            isPassword = true
        )
        if (state.passwordErrorResId != null) {
            ErrorText(stringResource(state.passwordErrorResId))
        }
        Spacer(modifier = Modifier.height(24.dp))

        CustomTextField(
            label = stringResource(id = R.string.confirm_password),
            value = state.confirmPassword,
            onValueChange = { onEvent(RegisterEvent.ConfirmPasswordChanged(it)) },
            isPassword = true
        )
        if (state.confirmPasswordErrorResId != null) {
            ErrorText(stringResource(state.confirmPasswordErrorResId))
        }
        Spacer(modifier = Modifier.height(32.dp))

        CustomButon(
            title = stringResource(id = R.string.sign_up),
            onContinue = { onEvent(RegisterEvent.RegisterClicked) },
            modifier = Modifier.fillMaxWidth(),
        )

        if (state.generalErrorResId != null) {
            ErrorText(stringResource(state.generalErrorResId))
        } else if (state.generalErrorMessage != null) {
            ErrorText(state.generalErrorMessage)
        }

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(48.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.already_have_account) + " ",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = stringResource(id = R.string.sign_in),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onEvent(RegisterEvent.LoginClicked) }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    SafwaTheme {
        RegisterScreen(
            state = RegisterState(),
            onEvent = {}
        )
    }
}