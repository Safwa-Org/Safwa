package com.tasneem.safwa.core.domain.model

data class Address(
    val id: String = "",
    val label: String = "",
    val recipientName: String = "",
    val street: String = "",
    val cityAndZip: String = "",
    val mobileNumber: String = ""
)