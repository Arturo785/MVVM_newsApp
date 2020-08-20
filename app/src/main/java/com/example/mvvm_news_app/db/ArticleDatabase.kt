package com.example.mvvm_news_app.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mvvm_news_app.models.Article

@Database(
    entities = [Article::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class ArticleDatabase : RoomDatabase(){

    abstract fun getArticleDAO() : ArticleDAO

    companion object{
        @Volatile // To be thread safe, other threads sees when a thread changes this instance
        private var instance : ArticleDatabase? = null
        private var LOCK = Any() // to synchronize setting the instance, there will be only one instance

        //Overrides the invoke method to do this whenever is called
        //a() is equivalent to a.invoke()
        operator fun invoke(context : Context)=
            //Returns the instance otherwise creates one threadSafe
            instance ?: synchronized(LOCK){ // to be sure that only one thread is working in this
                //If it stills null creates the instance
                instance ?: createDatabase(context).also{ instance = it} // sets the result to our instance
            }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                ArticleDatabase::class.java,
                "Article_db.db"
            ).build()
    }


}