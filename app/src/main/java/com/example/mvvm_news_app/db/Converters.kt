package com.example.mvvm_news_app.db

import androidx.room.TypeConverter
import com.example.mvvm_news_app.models.Source

class Converters {


    //Because Room only handles primitive data Types we convert Source(our own data class) to primitive type
    //Article has Source as parameter
    @TypeConverter
    fun fromSource(source:Source) : String{
        return source.name
    }

    @TypeConverter
    fun toSource(data:String) : Source{
        return Source(null,data)
    }
}