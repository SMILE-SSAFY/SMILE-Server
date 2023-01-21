package com.ssafy.smile.data

import com.ssafy.smile.data.remote.datasource.ExampleRemoteDataSourceImpl
import com.ssafy.smile.data.remote.datasource.PortfolioRemoteDataSourceImpl
import com.ssafy.smile.data.remote.datasource.UserRemoteDataSourceImpl
import com.ssafy.smile.data.remote.service.ExampleApiService
import javax.inject.Singleton

@Singleton
class DataSourceInstances(serviceInstances: ServiceInstances) {

    @Singleton
    private val exampleRemoteDataSourceImpl : ExampleRemoteDataSourceImpl = ExampleRemoteDataSourceImpl(serviceInstances.getExampleApiService())

    @Singleton
    private val userRemoteDataSourceImpl : UserRemoteDataSourceImpl = UserRemoteDataSourceImpl(serviceInstances.getUserApiService())

    @Singleton
    private val portfolioRemoteDataSourceImpl: PortfolioRemoteDataSourceImpl = PortfolioRemoteDataSourceImpl(serviceInstances.getPortfolioApiService())

    fun getExampleRemoteDataSource() : ExampleRemoteDataSourceImpl = exampleRemoteDataSourceImpl
    fun getUserRemoteDataSource(): UserRemoteDataSourceImpl = userRemoteDataSourceImpl
    fun getPortfolioRemoteDataSource(): PortfolioRemoteDataSourceImpl = portfolioRemoteDataSourceImpl

}