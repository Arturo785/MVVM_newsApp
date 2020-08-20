package com.example.mvvm_news_app.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvm_news_app.R
import com.example.mvvm_news_app.ui.NewsActivity
import com.example.mvvm_news_app.ui.NewsViewModel
import com.example.mvvm_news_app.util.adapters.NewsAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_saved_news.*

class SavedNewsFragment : Fragment(R.layout.fragment_saved_news) {

    lateinit var viewModel : NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    val TAG = "Saved news fragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //allow as to access the viewModel created on the activity
        viewModel = (activity as NewsActivity).viewModel

        setUpNewsAdapter()

        observeNewsFromDB()

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

        ItemTouchHelper(createSwipeObjectHandler()).apply {
            attachToRecyclerView(rvSavedNews)
        }
    }

    private fun setUpNewsAdapter(){
        newsAdapter = NewsAdapter()
        rvSavedNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun observeNewsFromDB(){ // observes every change from the db and submits the list to the adapter
        viewModel.getSavedNews().observe(viewLifecycleOwner, Observer { articles ->
            newsAdapter.differ.submitList(articles)
        })
    }

    private fun createSwipeObjectHandler(): ItemTouchHelper.SimpleCallback {
        //anonymous object to manage the swipe action
        return object : ItemTouchHelper.SimpleCallback(
           ItemTouchHelper.UP or ItemTouchHelper.DOWN, // drag directions
           ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) // swipe directions
       { // overrides the functions of the interface
           override fun onMove(
               recyclerView: RecyclerView,
               viewHolder: RecyclerView.ViewHolder,
               target: RecyclerView.ViewHolder
           ): Boolean {
               return true // no change needed
           }

           override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
               val position = viewHolder.adapterPosition
               val article = newsAdapter.differ.currentList[position]
               viewModel.deleteArticle(article)
               // shows the snackBar and gives the chance to reInsert the article
               Snackbar.make(view!!, "Successfully erased the article", Snackbar.LENGTH_LONG).apply {
                   setAction("Undo"){
                       viewModel.saveArticle(article)
                   }
                   show()
               }
           }

       }
    }

}