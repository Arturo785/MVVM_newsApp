package com.example.mvvm_news_app.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvvm_news_app.R
import com.example.mvvm_news_app.ui.NewsActivity
import com.example.mvvm_news_app.ui.NewsViewModel
import com.example.mvvm_news_app.util.adapters.NewsAdapter
import kotlinx.android.synthetic.main.fragment_saved_news.*

class SavedNewsFragment : Fragment(R.layout.fragment_breaking_news) {

    lateinit var viewModel : NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    val TAG = "Saved news fragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //allow as to access the viewModel created on the activity
        viewModel = (activity as NewsActivity).viewModel

        setUpNewsAdapter()

        // the action when clicked in an article
        newsAdapter.setItemClickListener {
            // creation of the bundle
            val bundle = Bundle().apply {
                putSerializable("article",it)
            }
            // we select an action to navigate to and attach the bundle as an argument
            findNavController().navigate(
                R.id.action_savedNewsFragment_to_articleFragment,
                bundle
            )
        }
    }

    private fun setUpNewsAdapter(){
        newsAdapter = NewsAdapter()
        rvSavedNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

}