package com.tasneem.network.datasource.auth

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.tasneem.safwa.network.CustomerAccessTokenCreateMutation
import com.tasneem.safwa.network.CustomerCreateMutation
import com.tasneem.safwa.network.type.CustomerAccessTokenCreateInput
import com.tasneem.safwa.network.type.CustomerCreateInput
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRemoteDataSourceImpl @Inject constructor(
    private val apolloClient: ApolloClient
) : AuthRemoteDataSource {

    override suspend fun registerCustomer(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        phone: String?
    ): Result<String> {
        return try {
            val input = CustomerCreateInput(
                email = email,
                password = password,
                firstName = Optional.presentIfNotNull(firstName.takeIf { it.isNotBlank() }),
                lastName = Optional.presentIfNotNull(lastName.takeIf { it.isNotBlank() }),
                phone = Optional.presentIfNotNull(phone?.takeIf { it.isNotBlank() })
            )

            val response = apolloClient.mutation(CustomerCreateMutation(input)).execute()
            val errors = response.data?.customerCreate?.customerUserErrors
            
            if (!errors.isNullOrEmpty()) {
                val errorMsg = errors.first().message
                return Result.failure(Exception(errorMsg))
            }

            // Successfully created. Now log them in to get an access token
            loginCustomer(email, password)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loginCustomer(
        email: String,
        password: String
    ): Result<String> {
        return try {
            val input = CustomerAccessTokenCreateInput(
                email = email,
                password = password
            )

            val response = apolloClient.mutation(CustomerAccessTokenCreateMutation(input)).execute()
            val errors = response.data?.customerAccessTokenCreate?.customerUserErrors
            
            if (!errors.isNullOrEmpty()) {
                val errorMsg = errors.first().message
                return Result.failure(Exception(errorMsg))
            }

            val token = response.data?.customerAccessTokenCreate?.customerAccessToken?.accessToken
            if (token != null) {
                Result.success(token)
            } else {
                Result.failure(Exception("Customer access token is null"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
