package com.tasneem.safwa.features.wishlist.presentation

import com.tasneem.safwa.features.wishlist.domain.model.Product

object WishlistMockData {
    val products = listOf(
        Product(
            id = "1",
            title = "Stylish Running Shoes",
            handle = "stylish-running-shoes",
            description = "Comfortable running shoes for daily use.",
            vendor = "Nike",
            productType = "Shoes",
            price = "120.00",
            currency = "USD",
            imageUrl = listOf("https://images.unsplash.com/photo-1542291026-7eec264c27ff"),
            imageAltText = listOf("Stylish Running Shoes")
        ),
        Product(
            id = "2",
            title = "Classic Leather Watch",
            handle = "classic-leather-watch",
            description = "A timeless leather watch.",
            vendor = "Fossil",
            productType = "Accessories",
            price = "85.00",
            currency = "USD",
            imageUrl = listOf("https://images.unsplash.com/photo-1524805444758-089113d48a6d"),
            imageAltText = listOf("Classic Leather Watch")
        ),
        Product(
            id = "3",
            title = "Wireless Headphones",
            handle = "wireless-headphones",
            description = "Noise-cancelling wireless headphones.",
            vendor = "Sony",
            productType = "Electronics",
            price = "299.99",
            currency = "USD",
            imageUrl = listOf("https://images.unsplash.com/photo-1505740420928-5e560c06d30e"),
            imageAltText = listOf("Wireless Headphones")
        )
    )
    
    val defaultCategories = listOf("All", "Fragrances", "Skincare")
}
