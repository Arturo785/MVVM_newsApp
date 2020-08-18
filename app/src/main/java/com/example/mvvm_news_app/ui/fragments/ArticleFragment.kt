package com.example.mvvm_news_app.ui.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.mvvm_news_app.R
import com.example.mvvm_news_app.ui.NewsActivity
import com.example.mvvm_news_app.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_article.*

class ArticleFragment : Fragment(R.layout.fragment_article), View.OnClickListener{

    lateinit var viewModel : NewsViewModel
    // gets the article passed through the transition made by the nav_graph
    val args : ArticleFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //allow as to access the viewModel created on the activity
        viewModel = (activity as NewsActivity).viewModel

        // returns the article passed through the transition made by the nav_graph
        val article = args.article

        webView.apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            loadUrl(article.url)
        }

        fab.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        viewModel.saveArticle(args.article)
        Snackbar.make(p0!!,"Article saved successfully", Snackbar.LENGTH_SHORT).show()
    }

}