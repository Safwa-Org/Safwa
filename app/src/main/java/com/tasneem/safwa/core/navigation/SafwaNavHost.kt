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
import com.tasneem.safwa.features.wishlist.presentation.WishlistScreen
import com.tasneem.safwa.features.onboarding.OnboardingScreen
import com.tasneem.safwa.features.productdetails.presentation.view.ProductDetailsScreen
import com.tasneem.safwa.features.search.presentation.SearchScreen
import com.tasneem.safwa.features.cart.presentation.view.CartScreen
import com.tasneem.safwa.features.wishlist.presentation.WishlistScreen

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
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(ScreenRoute.Home) {
                        popUpTo(ScreenRoute.Login) { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    navController.navigate(ScreenRoute.Register)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(ScreenRoute.ForgotPassword)
                }
            )
        }

        composable<ScreenRoute.Register> {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate(ScreenRoute.Login) {
                        popUpTo(ScreenRoute.Register) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(ScreenRoute.Home) {
                        popUpTo(ScreenRoute.Register) { inclusive = true }
                    }
                }
            )
        }
        composable<ScreenRoute.ForgotPassword> {
        }

        composable<ScreenRoute.Home> {
            MainScreen(
                onNavigateToProductDetails = {
                    // navController.navigate(ScreenRoute.ProductDetails)
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
            CartScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCheckout = { navController.navigate(ScreenRoute.Checkout) }
            )
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
