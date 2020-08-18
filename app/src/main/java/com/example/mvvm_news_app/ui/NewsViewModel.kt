package com.example.mvvm_news_app.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvm_news_app.models.Article
import com.example.mvvm_news_app.models.NewsApiResponse
import com.example.mvvm_news_app.repository.NewsRepository
import com.example.mvvm_news_app.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(private val newsRepository : NewsRepository): ViewModel() { // ViewModels by default does not allow
    // constructor parameters so we need a factoryInstance to tell the ViewModel how it should be created

    val breakingNews : MutableLiveData<Resource<NewsApiResponse>> = MutableLiveData()
    val breakingNewsPage = 1 // is in here cause the viewModel is not destroyed with rotation

    val searchNews : MutableLiveData<Resource<NewsApiResponse>> = MutableLiveData()
    val searchNewsPage = 1 // is in here cause the viewModel is not destroyed with rotation


    init {
        getBreakingNews("mx")
    }

    //this coroutine is only alive as long as the viewModel is alive
    fun getBreakingNews(coutryCode: String) = viewModelScope.launch {
        breakingNews.postValue(Resource.Loading()) // sets the UI the state of loading
        val response = newsRepository.getBreakingNews(coutryCode,breakingNewsPage)
        breakingNews.postValue(handleBreakingNewsResponse(response)) // sets the value of the request when it's done
    }

    fun searchRequestedNews(queryString : String) = viewModelScope.launch {
        searchNews.postValue(Resource.Loading())
        val response = newsRepository.searchNews(queryString, searchNewsPage)
        searchNews.postValue(handleSearchNewsResponse(response))
    }


    private fun handleBreakingNewsResponse(response: Response<NewsApiResponse>) : Resource<NewsApiResponse>{
        if (response.isSuccessful){
            response.body()?.let { successResponse ->
                return Resource.Success(successResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsApiResponse>) : Resource<NewsApiResponse>{
        if (response.isSuccessful){
            response.body()?.let { successResponse ->
                return Resource.Success(successResponse)
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
}