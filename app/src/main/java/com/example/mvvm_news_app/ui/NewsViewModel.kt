package com.example.mvvm_news_app.ui

import androidx.lifecycle.ViewModel
import com.example.mvvm_news_app.repository.NewsRepository

class NewsViewModel(newsRepository : NewsRepository): ViewModel() { // ViewModels by default does not allow
    // constructor parameters so we need a factoryInstance to tell the ViewModel how it should be created
}