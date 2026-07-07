package alex.valker91.lnd_demo_app.di

import alex.valker91.lnd_demo_app.features.BalancesApiService
import alex.valker91.lnd_demo_app.features.UserApiService
import alex.valker91.lnd_demo_app.features.MoneyTransferService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

import io.sentry.okhttp.SentryOkHttpInterceptor
//import io.sentry.android.okhttp.SentryOkHttpInterceptor

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LocalRetrofit

private const val URL_BASE = "http://10.68.84.61:8080/"
private const val LOCAL_URL_BASE = "http://10.0.2.2:8083/"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(SentryOkHttpInterceptor())
            .build()
    }

    @Provides
    @Singleton
    @MainRetrofit
    fun provideMainRetrofitInstance(): Retrofit = Retrofit.Builder()
        .baseUrl(URL_BASE)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideBalancesApiService(@MainRetrofit retrofit: Retrofit): BalancesApiService =
        retrofit.create(BalancesApiService::class.java)

    @Provides
    @Singleton
    fun provideMoneyTransferService(@MainRetrofit retrofit: Retrofit): MoneyTransferService =
        retrofit.create(MoneyTransferService::class.java)


    @Provides
    @Singleton
    @LocalRetrofit
    fun provideLocalRetrofitInstance(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(LOCAL_URL_BASE)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideUserApiService(@LocalRetrofit retrofit: Retrofit): UserApiService =
        retrofit.create(UserApiService::class.java)
}