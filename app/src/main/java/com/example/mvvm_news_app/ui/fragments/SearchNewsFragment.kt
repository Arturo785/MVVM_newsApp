package com.example.mvvm_news_app.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvm_news_app.R
import com.example.mvvm_news_app.ui.NewsActivity
import com.example.mvvm_news_app.ui.NewsViewModel
import com.example.mvvm_news_app.util.QUERY_PAGE_SIZE
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

    //control of pagination
    private var _isLoading = false
    private var _isLastPage = false
    private var _isScrolling = false

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
                        viewModel.searchNewsPage = 1
                        viewModel.searchNewsResponse = null // resets the data in a nw search
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
            addOnScrollListener(this@SearchNewsFragment.scrollListener)
        }
    }

    private fun statusProgressBar(visible : Boolean){
        paginationProgressBar.visibility = if (visible) View.VISIBLE else View.INVISIBLE
        _isLoading = visible
    }

    private fun observeSearchNews(){
        viewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success ->{
                    statusProgressBar(false)
                    response.data?.let { responseData ->
                        newsAdapter.differ.submitList(responseData.articles.toList())
                        val totalPages = responseData.totalResults / QUERY_PAGE_SIZE + 2
                        //+1 because of the round of dividing 2 ints and other because the last
                        // page of the response will always be empty
                        _isLastPage = viewModel.searchNewsPage == totalPages
                        if (_isLastPage){
                            rvSearchNews.setPadding(0,0,0,0)
                        }
                    }
                }
                is Resource.Error ->{
                    statusProgressBar(false)
                    response.message?.let { data ->
                        Toast.makeText(activity, "An error occurred $data", Toast.LENGTH_LONG).show()
                        Log.e(TAG,"An error ocurred $data")
                    }
                }
                is Resource.Loading ->{
                    statusProgressBar(true)
                }
            }
        })
    }

    //Anonymous object to manage the scrolls
    private val scrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager

            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = (!_isLoading && !_isLastPage)
            val isAtLastItem = (firstVisibleItemPosition + visibleItemCount) >= totalItemCount
            // if the 1st on the recycler plus the other items visible reaches the total number
            // is time to load more results
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = (isNotLoadingAndNotLastPage && isAtLastItem && isTotalMoreThanVisible && isNotAtBeginning
                    && _isScrolling)

            if(shouldPaginate){
                viewModel.searchRequestedNews(etSearch.text.toString()) // the query inserted
                _isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                _isScrolling = true
            }
        }
    }

}