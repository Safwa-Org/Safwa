package com.tasneem.safwa.core.dp

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SafwaTypeConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, type) ?: emptyList()
    }

    @TypeConverter
    fun fromOrderLineItemList(value: List<com.tasneem.safwa.features.settings.orderhistory.domain.model.OrderLineItem>?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toOrderLineItemList(value: String): List<com.tasneem.safwa.features.settings.orderhistory.domain.model.OrderLineItem> {
        val type = object : TypeToken<List<com.tasneem.safwa.features.settings.orderhistory.domain.model.OrderLineItem>>() {}.type
        return gson.fromJson(value, type) ?: emptyList()
    }
}