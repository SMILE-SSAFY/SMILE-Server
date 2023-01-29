package com.ssafy.smile.data


import com.ssafy.smile.data.remote.service.*
import javax.inject.Singleton

@Singleton
class ServiceInstances(retrofitInstances : RetrofitInstances) {
    private val userApiService : UserApiService = retrofitInstances.getRetrofit().create(UserApiService::class.java)

    @Singleton
    private val portfolioApiService: PortfolioApiService = retrofitInstances.getRetrofit().create(PortfolioApiService::class.java)

    @Singleton
    private val heartApiService: HeartApiService = retrofitInstances.getRetrofit().create(HeartApiService::class.java)

    @Singleton
    private val photographerApiService : PhotographerApiService = retrofitInstances.getRetrofit().create(PhotographerApiService::class.java)

    @Singleton
    private val postApiService: PostApiService = retrofitInstances.getRetrofit().create(PostApiService::class.java)

    @Singleton
    private val searchApiService: SearchApiService = retrofitInstances.getRetrofit().create(SearchApiService::class.java)

    fun getUserApiService() : UserApiService = userApiService
    fun getPortfolioApiService(): PortfolioApiService = portfolioApiService
    fun getHeartApiService(): HeartApiService = heartApiService
    fun getPhotographerApiService() : PhotographerApiService = photographerApiService
    fun getPostApiService(): PostApiService = postApiService
    fun getSearchApiService(): SearchApiService = searchApiService

}