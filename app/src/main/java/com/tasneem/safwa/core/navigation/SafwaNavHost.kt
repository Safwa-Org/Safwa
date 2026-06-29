package com.tasneem.safwa.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tasneem.safwa.features.auth.presentation.login.LoginScreen
import com.tasneem.safwa.features.auth.presentation.login.LoginSideEffect
import com.tasneem.safwa.features.auth.presentation.login.LoginViewModel
import com.tasneem.safwa.features.auth.presentation.register.RegisterScreen
import com.tasneem.safwa.features.auth.presentation.register.RegisterSideEffect
import com.tasneem.safwa.features.auth.presentation.register.RegisterViewModel
import com.tasneem.safwa.features.onboarding.OnboardingScreen

@Composable
fun SafwaNavHost(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = ScreenRoute.Onboarding
    ) {

        composable<ScreenRoute.Splash> {

        }

        composable<ScreenRoute.Onboarding> {
            OnboardingScreen(
                onFinish = {
                    navController.navigate(ScreenRoute.Login) {
                        popUpTo(ScreenRoute.Onboarding) { inclusive = true }
                    }
                }
            )
        }

        composable<ScreenRoute.Login> {
            val viewModel: LoginViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()
            LaunchedEffect(Unit) {
                viewModel.sideEffect.collect { effect ->
                    when (effect) {
                        LoginSideEffect.NavigateToSignUp -> navController.navigate(ScreenRoute.Register)
                        LoginSideEffect.NavigateToForgotPassword -> navController.navigate(ScreenRoute.ForgotPassword)
                        LoginSideEffect.NavigateToHome -> navController.navigate(ScreenRoute.Home) {
                            popUpTo(ScreenRoute.Login) { inclusive = true }
                        }
                        is LoginSideEffect.ShowToast -> {

                        }
                    }
                }
            }

            LoginScreen(
                state = state,
                onEvent = { event ->
                    viewModel.onEvent(event)
                },
                onGoogleSignInResult = { idToken ->
                    viewModel.handleGoogleLogin(idToken)
                }
            )
        }

        composable<ScreenRoute.Register> {
            val viewModel: RegisterViewModel = viewModel()
            val state by viewModel.state.collectAsState()

            LaunchedEffect(Unit) {
                viewModel.sideEffect.collect { effect ->
                    when (effect) {
                        RegisterSideEffect.NavigateToLogin -> navController.navigate(ScreenRoute.Login) {
                            popUpTo(ScreenRoute.Register) { inclusive = true }
                        }
                        RegisterSideEffect.NavigateToHome -> navController.navigate(ScreenRoute.Home) {
                            popUpTo(ScreenRoute.Register) { inclusive = true }
                        }
                        is RegisterSideEffect.ShowToast -> {
                            // Show toast
                        }
                    }
                }
            }

            RegisterScreen(
                state = state,
                onEvent = { event ->
                    viewModel.onEvent(event)
                }
            )
        }

        composable<ScreenRoute.ForgotPassword> {
        }

        composable<ScreenRoute.Home> {
        }

        composable<ScreenRoute.Search> {
        }

        composable<ScreenRoute.Wishlist> {
        }

        composable<ScreenRoute.Profile> {
        }

        composable<ScreenRoute.ProductDetails> {
        }

        composable<ScreenRoute.Cart> {
        }

        composable<ScreenRoute.Checkout> {
        }

        composable<ScreenRoute.OrderConfirmation> {
        }

        composable<ScreenRoute.OrderHistory> {
        }

        composable<ScreenRoute.SavedAddresses> {
        }

        composable<ScreenRoute.PaymentMethods> {
        }

        composable<ScreenRoute.Notifications> {
        }

        composable<ScreenRoute.Language> {
        }
    }
}