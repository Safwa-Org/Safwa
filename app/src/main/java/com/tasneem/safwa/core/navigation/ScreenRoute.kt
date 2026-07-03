package com.tasneem.safwa.core.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class ScreenRoute {

    @Serializable
    object Splash : ScreenRoute()

    @Serializable
    object Onboarding : ScreenRoute()

    @Serializable
    object Login : ScreenRoute()

    @Serializable
    object Register : ScreenRoute()

    @Serializable
    object ForgotPassword : ScreenRoute()

    @Serializable
    object Home : ScreenRoute()

    @Serializable
    object Search : ScreenRoute()

    @Serializable
    object Wishlist : ScreenRoute()

    @Serializable
    object Profile : ScreenRoute()

    @Serializable
    data class ProductDetails(val handle: String) : ScreenRoute()

    @Serializable
    object Cart : ScreenRoute()

    @Serializable
    object Checkout : ScreenRoute()

    @Serializable
    object Payment : ScreenRoute()

    @Serializable
    object OrderConfirmation : ScreenRoute()

    @Serializable
    object OrderHistory : ScreenRoute()

    @Serializable
    object SavedAddresses : ScreenRoute()

    @Serializable
    object PaymentMethods : ScreenRoute()

    @Serializable
    object Notifications : ScreenRoute()

    @Serializable
    object Language : ScreenRoute()

    @Serializable
    object Categories : ScreenRoute()

    @Serializable
    data class CategoryProducts(val categoryName: String) : ScreenRoute()

    @Serializable
    data class OrderDetails(val orderJson: String) : ScreenRoute()
}