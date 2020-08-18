package com.example.mvvm_news_app.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvvm_news_app.R
import com.example.mvvm_news_app.ui.NewsActivity
import com.example.mvvm_news_app.ui.NewsViewModel
import com.example.mvvm_news_app.util.Resource
import com.example.mvvm_news_app.util.SEARCH_DELAY
import com.example.mvvm_news_app.util.adapters.NewsAdapter
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment : Fragment(R.layout.fragment_search_news){

    lateinit var viewModel : NewsViewModel

    lateinit var newsAdapter: NewsAdapter
    val TAG = "Search news fragment"

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
                R.id.action_searchNewsFragment_to_articleFragment,
                bundle
            )
        }

        var job : Job? = null
        etSearch.addTextChangedListener{ editable ->
            job?.cancel() // we cancel previous search if existent
            job = MainScope().launch { // we launch the job on mainScope to let it change the UI
                delay(SEARCH_DELAY) // delay to avoid unnecessary searches
                editable?.let { // if exists
                    if (editable.toString().isNotEmpty()){
                        viewModel.searchRequestedNews(editable.toString())// send the query to the viewModel
                    }
                }
            }
        }

        observeSearchNews()


    }

    private fun setUpNewsAdapter(){
        newsAdapter = NewsAdapter()
        rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun statusProgressBar(visible : Boolean){
        paginationProgressBar.visibility = if (visible) View.VISIBLE else View.INVISIBLE
    }

    private fun observeSearchNews(){
        viewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success ->{
                    statusProgressBar(false)
                    response.data?.let { responseData ->
                        newsAdapter.differ.submitList(responseData.articles)
                    }
                }
                is Resource.Error ->{
                    statusProgressBar(false)
                    response.message?.let { data ->
                        Log.e(TAG,"An error ocurred $data")
                    }
                }
                is Resource.Loading ->{
                    statusProgressBar(true)
                }
            }
        })
    }

}