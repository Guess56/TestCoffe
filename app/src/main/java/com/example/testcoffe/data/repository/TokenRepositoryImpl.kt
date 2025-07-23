package com.example.testcoffe.data.repository

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.testcoffe.domain.repository.TokenRepository
import androidx.core.content.edit

class TokenRepositoryImpl(context: Context) : TokenRepository {
    companion object {
        private const val KEY_TOKEN = "KEY_TOKEN"
        private const val KEY_TOKEN_LIFETIME = "KEY_TOKEN_LIFETIME"
    }

    private val sharedPreferences = EncryptedSharedPreferences.create(
        "secure_prefs",
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override suspend fun saveToken(token: String, lifetime: Long) {
        sharedPreferences.edit {
            putString(KEY_TOKEN, token)
                .putLong(KEY_TOKEN_LIFETIME, lifetime)
        }
    }

    override suspend fun getToken(): String? {
        val token = sharedPreferences.getString(KEY_TOKEN, null)
        return token
    }

    override suspend fun getTokenLifetime(): Long {
        val tokenLifeTime = sharedPreferences.getLong(KEY_TOKEN_LIFETIME,0L)
        return tokenLifeTime
    }

    override suspend fun clearToken() {
        sharedPreferences.edit { clear() }
    }
}