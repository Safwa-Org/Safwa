package com.tasneem.safwa.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tasneem.safwa.features.auth.presentation.login.LoginEvent
import com.tasneem.safwa.features.auth.presentation.login.LoginScreen
import com.tasneem.safwa.features.auth.presentation.login.LoginViewModel
import com.tasneem.safwa.features.wishlist.presentation.WishlistScreen
import com.tasneem.safwa.features.onboarding.OnboardingScreen
import com.tasneem.safwa.features.search.presentation.SearchScreen
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
            val viewModel: LoginViewModel = viewModel()
            val state by viewModel.state.collectAsState()

            LoginScreen(
                state = state,
                onEvent = { event ->
                    viewModel.onEvent(event)
                    when (event) {
                        is LoginEvent.SignUpClicked -> navController.navigate(ScreenRoute.Register)
                        is LoginEvent.ForgotPasswordClicked -> navController.navigate(ScreenRoute.ForgotPassword)
                        is LoginEvent.GuestClicked -> navController.navigate(ScreenRoute.Home)
                        else -> { /* Other events handled by ViewModel */ }
                    }
                }
            )
        }

        composable<ScreenRoute.Register> {
        }

        composable<ScreenRoute.ForgotPassword> {
        }

        composable<ScreenRoute.Home> {
        }

        composable<ScreenRoute.Search> {
            SearchScreen()
        }

        composable<ScreenRoute.Wishlist> {
            WishlistScreen()
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
