package com.example.mvvm_news_app.models


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

//Tells the app that this will be a table in Room
@Entity(
    tableName = "Articles"
)
data class Article(
    @PrimaryKey(autoGenerate = true)
    val PK : Int? = null,
    val source: Source,
    val author: String?,
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
    val content: String?
)