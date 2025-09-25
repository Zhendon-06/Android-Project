/**
 * 历史记录适配器
 *
 * 用途：展示最近播放的封面卡片与标题，横向列表滚动。
 * 结构：`fengmian_item` 布局，包含封面与标题。
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
 * 历史记录适配器
 * @property items 历史项列表
 */
class HistoryAdapter(
    private val items: List<HistoryItem>
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    /**
     * 单项数据
     * @property imageResId 封面资源
     * @property title 标题
     */
    data class HistoryItem(
        val imageResId: Int,
        val title: String
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fengmian_item, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.image.setImageResource(item.imageResId)
    }

    override fun getItemCount(): Int = items.size

    /** ViewHolder：缓存封面与标题引用。 */
    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.musicfengmian)
        val title: TextView = itemView.findViewById(R.id.musicname)
    }
}


