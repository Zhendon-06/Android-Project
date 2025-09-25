package com.guet.stu.banamusic.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.guet.stu.banamusic.R

/**
 * 歌词适配器
 */
class LyricAdapter(
    private val lyrics: List<String>,
    private val onItemClickListener: (() -> Unit)? = null
) : RecyclerView.Adapter<LyricAdapter.LyricViewHolder>() {

    class LyricViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvLyricLine: TextView = itemView.findViewById(R.id.tv_lyric_line)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LyricViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lyric, parent, false)
        return LyricViewHolder(view)
    }

    override fun onBindViewHolder(holder: LyricViewHolder, position: Int) {
        holder.tvLyricLine.text = lyrics[position]
        
        // 设置点击监听器
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke()
        }
    }

    override fun getItemCount(): Int = lyrics.size
}
