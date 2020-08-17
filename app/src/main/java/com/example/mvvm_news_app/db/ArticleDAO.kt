package com.example.mvvm_news_app.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.mvvm_news_app.models.Article

@Dao
interface ArticleDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE) // what happens when a conflict happens
    suspend fun upsertArticle(article: Article) : Long

    @Query("SELECT * FROM Articles")
    suspend fun getAllArticles() : LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)
}