package com.tasneem.network.dto

data class ProductDto(
    val id: String,
    val handle: String,
    val title: String,
    val description: String?,
    val vendor: String,
    val productType: String,
    val price: String,
    val currency: String,
    val imageUrls: List<String>?,
    val imageAlts: List<String>?
)