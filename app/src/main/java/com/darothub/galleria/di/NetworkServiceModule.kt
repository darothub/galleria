package com.darothub.galleria.di

import com.darothub.galleria.Keys
import com.darothub.galleria.api.ShutterImageService
import com.darothub.galleria.utils.Constant
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.CacheControl
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object NetworkServiceModule {

    val baseurl = Constant.BASE_URL

    @Singleton
    @Provides
    fun provideGsonConverterFactory(): GsonConverterFactory =
        GsonConverterFactory.create(GsonBuilder().setLenient().create())
    /**
     * A function to provide okHttp logger
     */
    @Singleton
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
    /**
     * A function to provide okHttp client
     */
    @Singleton
    @Provides
    fun provideOkHttpInstance(loggingInterceptor: HttpLoggingInterceptor): okhttp3.OkHttpClient {
        return okhttp3.OkHttpClient.Builder()
            .addInterceptor{
                val header = "Bearer ${Keys.token()}"
                var req = it.request()
                req = req.newBuilder()
                    .cacheControl(CacheControl.FORCE_NETWORK)
                    .addHeader("Authorization", header)
                    .build()
                val res = it.proceed(req)
                res
            }
            .addInterceptor(loggingInterceptor)
            .connectTimeout(5, TimeUnit.MINUTES)
            .readTimeout(5, TimeUnit.MINUTES)
            .build()
    }

    /**
     * A function to provide retrofit
     *
     */
    @Singleton
    @Provides
    fun provideRetrofitInstance(
        gson: GsonConverterFactory,
        client: okhttp3.OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseurl)
            .addConverterFactory(gson)
            .client(client)
            .build()
    }

    @Provides
    fun provideAuthServices(retrofit: Retrofit): ShutterImageService {
        return retrofit.create(ShutterImageService::class.java)
    }
}
