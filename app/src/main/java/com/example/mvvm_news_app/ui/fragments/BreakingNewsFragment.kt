package com.example.mvvm_news_app.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast

import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.mvvm_news_app.R
import com.example.mvvm_news_app.ui.NewsActivity
import com.example.mvvm_news_app.ui.NewsViewModel
import com.example.mvvm_news_app.util.QUERY_PAGE_SIZE
import com.example.mvvm_news_app.util.Resource
import com.example.mvvm_news_app.util.adapters.NewsAdapter
import kotlinx.android.synthetic.main.fragment_breaking_news.*


class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {

    lateinit var viewModel : NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    val TAG = "Breaking news fragment"

    //control of pagination
    private var _isLoading = false
    private var _isLastPage = false
    private var _isScrolling = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //allow as to access the viewModel created on the activity
        viewModel = (activity as NewsActivity).viewModel

        refresh_news.apply {
            setOnRefreshListener(this@BreakingNewsFragment.refreshListener)
        }
        setUpNewsAdapter()

        // the action when clicked in an article
        newsAdapter.setItemClickListener {
            // creation of the bundle
            val bundle = Bundle().apply {
                putSerializable("article",it)
            }
            // we select an action to navigate to and attach the bundle as an argument
            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleFragment,
                bundle
            )
        }

        observeBreakingNews()
    }

    private fun setUpNewsAdapter(){
        newsAdapter = NewsAdapter()
        rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            //this way it calls the one we defined
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }

    private fun statusProgressBar(visible : Boolean){
        paginationProgressBar.visibility = if (visible) View.VISIBLE else View.INVISIBLE
        _isLoading = visible
    }

    private fun observeBreakingNews(){
        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success ->{
                    statusProgressBar(false)
                    response.data?.let {
                        //seems like mutable list does not work with differ
                        newsAdapter.differ.submitList(it.articles.toList())
                        val totalPages = it.totalResults / QUERY_PAGE_SIZE + 2
                        //+1 because of the round of dividing 2 ints and other because the last
                        // page of the response will always be empty
                        _isLastPage = viewModel.breakingNewsPage == totalPages
                        if (_isLastPage){
                            rvBreakingNews.setPadding(0,0,0,0)
                        }
                    }
                }
                is Resource.Error ->{
                    statusProgressBar(false)
                    response.message?.let {
                        Toast.makeText(activity, "An error occurred $it",Toast.LENGTH_LONG).show()
                        Log.e(TAG,"An error ocurred $it")
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
                viewModel.getBreakingNews("mx")
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

    private val refreshListener = SwipeRefreshLayout.OnRefreshListener {
        viewModel.breakingNewsPage = 1
        viewModel.breakingNewsResponse = null
        viewModel.getBreakingNews("mx")
        refresh_news.isRefreshing = false
    }



}