package com.guet.stu.banamusic.view.manager

import com.guet.stu.banamusic.R
import com.guet.stu.banamusic.view.activity.MusicPlayerFragment

/**
 * 音乐播放器管理器
 * 负责管理音乐播放器的显示、隐藏和动画
 */
class MusicPlayerManager(
    private val activity: android.app.Activity,
    private val fragmentManager: FragmentManager,
    private val navigationAnimator: NavigationAnimator
) {
    
    private var isMusicPlayerVisible = false
    
    /**
     * 显示音乐播放器
     */
    fun showMusicPlayer() {
        if (!isMusicPlayerVisible) {
            isMusicPlayerVisible = true
            fragmentManager.switchWithAnim(
                MusicPlayerFragment(), 
                R.anim.slide_up_enter, 
                R.anim.slide_up_exit
            )
            navigationAnimator.animateNav(visible = false)
        }
    }
    
    /**
     * 隐藏音乐播放器
     */
    fun hideMusicPlayer() {
        if (isMusicPlayerVisible) {
            isMusicPlayerVisible = false
            fragmentManager.switchWithAnim(
                fragmentManager.getLastPage(), 
                R.anim.slide_down_enter, 
                R.anim.slide_down_exit
            )
            navigationAnimator.animateNav(visible = true)
        }
    }
}

