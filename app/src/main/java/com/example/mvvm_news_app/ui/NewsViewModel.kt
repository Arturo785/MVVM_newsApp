package com.example.mvvm_news_app.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mvvm_news_app.NewsApplication
import com.example.mvvm_news_app.models.Article
import com.example.mvvm_news_app.models.NewsApiResponse
import com.example.mvvm_news_app.repository.NewsRepository
import com.example.mvvm_news_app.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(
    app: Application
    ,private val newsRepository : NewsRepository)
    : AndroidViewModel(app) { // ViewModels by default does not allow
    // constructor parameters so we need a factoryInstance to tell the ViewModel how it should be created
    // androidViewModel allow us to use the applicationContext

    val breakingNews : MutableLiveData<Resource<NewsApiResponse>> = MutableLiveData()
    var breakingNewsPage = 1 // is in here cause the viewModel is not destroyed with rotation
    var breakingNewsResponse : NewsApiResponse? = null

    val searchNews : MutableLiveData<Resource<NewsApiResponse>> = MutableLiveData()
    var searchNewsPage = 1 // is in here cause the viewModel is not destroyed with rotation
    var searchNewsResponse : NewsApiResponse? = null


    init {
        getBreakingNews("mx")
    }

    //this coroutine is only alive as long as the viewModel is alive
    fun getBreakingNews(coutryCode: String) = viewModelScope.launch {
        safeBreakingNewsCall(coutryCode)
    }

    fun searchRequestedNews(queryString : String) = viewModelScope.launch {
        safeSearchNewsCall(queryString)
    }


    private fun handleBreakingNewsResponse(response: Response<NewsApiResponse>) : Resource<NewsApiResponse>{
        if (response.isSuccessful){
            response.body()?.let { resultResponse ->
                breakingNewsPage++ // we add the number to be ready to the next request
                if (breakingNewsResponse == null){
                    breakingNewsResponse = resultResponse // if not news saved means it's a new request at page 1
                }
                else{ // we need more results more more pages
                    val oldArticles = breakingNewsResponse?.articles // takes the reference of the list of the old results
                    val newArticles = resultResponse.articles // takes the new results
                    oldArticles?.addAll(newArticles) // adds the new results to the list
                }
                return Resource.Success(breakingNewsResponse ?: resultResponse)
                // if we had old results sends the updated list with more data
                // otherwise only sends the results from the request
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsApiResponse>) : Resource<NewsApiResponse>{
        if (response.isSuccessful){
            response.body()?.let { resultResponse ->
                searchNewsPage++ // we add the number to be ready to the next request
                if (searchNewsResponse == null){
                    searchNewsResponse = resultResponse // if not news saved means it's a new request at page 1
                }
                else{ // we need more results more more pages
                    val oldArticles = searchNewsResponse?.articles // takes the reference of the list of the old results
                    val newArticles = resultResponse.articles // takes the new results
                    oldArticles?.addAll(newArticles) // adds the new results to the list
                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
                // if we had old results sends the updated list with more data
                // otherwise only sends the results from the request
            }
        }
        return Resource.Error(response.message())
    }

    // *****************************DATABASE FUNCTIONS

    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun getSavedNews() =
        newsRepository.getSavedNews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    //*************INTERNET CHECKING ***********************

    private fun hasInternetConnection(): Boolean{
        val connectivityManager = getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        //Obsolete check because our version is always higher
/*        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val activeNetwork = connectivityManager.activeNetwork?: return false
        }*/
        val activeNetwork = connectivityManager.activeNetwork?: return false // if is active or return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return when{
            capabilities.hasTransport(TRANSPORT_WIFI) -> true
            capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    // better to use the way of Mitch tho
    private suspend fun safeBreakingNewsCall(countryCode: String){
        breakingNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
                breakingNews.postValue(handleBreakingNewsResponse(response)) // sets the value of the request when it's done
            }
            else{
                breakingNews.postValue(Resource.Error("No internet detected"))
            }
        }
        catch (t: Throwable){
            when(t){
                is IOException -> breakingNews.postValue(Resource.Error("Network failure"))
                else -> breakingNews.postValue(Resource.Error("Another error :${t.cause}"))
            }
        }
    }

    // better to use the way of Mitch tho
    private suspend fun safeSearchNewsCall(query: String){
        searchNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = newsRepository.searchNews(query, searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response)) // sets the value of the request when it's done
            }
            else{
                searchNews.postValue(Resource.Error("No internet detected"))
            }
        }
        catch (t: Throwable){
            when(t){
                is IOException -> breakingNews.postValue(Resource.Error("Network failure"))
                else -> breakingNews.postValue(Resource.Error("Another error :${t.cause}"))
            }
        }
    }
}