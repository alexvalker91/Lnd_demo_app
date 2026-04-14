package alex.valker91.lnd_demo_app.di

import alex.valker91.lnd_demo_app.features.BalancesApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

private const val URL_BASE = "http://10.68.84.61:8080/"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofitInstance(): Retrofit = Retrofit.Builder()
        .baseUrl(URL_BASE)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideBalancesApiService(retrofit: Retrofit): BalancesApiService =
        retrofit.create(BalancesApiService::class.java)
}