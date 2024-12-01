package com.example.graph_app.di

import com.example.graph_app.core.utils.Constants
import com.example.graph_app.data.remote.GraphApi
import com.example.graph_app.data.repository.GraphRepositoryImpl
import com.example.graph_app.domain.repository.GraphRepository
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    @Provides
    @Singleton
    fun provideGraphApi(retrofit: Retrofit): GraphApi = retrofit.create(GraphApi::class.java)

    @Provides
    @Singleton
    fun provideGraphRepository(api: GraphApi): GraphRepository = GraphRepositoryImpl(api)


}