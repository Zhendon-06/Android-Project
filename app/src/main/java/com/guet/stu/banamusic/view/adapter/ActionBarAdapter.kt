/**
 * 推荐内容栏适配器
 *
 * 用途：用于首页顶部横向推荐栏，展示封面卡片与标题，并回调点击事件。
 * 结构：使用 `action_bar_item` 作为单项布局，包含图片与标题。
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
 * 推荐内容适配器
 *
 * @property items 数据列表
 * @property onItemClick 单项点击回调
 */
class ActionBarAdapter(
    private val items: List<ActionBarItem>,
    private val onItemClick: (ActionBarItem) -> Unit
)
    : RecyclerView.Adapter<ActionBarAdapter.ActionBarViewHolder>() {

    /**
     * 推荐项数据结构
     *
     * @property imageResId 封面资源ID
     * @property title 标题文本
     */
    data class ActionBarItem(
        val imageResId: Int,
        val title: String
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActionBarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.action_bar_item, parent, false)
        return ActionBarViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActionBarViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.image.setImageResource(item.imageResId)
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount(): Int = items.size

    /**
     * ViewHolder：缓存并暴露单项中的视图引用，避免重复查找。
     */
    class ActionBarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.card_image)
        val title: TextView = itemView.findViewById(R.id.action_bar_title_name)
    }
}


