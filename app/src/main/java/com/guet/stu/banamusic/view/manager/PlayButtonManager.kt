package com.guet.stu.banamusic.view.manager

import android.view.View
import android.widget.ImageView
import com.guet.stu.banamusic.R
import com.guet.stu.banamusic.BanaMusicApplication

/**
 * 播放按钮管理器
 * 负责管理所有播放按钮的状态同步和图标切换
 * 
 * 更新说明：
 * - 移除了本地的 isPlaying 变量
 * - 改为使用 BanaMusicApplication 中的全局播放状态
 * - 确保所有Activity中的播放状态保持同步
 * - 所有播放操作方法都集中在此类中
 */
class PlayButtonManager {
    
    private var floatingNavigationContainer: View? = null
    
    /**
     * 设置浮动导航容器引用
     */
    fun setFloatingNavigationContainer(container: View) {
        this.floatingNavigationContainer = container
    }
    
    /**
     * 获取当前播放状态
     * 从全局Application中获取播放状态
     */
    fun isPlaying(): Boolean = BanaMusicApplication.getInstance().isPlaying

    /**
     * 切换播放/暂停状态（包括Activity中的按钮）
     * @param activityView Activity的根视图
     */
    fun togglePlayPause(activityView: View) {
        // 使用全局Application切换播放状态
        val app = BanaMusicApplication.getInstance()
        app.setPlaying(!app.isPlaying)
        syncAllPlayButtonIcons(activityView)
    }
    
    /**
     * 同步所有可见的播放按钮图标状态
     */
    fun syncAllPlayButtonIcons() {
        val isPlaying = BanaMusicApplication.getInstance().isPlaying
        val iconRes = if (isPlaying) R.drawable.stop else R.drawable.play
        // 更新浮动导航容器中的播放按钮（如果存在）
        floatingNavigationContainer?.findViewById<ImageView>(R.id.iv_play)?.setImageResource(iconRes)
    }
    
    /**
     * 同步所有播放按钮图标状态（包括Activity中的按钮）
     * @param activityView Activity的根视图
     */
    fun syncAllPlayButtonIcons(activityView: View) {
        val isPlaying = BanaMusicApplication.getInstance().isPlaying
        val iconRes = if (isPlaying) R.drawable.stop else R.drawable.play
        // 更新Activity中的播放按钮
        activityView.findViewById<ImageView>(R.id.iv_play)?.setImageResource(iconRes)
        // 更新浮动导航容器中的播放按钮（如果存在）
        floatingNavigationContainer?.findViewById<ImageView>(R.id.iv_play)?.setImageResource(iconRes)
    }
    
    /**
     * 更新当前Activity中的播放按钮图标
     * @param activityView Activity的根视图
     */
    fun updateCurrentPlayButton(activityView: View) {
        val isPlaying = BanaMusicApplication.getInstance().isPlaying
        val iconRes = if (isPlaying) R.drawable.stop else R.drawable.play
        activityView.findViewById<ImageView>(R.id.iv_play)?.setImageResource(iconRes)
    }
    
    /**
     * 初始化播放按钮状态
     * @param activityView Activity的根视图
     */
    fun initializePlayButton(activityView: View) {
        updateCurrentPlayButton(activityView)
        syncAllPlayButtonIcons()
    }
    
}
