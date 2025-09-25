/**
 * 搜索页面Activity - 音乐搜索功能
 * 
 * 功能描述：
 * - 提供音乐搜索功能的主界面
 * - 显示搜索分类、热门搜索、推荐内容
 * - 支持多种布局展示不同类型的内容
 * - 提供返回功能和沉浸式状态栏
 * 
 * 页面结构：
 * 1. 搜索分类：2列网格布局展示音乐分类
 * 2. 热门搜索：垂直列表展示热门搜索词
 * 3. 推荐内容：水平滚动展示推荐歌单/专辑
 * 
 * 技术特点：
 * - 使用多种RecyclerView布局管理器
 * - 自定义适配器处理不同类型的数据
 * - 响应式设计，适配不同屏幕尺寸
 * 
 * @author BanaMusic Team
 * @version 1.0
 * @since 2024
 */
package com.guet.stu.banamusic.view.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.guet.stu.banamusic.R
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * 搜索页面Activity类
 * 
 * 继承自AppCompatActivity，提供搜索功能
 * 包含多个RecyclerView展示不同类型的搜索内容
 */
class SearchPage : AppCompatActivity() {
    /**
     * Activity创建时的回调方法
     * 
     * 执行流程：
     * 1. 启用边缘到边缘显示（沉浸式状态栏）
     * 2. 设置搜索页面布局文件
     * 3. 处理系统栏的边距适配
     * 4. 设置返回按钮点击事件
     * 5. 初始化三个RecyclerView及其适配器
     * 
     * @param savedInstanceState 保存的实例状态，用于Activity重建时恢复数据
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 启用边缘到边缘显示，实现沉浸式状态栏效果
        enableEdgeToEdge()
        
        // 设置搜索页面的布局文件
        setContentView(R.layout.activity_search_page)
        
        // 设置窗口边距监听器，处理系统栏（状态栏、导航栏）的适配
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            // 获取系统栏的边距信息
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // 为根视图设置内边距，避免内容被系统栏遮挡
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 设置返回按钮的点击事件
        findViewById<android.widget.ImageButton>(R.id.btn_back).setOnClickListener {
            // 结束当前Activity，返回上一个页面
            finish()
        }

        // ==================== 搜索分类RecyclerView ====================
        // 使用2列网格布局展示音乐分类
        findViewById<RecyclerView>(R.id.rv_categories).apply {
            // 设置2列网格布局管理器
            layoutManager = GridLayoutManager(this@SearchPage, 2)
            // 设置适配器，展示音乐分类数据
            adapter = SimpleTextAdapter(listOf("华语", "流行", "摇滚", "电子", "古典", "民谣"))
        }

        // ==================== 热门搜索RecyclerView ====================
        // 使用垂直列表布局展示热门搜索词
        findViewById<RecyclerView>(R.id.rv_trending).apply {
            // 设置垂直线性布局管理器
            layoutManager = LinearLayoutManager(this@SearchPage)
            // 设置适配器，展示热门搜索数据
            adapter = SimpleTextAdapter(listOf("周杰伦", "Taylor Swift", "热歌榜", "NewJeans", "黑胶热播"))
        }

        // ==================== 推荐内容RecyclerView ====================
        // 使用水平滚动布局展示推荐内容
        findViewById<RecyclerView>(R.id.rv_recommended).apply {
            // 设置水平线性布局管理器
            layoutManager = LinearLayoutManager(this@SearchPage, LinearLayoutManager.HORIZONTAL, false)
            // 设置适配器，展示推荐内容数据
            adapter = SimpleCardAdapter(listOf("每日音乐推荐", "编辑精选", "为你打造的歌单", "最新发行"))
        }
    }
}

/**
 * 简单文本适配器
 * 
 * 功能：为RecyclerView提供简单的文本列表显示
 * 用途：用于搜索分类和热门搜索的显示
 * 特点：使用系统默认的简单列表项布局
 */
class SimpleTextAdapter(private val items: List<String>) : RecyclerView.Adapter<SimpleTextViewHolder>() {
    
    /**
     * 创建ViewHolder
     * 
     * @param parent 父容器
     * @param viewType 视图类型
     * @return 创建的ViewHolder
     */
    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): SimpleTextViewHolder {
        // 使用系统默认的简单列表项布局
        val view = layoutInflater(parent).inflate(android.R.layout.simple_list_item_1, parent, false)
        return SimpleTextViewHolder(view)
    }
    
    /**
     * 绑定数据到ViewHolder
     * 
     * @param holder ViewHolder实例
     * @param position 数据位置
     */
    override fun onBindViewHolder(holder: SimpleTextViewHolder, position: Int) { 
        holder.bind(items[position]) 
    }
    
    /**
     * 获取数据项数量
     * 
     * @return 数据项总数
     */
    override fun getItemCount(): Int = items.size
    
    /**
     * 获取布局填充器
     * 
     * @param parent 父容器
     * @return LayoutInflater实例
     */
    private fun layoutInflater(parent: android.view.ViewGroup) = android.view.LayoutInflater.from(parent.context)
}

/**
 * 简单文本ViewHolder
 * 
 * 功能：持有简单文本视图的引用
 * 用途：配合SimpleTextAdapter使用
 */
class SimpleTextViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
    /** 文本视图引用 */
    private val tv: android.widget.TextView = itemView.findViewById(android.R.id.text1)
    
    /**
     * 绑定文本数据
     * 
     * @param text 要显示的文本
     */
    fun bind(text: String) { 
        tv.text = text 
    }
}

/**
 * 简单卡片适配器
 * 
 * 功能：为RecyclerView提供卡片样式的显示
 * 用途：用于推荐内容的显示
 * 特点：使用自定义的action_bar_item布局
 */
class SimpleCardAdapter(private val items: List<String>) : RecyclerView.Adapter<SimpleCardViewHolder>() {
    
    /**
     * 创建ViewHolder
     * 
     * @param parent 父容器
     * @param viewType 视图类型
     * @return 创建的ViewHolder
     */
    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): SimpleCardViewHolder {
        // 使用自定义的action_bar_item布局
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.action_bar_item, parent, false)
        return SimpleCardViewHolder(view)
    }
    
    /**
     * 绑定数据到ViewHolder
     * 
     * @param holder ViewHolder实例
     * @param position 数据位置
     */
    override fun onBindViewHolder(holder: SimpleCardViewHolder, position: Int) { 
        holder.bind(items[position]) 
    }
    
    /**
     * 获取数据项数量
     * 
     * @return 数据项总数
     */
    override fun getItemCount(): Int = items.size
}

/**
 * 简单卡片ViewHolder
 * 
 * 功能：持有卡片视图的引用
 * 用途：配合SimpleCardAdapter使用
 */
class SimpleCardViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
    /** 标题文本视图引用 */
    private val title: android.widget.TextView = itemView.findViewById(R.id.action_bar_title_name)
    
    /**
     * 绑定文本数据
     * 
     * @param text 要显示的文本
     */
    fun bind(text: String) { 
        title.text = text 
    }
}