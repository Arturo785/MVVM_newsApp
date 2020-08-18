package com.example.mvvm_news_app.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View

import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvvm_news_app.R
import com.example.mvvm_news_app.ui.NewsActivity
import com.example.mvvm_news_app.ui.NewsViewModel
import com.example.mvvm_news_app.util.Resource
import com.example.mvvm_news_app.util.adapters.NewsAdapter
import kotlinx.android.synthetic.main.fragment_breaking_news.*


class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {

    lateinit var viewModel : NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    val TAG = "Breaking news fragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //allow as to access the viewModel created on the activity
        viewModel = (activity as NewsActivity).viewModel
        setUpNewsAdapter()

        observeBreakingNews()
    }

    private fun setUpNewsAdapter(){
        newsAdapter = NewsAdapter()
        rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun statusProgressBar(visible : Boolean){
        paginationProgressBar.visibility = if (visible) View.VISIBLE else View.INVISIBLE
    }

    private fun observeBreakingNews(){
        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success ->{
                    statusProgressBar(false)
                    response.data?.let {
                        newsAdapter.differ.submitList(it.articles)
                    }
                }
                is Resource.Error ->{
                    statusProgressBar(false)
                    response.message?.let {
                        Log.e(TAG,"An error ocurred $it")
                    }
                }
                is Resource.Loading ->{
                    statusProgressBar(true)
                }
            }
        })
    }



}