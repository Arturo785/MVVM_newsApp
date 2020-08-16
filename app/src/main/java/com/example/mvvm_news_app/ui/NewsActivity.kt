package com.example.mvvm_news_app.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.mvvm_news_app.R
import kotlinx.android.synthetic.main.activity_news.*


class NewsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        //We set the bottomNavigation with our navController
        bottomNavigationView.setupWithNavController(newsNavHostFragment.findNavController())
    }


}
