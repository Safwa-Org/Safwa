package com.tasneem.network.datasource.auth

interface AuthRemoteDataSource {
    suspend fun registerCustomer(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        phone: String?
    ): Result<String>

    suspend fun loginCustomer(
        email: String,
        password: String
    ): Result<String>
}
