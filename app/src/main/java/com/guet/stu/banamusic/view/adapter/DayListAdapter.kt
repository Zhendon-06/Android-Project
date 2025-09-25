/**
 * 每日推荐适配器
 *
 * 用途：首页“每日推荐”区域，水平多行网格滚动展示内容。
 * 结构：`title_recyclerview_item` 布局，包含封面、歌曲、歌手。
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
 * 每日推荐适配器
 * @property items 数据列表
 */
class DayListAdapter(
    private val items: List<DayItem>
) : RecyclerView.Adapter<DayListAdapter.DayViewHolder>() {

    /**
     * 单项数据
     * @property coverResId 封面资源
     * @property songTitle 歌曲名
     * @property singer 歌手名
     */
    data class DayItem(
        val coverResId: Int,
        val songTitle: String,
        val singer: String
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.title_recyclerview_item, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val item = items[position]
        holder.song.text = item.songTitle
        holder.singer.text = item.singer
        holder.cover.setImageResource(item.coverResId)
    }

    override fun getItemCount(): Int = items.size

    /** ViewHolder：持有封面和文本引用。 */
    class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cover: ImageView = itemView.findViewById(R.id.music_item_pic)
        val song: TextView = itemView.findViewById(R.id.music_item_song)
        val singer: TextView = itemView.findViewById(R.id.music_item_sing)
    }
}


