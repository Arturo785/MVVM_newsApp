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
}