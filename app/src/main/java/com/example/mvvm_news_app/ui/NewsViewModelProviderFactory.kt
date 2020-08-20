package com.example.mvvm_news_app.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mvvm_news_app.repository.NewsRepository

//Tells the viewModel how it should be created and what it needs
class NewsViewModelProviderFactory(val newsRepository: NewsRepository,
                                   val app:Application) : ViewModelProvider.Factory{

    // T: ViewModel Constraints the T type to only be a viewModel
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NewsViewModel(app,newsRepository) as T // cast that as T
    }

}