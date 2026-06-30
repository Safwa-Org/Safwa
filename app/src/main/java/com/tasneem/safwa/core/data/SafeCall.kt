package com.tasneem.safwa.core.data

import com.tasneem.network.exception.AppException
import com.tasneem.network.exception.EmptyResponseException
import com.tasneem.network.exception.GraphQlException
import com.tasneem.network.exception.NetworkException
import com.tasneem.network.exception.NoInternetException
import com.tasneem.safwa.core.exception.DomainException

suspend fun <T> safeCall(block: suspend () -> T): T {
    return try {
        block()
    } catch (e: NoInternetException) {
        throw DomainException.NoInternet()
    } catch (e: NetworkException) {
        throw DomainException.NetworkError()
    } catch (e: GraphQlException) {
        throw DomainException.ServerError(e.errors)
    } catch (e: EmptyResponseException) {
        throw DomainException.NotFound()
    } catch (e: AppException) {
        throw DomainException.Unknown()
    }
}
