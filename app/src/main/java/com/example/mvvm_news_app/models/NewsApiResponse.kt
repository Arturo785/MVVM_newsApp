package com.example.mvvm_news_app.models


import com.google.gson.annotations.SerializedName

data class NewsApiResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<Article>
)