package com.tasneem.safwa.core.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products_table")
data class ProductNode(
    @PrimaryKey
    val id: String,
    val title: String,
    val handle: String,
    val description: String,
    val vendor: String,
    val productType: String,
)