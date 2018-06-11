package com.pudding.tangentninety.di.module

import com.pudding.tangentninety.BuildConfig
import com.pudding.tangentninety.app.Constants
import com.pudding.tangentninety.module.http.api.ZhihuApis
import com.pudding.tangentninety.utils.SystemUtil

import java.io.File
import java.util.concurrent.TimeUnit

import javax.inject.Singleton

import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Error on 2017/6/22 0022.
 */

@Module
class HttpModule {
    @Singleton
    @Provides
    internal fun provideRetrofitBuilder(): Retrofit.Builder {
        return Retrofit.Builder()
    }


    @Singleton
    @Provides
    internal fun provideOkHttpBuilder(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
    }

    @Singleton
    @Provides
    internal fun provideZhihuRetrofit(builder: Retrofit.Builder, client: OkHttpClient): Retrofit {
        return createRetrofit(builder, client, ZhihuApis.HOST)
    }

    @Singleton
    @Provides
    internal fun provideClient(builder: OkHttpClient.Builder): OkHttpClient {
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
            builder.addInterceptor(loggingInterceptor)
        } else {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
            builder.addInterceptor(loggingInterceptor)
        }
        val cacheFile = File(Constants.PATH_NET_CACHE)
        val cache = Cache(cacheFile, (1024 * 1024 * 50).toLong())
        val cacheInterceptor = Interceptor { chain ->
            var request = chain.request()
            if (!SystemUtil.isNetworkAvailable) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build()
            }
            val response = chain.proceed(request)
            if (SystemUtil.isNetworkAvailable) {
                // 有网络时, 根据头部来决定是否请求网络
                response.newBuilder()
                        .build()
            } else {
                // 无网络时，使用缓存,设置超时为4周
                response.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + Constants.NO_NET_CACHE_TIME)
                        .removeHeader("Pragma")
                        .build()
            }
            response
        }
        //设置缓存
        builder.addNetworkInterceptor(cacheInterceptor)
        builder.addInterceptor(cacheInterceptor)
        builder.cache(cache)
        //设置超时
        builder.connectTimeout(10, TimeUnit.SECONDS)
        builder.readTimeout(20, TimeUnit.SECONDS)
        builder.writeTimeout(20, TimeUnit.SECONDS)
        //错误重连
        builder.retryOnConnectionFailure(true)
        return builder.build()
    }

    @Singleton
    @Provides
    internal fun provideZhihuService(retrofit: Retrofit): ZhihuApis {
        return retrofit.create(ZhihuApis::class.java)
    }

    private fun createRetrofit(builder: Retrofit.Builder, client: OkHttpClient, url: String): Retrofit {
        return builder
                .baseUrl(url)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

}

