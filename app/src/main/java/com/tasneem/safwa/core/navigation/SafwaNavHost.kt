package com.tasneem.safwa.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tasneem.safwa.features.auth.presentation.login.LoginScreen
import com.tasneem.safwa.features.auth.presentation.login.LoginSideEffect
import com.tasneem.safwa.features.auth.presentation.login.LoginViewModel
import com.tasneem.safwa.features.onboarding.OnboardingScreen
import com.tasneem.safwa.features.search.presentation.SearchScreen
import com.tasneem.safwa.features.wishlist.presentation.WishlistScreen
import com.tasneem.safwa.features.wishlist.presentation.productdetails.presentation.view.ProductDetailsScreen

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

            LaunchedEffect(Unit) {
                viewModel.sideEffect.collect { effect ->
                    when (effect) {
                        is LoginSideEffect.NavigateToHome -> {
                            navController.navigate(ScreenRoute.Home) {
                                popUpTo(ScreenRoute.Login) { inclusive = true }
                            }
                        }
                        is LoginSideEffect.NavigateToSignUp -> navController.navigate(ScreenRoute.Register)
                        is LoginSideEffect.NavigateToForgotPassword -> navController.navigate(ScreenRoute.ForgotPassword)
                        is LoginSideEffect.NavigateAsGuest -> navController.navigate(ScreenRoute.Home)
                        else -> {  }
                    }
                }
            }

            LoginScreen(
                state = state,
                onEvent = { event ->
                    viewModel.onEvent(event)
                }
            )
        }

        composable<ScreenRoute.Register> {
        }

        composable<ScreenRoute.ForgotPassword> {
        }

        composable<ScreenRoute.Home> {
            MainScreen(
                onNavigateToProductDetails = {
                    navController.navigate(ScreenRoute.ProductDetails)
                },
                onNavigateToCart = {
                    navController.navigate(ScreenRoute.Cart)
                }
            )
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
            ProductDetailsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
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
