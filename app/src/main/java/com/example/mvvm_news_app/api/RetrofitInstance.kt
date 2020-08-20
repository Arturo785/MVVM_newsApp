package com.example.mvvm_news_app.api

import com.example.mvvm_news_app.util.API_KEY
import com.example.mvvm_news_app.util.BASE_URL
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {

    companion object{

        private val retrofit by lazy {
            //by lazy { ... } performs its initializer where the defined property is first used, not its declaration.
            // useful to avoid to many memory used
            val loggin = HttpLoggingInterceptor()
            loggin.setLevel(HttpLoggingInterceptor.Level.BODY)

            val client = OkHttpClient.Builder()
                .addInterceptor(loggin)
                .addInterceptor {chain -> // we add the apiKey on the interceptor created by a lambda
                    val newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", API_KEY)
                        .build()
                        chain.proceed(newRequest)
                }
                .build()

            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }

        val api by lazy {
                retrofit.create(InewsAPI::class.java)
        }
    }
}