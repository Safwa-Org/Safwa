package com.tasneem.safwa.features.core.domain

interface AuthDataSource {
    fun getCurrentUserId(): String?
}
