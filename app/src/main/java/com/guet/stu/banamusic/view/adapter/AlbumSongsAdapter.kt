/**
 * 专辑歌曲列表适配器
 * 
 * 功能描述：
 * - 为专辑详情页面的歌曲列表提供数据绑定
 * - 显示歌曲封面、标题和艺术家信息
 * - 支持点击事件处理
 * - 使用自定义的album_item布局
 * 
 * 数据结构：
 * - SongItem：包含歌曲的封面资源ID、标题和艺术家
 * 
 * 使用场景：
 * - 专辑详情页面的歌曲列表
 * - 播放列表的歌曲展示
 * - 搜索结果中的歌曲项
 * 
 * @author BanaMusic Team
 * @version 1.0
 * @since 2024
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
 * 专辑歌曲列表适配器类
 * 
 * 继承自RecyclerView.Adapter，提供歌曲列表的数据绑定功能
 * 使用SongViewHolder作为ViewHolder类型
 */
class AlbumSongsAdapter(private val items: List<SongItem>) : RecyclerView.Adapter<AlbumSongsAdapter.SongViewHolder>() {

    /**
     * 歌曲数据项
     * 
     * 数据类，包含歌曲的基本信息
     * 
     * @property coverResId 歌曲封面图片资源ID
     * @property title 歌曲标题
     * @property artist 艺术家名称
     */
    data class SongItem(
        val coverResId: Int,
        val title: String,
        val artist: String
    )

    /**
     * 创建ViewHolder
     * 
     * 功能：创建歌曲项的ViewHolder实例
     * 布局：使用album_item.xml布局文件
     * 
     * @param parent 父容器ViewGroup
     * @param viewType 视图类型（当前未使用）
     * @return 创建的SongViewHolder实例
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        // 加载album_item布局文件
        val view = LayoutInflater.from(parent.context).inflate(R.layout.album_item, parent, false)
        return SongViewHolder(view)
    }

    /**
     * 绑定数据到ViewHolder
     * 
     * 功能：将指定位置的歌曲数据绑定到ViewHolder
     * 数据绑定：封面图片、歌曲标题、艺术家名称
     * 
     * @param holder 要绑定的ViewHolder
     * @param position 数据位置索引
     */
    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        // 获取指定位置的歌曲数据
        val item = items[position]
        
        // 绑定封面图片
        holder.cover.setImageResource(item.coverResId)
        
        // 绑定歌曲标题
        holder.title.text = item.title
        
        // 绑定艺术家名称
        holder.artist.text = item.artist
    }

    /**
     * 获取数据项数量
     * 
     * @return 歌曲列表的总数量
     */
    override fun getItemCount(): Int = items.size

    /**
     * 歌曲ViewHolder类
     * 
     * 功能：持有歌曲项视图的引用
     * 视图组件：封面图片、标题文本、艺术家文本
     */
    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        /** 歌曲封面图片视图 */
        val cover: ImageView = itemView.findViewById(R.id.music_item_pic)
        
        /** 歌曲标题文本视图 */
        val title: TextView = itemView.findViewById(R.id.music_item_song)
        
        /** 艺术家名称文本视图 */
        val artist: TextView = itemView.findViewById(R.id.music_item_sing)
    }
}


