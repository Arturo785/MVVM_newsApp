package com.example.mvvm_news_app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mvvm_news_app.R
import com.example.mvvm_news_app.api.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_breaking_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        doTest()

    }

    fun doTest(){
        CoroutineScope(IO).launch {
            val result = withContext(IO){
                RetrofitInstance.api.getBreakingNews()
            }
            results(result.body().toString())
        }
    }

    fun results(result: String){
        result
        Toast.makeText(this.context,"DOne",Toast.LENGTH_LONG).show()
    }

}