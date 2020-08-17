package com.example.mvvm_news_app.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.mvvm_news_app.R
import com.example.mvvm_news_app.db.ArticleDatabase
import com.example.mvvm_news_app.repository.NewsRepository
import kotlinx.android.synthetic.main.activity_news.*


//Acts and it's the holder of all the fragments
class NewsActivity : AppCompatActivity() {

    lateinit var viewModel: NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        //Creates the repository, the factoryProvider and sets the ViewModel with
        // our provider and our own viewModel
        val newsRepository = NewsRepository(ArticleDatabase(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(newsRepository)
        viewModel = ViewModelProvider(this,viewModelProviderFactory).get(NewsViewModel::class.java)

        //We set the bottomNavigation with our navController
        bottomNavigationView.setupWithNavController(newsNavHostFragment.findNavController())
    }


}
