package com.tasneem.safwa.features.auth.presentation.login.view

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.tasneem.safwa.R
import com.tasneem.safwa.core.shared_component.CustomButon
import com.tasneem.safwa.core.shared_component.SafwaLogo
import com.tasneem.safwa.features.auth.presentation.components.CustomTextField
import com.tasneem.safwa.features.auth.presentation.components.ErrorText
import com.tasneem.safwa.features.auth.presentation.components.GoogleButton
import com.tasneem.safwa.features.auth.presentation.components.OrDivider
import com.tasneem.safwa.features.auth.presentation.login.state.LoginEvent
import com.tasneem.safwa.features.auth.presentation.login.state.LoginSideEffect
import com.tasneem.safwa.features.auth.presentation.login.state.LoginState
import com.tasneem.safwa.features.auth.presentation.login.viewmodel.LoginViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface GoogleSignInClientEntryPoint {
    fun googleSignInClient(): GoogleSignInClient
}
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                LoginSideEffect.NavigateToSignUp -> onNavigateToSignUp()
                LoginSideEffect.NavigateToForgotPassword -> onNavigateToForgotPassword()
                LoginSideEffect.NavigateToHome -> onNavigateToHome()
                is LoginSideEffect.ShowToast -> {
                }
            }
        }
    }

    LoginContent(
        state = state,
        onEvent = viewModel::onEvent,
        onGoogleSignInResult = viewModel::handleGoogleLogin
    )
}

@Composable
fun LoginContent(
    state: LoginState,
    onEvent: (LoginEvent) -> Unit,
    onGoogleSignInResult: (String) -> Unit,
    modifier: Modifier = Modifier
){
    val context = LocalContext.current
    val googleSignInClient = remember {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            GoogleSignInClientEntryPoint::class.java
        )
        entryPoint.googleSignInClient()
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken
                if (idToken != null) {
                    onGoogleSignInResult(idToken)
                } else {
                    onEvent(LoginEvent.ShowError("Google ID token missing"))
                }
            } catch (e: ApiException) {
                Log.e("GoogleSignIn", "Sign-in failed", e)
                onEvent(LoginEvent.ShowError("Google sign-in failed: ${e.message}"))
            }
        } else {
            Log.d("GoogleSignIn", "Sign-in cancelled by user")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            SafwaLogo(
                size = 40.dp,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                iconColor = MaterialTheme.colorScheme.primary
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
            onValueChange = { onEvent(LoginEvent.FormInputChanged(email = it)) },
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Email
        )

        state.emailErrorResId?.let { errorRes ->
            ErrorText(stringResource(errorRes))
        }
        Spacer(modifier = Modifier.height(24.dp))

        CustomTextField(
            label = stringResource(id = R.string.password),
            value = state.password,
            onValueChange = { onEvent(LoginEvent.FormInputChanged(password = it)) },
            isPassword = true
        )
        state.passwordErrorResId?.let { errorRes ->
            ErrorText(stringResource(errorRes))
        }

        Spacer(modifier = Modifier.height(32.dp))

        CustomButon(
            title = stringResource(id = R.string.sign_in),
            onContinue = { onEvent(LoginEvent.LoginClicked) },
            modifier = Modifier.fillMaxWidth(),
        )

        if (state.generalErrorMessage != null) {
            ErrorText(state.generalErrorMessage ?: "")
        }
        state.generalErrorResId?.let {
            errorRes -> ErrorText(stringResource(errorRes))
        }

        Spacer(modifier = Modifier.height(32.dp))
        OrDivider()

        Spacer(modifier = Modifier.height(32.dp))

        GoogleButton(
            onContinue = {
                val signInIntent = googleSignInClient.signInIntent
                launcher.launch(signInIntent)
            },
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
                text = stringResource(id = R.string.new_to_safwa) + " ",
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