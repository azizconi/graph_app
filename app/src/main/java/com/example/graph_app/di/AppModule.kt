package com.example.graph_app.di

import com.example.graph_app.core.utils.Constants
import com.example.graph_app.data.remote.GraphApi
import com.example.graph_app.data.repository.GraphRepositoryImpl
import com.example.graph_app.domain.repository.GraphRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .build()
    }


    @Provides
    @Singleton
    fun provideGraphApi(retrofit: Retrofit): GraphApi = retrofit.create(GraphApi::class.java)

    @Provides
    @Singleton
    fun provideGraphRepository(api: GraphApi): GraphRepository = GraphRepositoryImpl(api)


}