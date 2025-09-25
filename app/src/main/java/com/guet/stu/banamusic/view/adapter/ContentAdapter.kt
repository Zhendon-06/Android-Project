/**
 * 网格内容适配器
 *
 * 用途：用于展示音乐封面网格（如“音乐/歌手”宫格）。
 * 结构：使用 `content_item` 布局，包含封面、歌曲名、歌手名。
 */
package com.guet.stu.banamusic.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.guet.stu.banamusic.R

/**
 * 网格内容适配器
 *
 * @property items 数据列表
 */
class ContentAdapter(
    private val items: List<ContentItem>
) : RecyclerView.Adapter<ContentAdapter.ContentViewHolder>() {

    /**
     * 单项数据
     * @property imageResId 封面资源
     * @property song 歌曲名
     * @property artist 歌手名
     */
    data class ContentItem(
        val imageResId: Int,
        val song: String,
        val artist: String
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.content_item, parent, false)
        return ContentViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        val item = items[position]
        holder.cover.setImageResource(item.imageResId)
        holder.song.text = item.song
        holder.artist.text = item.artist
    }

    override fun getItemCount(): Int = items.size

    /**
     * ViewHolder：缓存封面与文本引用，减少 findViewById 成本。
     */
    class ContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cover: ImageView = itemView.findViewById(R.id.card_image)
        val song: TextView = itemView.findViewById(R.id.music_song)
        val artist: TextView = itemView.findViewById(R.id.music_artist)
    }
}


