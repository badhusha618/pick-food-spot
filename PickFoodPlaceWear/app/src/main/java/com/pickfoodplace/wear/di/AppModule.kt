package com.pickfoodplace.wear.di

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.work.WorkManager
import com.pickfoodplace.wear.BuildConfig
import com.pickfoodplace.wear.data.local.AppDatabase
import com.pickfoodplace.wear.data.local.TokenStorage
import com.pickfoodplace.wear.data.remote.ApiService
import com.pickfoodplace.wear.repository.AuthRepository
import com.pickfoodplace.wear.repository.BookingRepository
import com.pickfoodplace.wear.repository.PlaceRepository
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMasterKey(@ApplicationContext context: Context): MasterKey =
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

    @Provides
    @Singleton
    fun provideEncryptedPrefs(@ApplicationContext context: Context, masterKey: MasterKey) =
        EncryptedSharedPreferences.create(
            context,
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

    @Provides
    @Singleton
    fun provideTokenStorage(prefs: android.content.SharedPreferences): TokenStorage = TokenStorage(prefs)

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder().build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: okhttp3.OkHttpClient, moshi: Moshi): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase = AppDatabase.build(context)

    @Provides
    @Singleton
    fun providePlaceRepository(api: ApiService, db: AppDatabase): PlaceRepository = PlaceRepository(api, db.placeDao())

    @Provides
    @Singleton
    fun provideAuthRepository(api: ApiService, tokenStorage: TokenStorage): AuthRepository = AuthRepository(api, tokenStorage)

    @Provides
    @Singleton
    fun provideBookingRepository(api: ApiService, db: AppDatabase, workManager: WorkManager): BookingRepository =
        BookingRepository(api, db.bookingDao(), workManager)

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager = WorkManager.getInstance(context)
}