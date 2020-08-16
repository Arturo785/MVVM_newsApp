package com.example.mvvm_news_app.api

import com.example.mvvm_news_app.models.NewsApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface InewsAPI {

    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
       @Query("country") countryCode : String = "mx",
       @Query("page") pageNumber : Int = 1
    ): Response<NewsApiResponse>

    @GET("v2/everything")
    suspend fun searchForNews(
        @Query("q") searchQuery : String,
        @Query("page") pageNumber : Int = 1
    ): Response<NewsApiResponse>
}