package com.tasneem.network.datasource.payment

import com.google.gson.annotations.SerializedName

data class DepositRequest(
    @SerializedName("credit_card")
    val creditCard: DepositCreditCard
)

data class DepositCreditCard(
    val number: String,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    val month: Int,
    val year: Int,
    @SerializedName("verification_value")
    val verificationValue: String
)

data class DepositResponse(
    val id: String
)


