package com.example.mvvm_news_app.repository

import com.example.mvvm_news_app.api.RetrofitInstance
import com.example.mvvm_news_app.db.ArticleDatabase
import com.example.mvvm_news_app.models.Article
import com.example.mvvm_news_app.models.NewsApiResponse
import retrofit2.Response

class NewsRepository(private val db: ArticleDatabase) {

    suspend fun getBreakingNews(countryCode: String = "mx", pageNumber: Int = 1)=
         RetrofitInstance.api.getBreakingNews(countryCode,pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchForNews(searchQuery,pageNumber)

    suspend fun upsert(article: Article) =
        db.getArticleDAO().upsertArticle(article)

    fun getSavedNews()=
        db.getArticleDAO().getAllArticles()

    suspend fun deleteArticle(article: Article) =
        db.getArticleDAO().deleteArticle(article)

}