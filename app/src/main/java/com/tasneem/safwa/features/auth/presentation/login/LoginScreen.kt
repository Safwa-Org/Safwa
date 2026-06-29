package com.tasneem.safwa.features.auth.presentation.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tasneem.safwa.R
import com.tasneem.safwa.core.shared_component.CustomButon
import com.tasneem.safwa.core.shared_component.SafwaLogo
import com.tasneem.safwa.core.theme.SafwaTheme
import com.tasneem.safwa.features.auth.presentation.components.CustomTextField
import com.tasneem.safwa.features.auth.presentation.components.GoogleButton
import com.tasneem.safwa.features.auth.presentation.components.OrDivider

@Composable
fun LoginScreen(
    state: LoginState,
    onEvent: (LoginEvent) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
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
            text = stringResource(id = R.string.welcome_back),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            fontFamily = FontFamily.Serif
        )
        
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.sign_in_desc),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        CustomTextField(
            label = stringResource(id = R.string.email),
            value = state.email,
            onValueChange = { onEvent(LoginEvent.EmailChanged(it)) },
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Email
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        CustomTextField(
            label = stringResource(id = R.string.password),
            value = state.password,
            onValueChange = { onEvent(LoginEvent.PasswordChanged(it)) },
            isPassword = true
        )
        
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = stringResource(id = R.string.forgot_password),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .clickable { onEvent(LoginEvent.ForgotPasswordClicked) }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        CustomButon(
            title = stringResource(id = R.string.sign_in),
            onContinue = { onEvent(LoginEvent.LoginClicked) },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        OrDivider()
        
        Spacer(modifier = Modifier.height(32.dp))
        
        GoogleButton(
            onContinue = { onEvent(LoginEvent.GoogleLoginClicked) },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(id = R.string.continue_as_guest),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.clickable { onEvent(LoginEvent.GuestClicked) }
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(48.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.new_to_safwa),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = stringResource(id = R.string.create_account),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onEvent(LoginEvent.SignUpClicked) }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    SafwaTheme {
        LoginScreen(
            state = LoginState(),
            onEvent = {}
        )
    }
}
