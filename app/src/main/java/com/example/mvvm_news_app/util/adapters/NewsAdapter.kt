package com.example.mvvm_news_app.util.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mvvm_news_app.R
import com.example.mvvm_news_app.models.Article
import kotlinx.android.synthetic.main.item_article_preview.view.*

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_article_preview,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val item = differ.currentList[position]
        // apply to reference directly the data to the viewHolder
        holder.itemView.apply {
            Glide.with(this).load(item.urlToImage).into(ivArticleImage)
            tvSource.text = item.source.name
            tvTitle.text = item.title
            tvDescription.text = item.description
            tvPublishedAt.text = item.publishedAt
            setOnClickListener{
                onItemClickListener?.let {
                    it(item) // sets the action if not null
                }
            }
        }
    }

    //Lambda that receives a fun a returns nothing
    private var onItemClickListener : ((Article) -> Unit)? = null

    fun setItemClickListener(listener : (Article) -> Unit){
        onItemClickListener = listener
    }

    inner class ArticleViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
    }

    //Our anonymous object to check
    private val differCallBack = object : DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem // kotlin does object content and not referential equals so it's ok
        }
    }

    val differ = AsyncListDiffer(this,differCallBack)

}