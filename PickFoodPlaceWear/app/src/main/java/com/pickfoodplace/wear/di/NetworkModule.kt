package com.pickfoodplace.wear.di

import com.pickfoodplace.wear.BuildConfig
import com.pickfoodplace.wear.data.local.TokenStorage
import com.pickfoodplace.wear.data.remote.AuthInterceptor
import com.pickfoodplace.wear.data.remote.MockInterceptor
import com.pickfoodplace.wear.data.remote.TokenAuthenticator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(tokenStorage: TokenStorage): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.BASIC
        }
        val builder = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenStorage))
            .authenticator(TokenAuthenticator(tokenStorage))
            .addInterceptor(logging)
        if (BuildConfig.USE_MOCK) {
            builder.addInterceptor(MockInterceptor())
        }
        return builder.build()
    }
}