/**
 * 专辑列表页面Activity
 * 
 * 功能描述：
 * - 显示专辑的详细信息（封面、标题、艺术家等）
 * - 提供专辑歌曲列表的展示
 * - 支持分享和播放专辑功能
 * - 实现自定义的透明模糊弹出菜单
 * 
 * 主要特性：
 * - 使用RecyclerView展示歌曲列表
 * - 自定义ActionBar固定在顶部
 * - 支持返回和菜单操作
 * - 透明模糊的弹出菜单效果
 * 
 * @author BanaMusic Team
 * @version 1.0
 * @since 2024
 */
package com.guet.stu.banamusic.view.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.guet.stu.banamusic.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.guet.stu.banamusic.view.adapter.AlbumSongsAdapter
import com.guet.stu.banamusic.BanaMusicApplication

/**
 * 专辑列表Activity类
 * 
 * 继承自AppCompatActivity，提供现代化的Activity功能
 * 实现了专辑详情展示和歌曲列表管理功能
 */
class  AlbumListActivity : AppCompatActivity() {
    /**
     * Activity创建时的回调方法
     * 
     * 执行流程：
     * 1. 启用边缘到边缘显示（沉浸式状态栏）
     * 2. 设置布局文件
     * 3. 处理系统栏的边距适配
     * 4. 初始化RecyclerView和歌曲列表
     * 5. 设置按钮点击事件监听器
     * 
     * @param savedInstanceState 保存的实例状态，用于Activity重建时恢复数据
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 启用边缘到边缘显示，实现沉浸式状态栏效果
        enableEdgeToEdge()
        
        // 设置Activity的布局文件
        setContentView(R.layout.activity_album_list)
        
        // 设置窗口边距监听器，处理系统栏（状态栏、导航栏）的适配
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { v, insets ->
            // 获取系统栏的边距信息
            val sysBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // 为根视图设置内边距，避免内容被系统栏遮挡
            v.setPadding(sysBars.left, sysBars.top, sysBars.right, sysBars.bottom)
            insets
        }

        // 初始化歌曲列表RecyclerView
        initSongList()
        
        // 设置ActionBar按钮的点击事件监听器
        setupClickListeners()
    }
    
    /**
     * 初始化歌曲列表RecyclerView
     * 
     * 功能：
     * - 设置线性布局管理器
     * - 创建演示数据（20首歌曲）
     * - 绑定适配器
     */
    private fun initSongList() {
        // 获取歌曲列表的RecyclerView
        val rv = findViewById<RecyclerView>(R.id.rv_songs)
        
        // 设置线性布局管理器，垂直方向排列
        rv.layoutManager = LinearLayoutManager(this)
        
        // 创建演示数据：生成1-20的歌曲列表
        val demo = (1..20).map {
            AlbumSongsAdapter.SongItem(
                R.drawable.music,  // 歌曲图标
                "歌曲$it",          // 歌曲名称
                "歌手$it"           // 歌手名称
            )
        }
        
        // 设置适配器，绑定数据到RecyclerView
        rv.adapter = AlbumSongsAdapter(demo)
    }
    
    /**
     * 设置ActionBar按钮的点击事件监听器
     * 
     * 功能：
     * - 设置返回按钮的点击事件
     * - 设置菜单按钮的点击事件
     * 
     * 按钮功能：
     * - 返回按钮：结束当前Activity，返回上一页面
     * - 菜单按钮：显示弹出菜单（分享、播放）
     */
    private fun setupClickListeners() {
        // 设置返回按钮的点击事件
        val backIcon = findViewById<ImageView>(R.id.back_ic)
        backIcon.setOnClickListener {
            // 结束当前Activity，返回上一个页面
            // 这会触发Activity的onDestroy()方法并返回到调用此Activity的页面
            finish()
        }
        
        // 设置菜单按钮的点击事件
        val menuIcon = findViewById<ImageView>(R.id.menu_ic)
        menuIcon.setOnClickListener { view ->
            // 显示弹出菜单，传入触发按钮的视图用于定位菜单位置
            showPopupMenu(view)
        }
    }
    
    /**
     * 显示弹出菜单
     * 
     * 功能：
     * - 创建PopupMenu实例
     * - 加载菜单资源文件
     * - 应用自定义透明模糊背景样式
     * - 设置菜单项点击事件监听器
     * - 显示菜单
     * 
     * 技术实现：
     * - 使用反射机制访问PopupMenu的内部ListPopupWindow
     * - 通过反射设置自定义背景drawable
     * - 异常处理确保反射失败时不影响基本功能
     * 
     * @param view 触发菜单的视图，用于定位菜单位置
     */
    private fun showPopupMenu(view: View) {
        // 创建PopupMenu实例，绑定到当前Activity和触发视图
        val popupMenu = PopupMenu(this, view)
        
        // 从菜单资源文件加载菜单项
        popupMenu.menuInflater.inflate(R.menu.album_menu, popupMenu.menu)
        
        // 应用自定义透明模糊背景样式
        try {
            // 使用反射获取PopupMenu内部的ListPopupWindow对象
            val field = popupMenu.javaClass.getDeclaredField("mPopup")
            field.isAccessible = true
            val popup = field.get(popupMenu) as android.widget.ListPopupWindow
            
            // 设置自定义背景drawable（透明模糊效果）
            popup.setBackgroundDrawable(resources.getDrawable(R.drawable.popup_menu_with_shadow, null))
            
        } catch (e: Exception) {
            // 反射失败时的异常处理，打印错误信息但不影响基本功能
            e.printStackTrace()
        }
        
        // 设置菜单项点击事件监听器
        popupMenu.setOnMenuItemClickListener { item ->
            // 根据菜单项的ID执行相应的操作
            when (item.itemId) {
                R.id.action_share -> {
                    // TODO: 实现分享专辑功能
                    // 可以分享专辑链接、封面图片等信息到其他应用
                    true
                }
                R.id.action_play -> {
                    // TODO: 实现播放专辑功能
                    // 可以开始播放专辑中的所有歌曲
                    true
                }
                else -> false
            }
        }
        
        // 显示弹出菜单
        popupMenu.show()
    }

}