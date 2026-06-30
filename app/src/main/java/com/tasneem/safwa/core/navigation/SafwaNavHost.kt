package com.tasneem.safwa.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tasneem.safwa.features.auth.presentation.login.view.LoginScreen
import com.tasneem.safwa.features.auth.presentation.register.view.RegisterScreen
import com.tasneem.safwa.features.wishlist.presentation.WishlistScreen
import com.tasneem.safwa.features.onboarding.OnboardingScreen
import com.tasneem.safwa.features.productdetails.presentation.view.ProductDetailsScreen
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
