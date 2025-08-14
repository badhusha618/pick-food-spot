package com.pickfoodplace.wear.repository

import com.pickfoodplace.wear.data.local.TokenStorage
import com.pickfoodplace.wear.data.remote.ApiService
import com.pickfoodplace.wear.data.remote.dto.AuthRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(
    private val api: ApiService,
    private val tokenStorage: TokenStorage
) {
    suspend fun login(email: String, password: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val res = api.login(AuthRequest(email, password))
            tokenStorage.saveTokens(res.token, res.refreshToken)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(email: String, password: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val res = api.register(AuthRequest(email, password))
            tokenStorage.saveTokens(res.token, res.refreshToken)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        tokenStorage.clear()
    }

    fun isLoggedIn(): Boolean = tokenStorage.getAccessToken() != null
}